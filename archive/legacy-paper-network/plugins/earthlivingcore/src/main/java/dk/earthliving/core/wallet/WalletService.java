package dk.earthliving.core.wallet;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class WalletService {
    public static final String MENU_TITLE = "EarthOS Wallet";

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final File dataFile;
    private FileConfiguration data;

    public WalletService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.dataFile = new File(plugin.getDataFolder(), "wallets.yml");
        load();
    }

    public void recordJoin(Player player) {
        ensureWallet(player.getUniqueId(), player.getName());
        save();
    }

    public void open(Player player) {
        ensureWallet(player.getUniqueId(), player.getName());
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        inventory.setItem(11, item(Material.EMERALD, "&aWallet", List.of(
                "&7Balance: &f" + format(balance(player.getUniqueId())),
                "&7Currency: &f" + currencyName(),
                "&8VS2: simple balance only"
        )));
        inventory.setItem(13, item(Material.GOLD_NUGGET, "&eHow to earn", List.of(
                "&7Mine, farm or fish on Main.",
                "&7Small rewards are paid automatically.",
                "&8TODO VS3: banking, shops and transfers."
        )));
        inventory.setItem(15, item(Material.BARRIER, "&cClose", List.of("&7Return to the game.")));
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot) {
        if (slot == 15) {
            player.closeInventory();
        }
    }

    public double balance(UUID uuid) {
        ensureWallet(uuid, "Unknown");
        return data.getDouble(path(uuid) + ".balance", startBalance());
    }

    public double add(UUID uuid, String playerName, double amount, String reason) {
        ensureWallet(uuid, playerName);
        double next = Math.max(0.0D, balance(uuid) + amount);
        data.set(path(uuid) + ".balance", round(next));
        data.set(path(uuid) + ".player-name", playerName);
        data.set(path(uuid) + ".last-reason", reason == null ? "" : reason);
        save();
        return next;
    }

    public double set(UUID uuid, String playerName, double amount) {
        ensureWallet(uuid, playerName);
        double next = Math.max(0.0D, amount);
        data.set(path(uuid) + ".balance", round(next));
        data.set(path(uuid) + ".player-name", playerName);
        save();
        return next;
    }

    public String format(double amount) {
        return currencySymbol() + String.format("%.2f", amount);
    }

    public String currencyName() {
        return plugin.getConfig().getString("wallet.currency-name", "EarthCoins");
    }

    private String currencySymbol() {
        return plugin.getConfig().getString("wallet.currency-symbol", "EC ");
    }

    private double startBalance() {
        return plugin.getConfig().getDouble("wallet.start-balance", 0.0D);
    }

    private void ensureWallet(UUID uuid, String playerName) {
        String path = path(uuid);
        if (!data.contains(path + ".balance")) {
            data.set(path + ".balance", round(startBalance()));
        }
        if (data.getString(path + ".player-name", "").isBlank()) {
            data.set(path + ".player-name", playerName);
        }
    }

    private String path(UUID uuid) {
        return "players." + uuid;
    }

    private double round(double value) {
        return Math.round(value * 100.0D) / 100.0D;
    }

    private void load() {
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void save() {
        try {
            data.save(dataFile);
        } catch (IOException exception) {
            notifications.console("Could not save wallets.yml: " + exception.getMessage());
        }
    }

    public UUID resolvePlayerUuid(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return player.getUniqueId();
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
