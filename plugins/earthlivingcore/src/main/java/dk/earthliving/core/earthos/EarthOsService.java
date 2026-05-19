package dk.earthliving.core.earthos;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class EarthOsService {
    public static final String MENU_TITLE = "EarthOS";

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final NamespacedKey itemKey;

    public EarthOsService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.itemKey = new NamespacedKey(plugin, "earthos_device");
    }

    public boolean enabled() {
        return plugin.getConfig().getBoolean("earthos.enabled", true);
    }

    public boolean shouldGiveOnJoin() {
        return enabled() && plugin.getConfig().getBoolean("earthos.give-on-join", true);
    }

    public void giveDevice(Player player) {
        if (!enabled()) {
            return;
        }

        ItemStack device = createDevice();
        int slot = Math.max(0, Math.min(8, plugin.getConfig().getInt("earthos.hotbar-slot", 8)));

        ItemStack existing = player.getInventory().getItem(slot);
        if (existing == null || existing.getType().isAir() || isDevice(existing)) {
            player.getInventory().setItem(slot, device);
            return;
        }

        if (!player.getInventory().containsAtLeast(device, 1)) {
            player.getInventory().addItem(device);
        }
    }

    public boolean isDevice(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        Byte value = item.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }

    public void open(Player player) {
        if (!enabled()) {
            notifications.send(player, "&cEarthOS is disabled.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        inventory.setItem(10, menuItem(Material.MAP, "&bWorld Map", List.of("&7BlueMap and country overview")));
        inventory.setItem(11, menuItem(Material.CLOCK, "&eServer Events", List.of("&7Random events and competitions")));
        inventory.setItem(12, menuItem(Material.PAPER, "&6Passport", List.of("&7Countries, access and travel")));
        inventory.setItem(13, menuItem(Material.EMERALD, "&aWallet", List.of("&7Economy placeholder")));
        inventory.setItem(14, menuItem(Material.WRITABLE_BOOK, "&dReports", List.of("&7Support and bug reports")));
        inventory.setItem(15, menuItem(Material.REDSTONE_TORCH, "&cServer Status", List.of("&7Status, maintenance and updates")));
        inventory.setItem(16, menuItem(Material.COMPARATOR, "&fSettings", List.of("&7Player preferences")));
        player.openInventory(inventory);
    }

    private ItemStack createDevice() {
        FileConfiguration config = plugin.getConfig();
        Material material = Material.matchMaterial(config.getString("earthos.material", "COMPASS"));
        if (material == null) {
            material = Material.COMPASS;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(config.getString("earthos.display-name", "&6EarthOS")));
        meta.setLore(colorList(config.getStringList("earthos.lore")));
        meta.setCustomModelData(config.getInt("earthos.custom-model-data", 260519));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack menuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(name));
        meta.setLore(colorList(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private List<String> colorList(List<String> values) {
        List<String> colored = new ArrayList<>();
        for (String value : values) {
            colored.add(notifications.color(value));
        }
        return colored;
    }
}
