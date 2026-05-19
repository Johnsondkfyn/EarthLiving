package dk.earthliving.core.report;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.time.Instant;
import java.util.List;
import java.util.Locale;

public final class ReportService {
    public static final String MENU_TITLE = "EarthOS Reports";

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final File reportsFile;
    private FileConfiguration reports;
    private int nextId;

    public ReportService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.reportsFile = new File(plugin.getDataFolder(), "reports.yml");
        load();
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        int slot = 10;
        for (ReportCategory category : ReportCategory.values()) {
            inventory.setItem(slot, categoryItem(category));
            slot++;
            if (slot == 13) {
                slot = 14;
            }
        }
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int rawSlot) {
        ReportCategory[] categories = ReportCategory.values();
        int[] slots = {10, 11, 12, 14, 15, 16};
        for (int index = 0; index < slots.length; index++) {
            if (rawSlot == slots[index] && index < categories.length) {
                create(player, categories[index]);
                return;
            }
        }
    }

    public void create(Player player, ReportCategory category) {
        int id = nextId++;
        reports.set("next-id", nextId);

        Location location = player.getLocation();
        String path = "reports." + id;
        reports.set(path + ".id", id);
        reports.set(path + ".status", "open");
        reports.set(path + ".category", category.id());
        reports.set(path + ".category-title", category.title());
        reports.set(path + ".player-name", player.getName());
        reports.set(path + ".player-uuid", player.getUniqueId().toString());
        reports.set(path + ".world", location.getWorld() == null ? "unknown" : location.getWorld().getName());
        reports.set(path + ".x", location.getBlockX());
        reports.set(path + ".y", location.getBlockY());
        reports.set(path + ".z", location.getBlockZ());
        reports.set(path + ".created-at", Instant.now().toString());
        reports.set(path + ".note", "Quick report created from EarthOS. Text input will be added in a later version.");
        save();

        player.closeInventory();
        notifications.send(player, "&aReport #" + id + " created: &f" + category.title());
        notifications.send(player, "&7Location saved: &f" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        notifications.console("Report #" + id + " created by " + player.getName() + " (" + category.id() + ").");
    }

    public int openReportCount() {
        if (!reports.isConfigurationSection("reports")) {
            return 0;
        }

        int count = 0;
        for (String id : reports.getConfigurationSection("reports").getKeys(false)) {
            if ("open".equalsIgnoreCase(reports.getString("reports." + id + ".status", ""))) {
                count++;
            }
        }
        return count;
    }

    private void load() {
        if (!reportsFile.exists()) {
            reportsFile.getParentFile().mkdirs();
        }
        reports = YamlConfiguration.loadConfiguration(reportsFile);
        nextId = Math.max(1, reports.getInt("next-id", 1));
    }

    private void save() {
        try {
            reports.save(reportsFile);
        } catch (IOException exception) {
            notifications.console("Could not save reports.yml: " + exception.getMessage());
        }
    }

    private ItemStack categoryItem(ReportCategory category) {
        ItemStack item = new ItemStack(category.icon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color("&d" + category.title()));
        meta.setLore(List.of(
                notifications.color("&7Create a quick report."),
                notifications.color("&7Your location will be saved."),
                notifications.color("&8Category: " + category.id().toUpperCase(Locale.ROOT))
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
