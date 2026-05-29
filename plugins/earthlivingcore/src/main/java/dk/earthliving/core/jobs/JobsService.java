package dk.earthliving.core.jobs;

import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.wallet.WalletService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class JobsService implements Listener {
    public static final String MENU_TITLE = "EarthOS Jobs";

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final WalletService walletService;
    private final Map<String, Long> cooldowns = new HashMap<>();

    public JobsService(JavaPlugin plugin, NotificationService notifications, WalletService walletService) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.walletService = walletService;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        inventory.setItem(10, item(Material.IRON_PICKAXE, "&7Mining", List.of(
                "&7Earn from basic mining blocks.",
                "&7Cooldown: &f" + cooldownSeconds("mining") + "s",
                "&8No leveling in VS2."
        )));
        inventory.setItem(12, item(Material.WHEAT, "&eFarming", List.of(
                "&7Earn from mature crops.",
                "&7Cooldown: &f" + cooldownSeconds("farming") + "s"
        )));
        inventory.setItem(14, item(Material.FISHING_ROD, "&bFishing", List.of(
                "&7Earn when catching fish.",
                "&7Cooldown: &f" + cooldownSeconds("fishing") + "s"
        )));
        inventory.setItem(16, item(Material.EMERALD, "&aBalance", List.of(
                "&7Current balance: &f" + walletService.format(walletService.balance(player.getUniqueId()))
        )));
        inventory.setItem(22, item(Material.BARRIER, "&cClose", List.of("&7Jobs VS2 is simple rewards only.")));
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot) {
        if (slot == 22) {
            player.closeInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        if (isConfiguredMaterial("mining", material)) {
            reward(player, "mining", rewardAmount("mining", material), material.name().toLowerCase(Locale.ROOT));
            return;
        }
        if (isConfiguredMaterial("farming", material) && isMatureCrop(block)) {
            reward(player, "farming", rewardAmount("farming", material), material.name().toLowerCase(Locale.ROOT));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        reward(event.getPlayer(), "fishing", plugin.getConfig().getDouble("jobs.rewards.fishing.default", 1.0D), "fish");
    }

    private void reward(Player player, String job, double amount, String reason) {
        if (!plugin.getConfig().getBoolean("jobs.enabled", true) || amount <= 0.0D) {
            return;
        }
        if (!player.hasPermission("earthliving.jobs")) {
            return;
        }
        if (coolingDown(player.getUniqueId(), job)) {
            return;
        }
        double balance = walletService.add(player.getUniqueId(), player.getName(), amount, job + ":" + reason);
        if (plugin.getConfig().getBoolean("jobs.show-reward-message", true)) {
            String message = plugin.getConfig().getString("jobs.messages.reward", "&a+{amount} from {job}. Balance: {balance}");
            notifications.send(player, message
                    .replace("{amount}", walletService.format(amount))
                    .replace("{balance}", walletService.format(balance))
                    .replace("{job}", job));
        }
    }

    private boolean coolingDown(UUID uuid, String job) {
        String key = uuid + ":" + job;
        long now = System.currentTimeMillis();
        long next = cooldowns.getOrDefault(key, 0L);
        if (now < next) {
            return true;
        }
        cooldowns.put(key, now + cooldownSeconds(job) * 1000L);
        return false;
    }

    private int cooldownSeconds(String job) {
        return Math.max(0, plugin.getConfig().getInt("jobs.cooldowns." + job, 3));
    }

    private boolean isConfiguredMaterial(String job, Material material) {
        return plugin.getConfig().getConfigurationSection("jobs.rewards." + job + ".blocks") != null
                && plugin.getConfig().contains("jobs.rewards." + job + ".blocks." + material.name());
    }

    private double rewardAmount(String job, Material material) {
        return plugin.getConfig().getDouble("jobs.rewards." + job + ".blocks." + material.name(), 0.0D);
    }

    private boolean isMatureCrop(Block block) {
        return block.getBlockData() instanceof Ageable ageable && ageable.getAge() >= ageable.getMaximumAge();
    }

    private ItemStack item(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(name));
        List<String> colored = new ArrayList<>();
        for (String line : lore) {
            colored.add(notifications.color(line));
        }
        meta.setLore(colored);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
