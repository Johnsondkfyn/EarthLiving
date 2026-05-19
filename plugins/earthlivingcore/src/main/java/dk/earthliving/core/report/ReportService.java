package dk.earthliving.core.report;

import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.notification.DiscordNotificationService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ReportService {
    public static final String MENU_TITLE = "EarthOS Reports";
    public static final String CREATE_MENU_TITLE = "EarthOS Create Report";
    public static final String MY_REPORTS_TITLE = "EarthOS My Reports";
    public static final String ADMIN_REPORTS_TITLE = "EarthOS Admin Reports";

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final DiscordNotificationService discordNotifications;
    private final File reportsFile;
    private final File panelReportsFile;
    private final Map<UUID, ReportCategory> pendingReports = new ConcurrentHashMap<>();
    private FileConfiguration reports;
    private int nextId;

    public ReportService(JavaPlugin plugin, NotificationService notifications, DiscordNotificationService discordNotifications) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.discordNotifications = discordNotifications;
        this.reportsFile = new File(plugin.getDataFolder(), "reports.yml");
        this.panelReportsFile = new File(plugin.getDataFolder(), "reports-panel.json");
        load();
        exportPanelReports();
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        inventory.setItem(11, menuItem(Material.WRITABLE_BOOK, "&dCreate Report", List.of(
                "&7Choose a category and write a note.",
                "&7Your location will be saved."
        )));
        inventory.setItem(13, menuItem(Material.BOOK, "&bMy Reports", List.of(
                "&7View your submitted reports."
        )));
        if (player.hasPermission("earthliving.admin")) {
            inventory.setItem(15, menuItem(Material.ENDER_EYE, "&6Admin Reports", List.of(
                    "&7View latest server reports.",
                    "&7Open reports: &f" + openReportCount()
            )));
        }
        player.openInventory(inventory);
    }

    public void openCreate(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, CREATE_MENU_TITLE);
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

    public void openMyReports(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, MY_REPORTS_TITLE);
        List<Integer> ids = reportIds().stream()
                .filter(id -> player.getUniqueId().toString().equals(reports.getString("reports." + id + ".player-uuid")))
                .limit(45)
                .toList();
        fillReportList(inventory, ids);
        player.openInventory(inventory);
    }

    public void openAdminReports(Player player) {
        if (!player.hasPermission("earthliving.admin")) {
            notifications.send(player, "&cYou do not have permission to view admin reports.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(player, 54, ADMIN_REPORTS_TITLE);
        fillReportList(inventory, reportIds().stream().limit(45).toList());
        player.openInventory(inventory);
    }

    public void handleHubClick(Player player, int rawSlot) {
        if (rawSlot == 11) {
            openCreate(player);
        } else if (rawSlot == 13) {
            openMyReports(player);
        } else if (rawSlot == 15) {
            openAdminReports(player);
        }
    }

    public void handleCreateClick(Player player, int rawSlot) {
        ReportCategory[] categories = ReportCategory.values();
        int[] slots = {10, 11, 12, 14, 15, 16};
        for (int index = 0; index < slots.length; index++) {
            if (rawSlot == slots[index] && index < categories.length) {
                beginDraft(player, categories[index]);
                return;
            }
        }
    }

    public void handleReportListClick(Player player, int rawSlot) {
        ItemStack item = player.getOpenInventory().getTopInventory().getItem(rawSlot);
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();
        String stripped = org.bukkit.ChatColor.stripColor(displayName);
        if (stripped == null || !stripped.startsWith("Report #")) {
            return;
        }

        String id = stripped.substring("Report #".length()).split(" ")[0];
        sendReportDetails(player, id);
        player.closeInventory();
    }

    public boolean hasPendingReport(Player player) {
        return pendingReports.containsKey(player.getUniqueId());
    }

    public void submitPending(Player player, String message) {
        ReportCategory category = pendingReports.remove(player.getUniqueId());
        if (category == null) {
            return;
        }

        if ("cancel".equalsIgnoreCase(message.trim())) {
            notifications.send(player, "&eReport cancelled.");
            return;
        }

        create(player, category, message.trim());
    }

    private void beginDraft(Player player, ReportCategory category) {
        pendingReports.put(player.getUniqueId(), category);
        player.closeInventory();
        notifications.send(player, "&dCreating report: &f" + category.title());
        notifications.send(player, "&7Write your note in chat now. Type &fcancel &7to stop.");
        notifications.send(player, "&8Your next chat message will not be sent publicly.");
    }

    private void create(Player player, ReportCategory category, String note) {
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
        reports.set(path + ".note", note.isBlank() ? "No note provided." : note);
        save();

        player.closeInventory();
        notifications.send(player, "&aReport #" + id + " created: &f" + category.title());
        notifications.send(player, "&7Location saved: &f" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        notifications.console("Report #" + id + " created by " + player.getName() + " (" + category.id() + ").");
        discordNotifications.reportCreated(
                id,
                category.title(),
                player.getName(),
                location.getWorld() == null ? "unknown" : location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                note.isBlank() ? "No note provided." : note
        );
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
            exportPanelReports();
        } catch (IOException exception) {
            notifications.console("Could not save reports.yml: " + exception.getMessage());
        }
    }

    private void exportPanelReports() {
        try {
            panelReportsFile.getParentFile().mkdirs();
            java.nio.file.Files.writeString(panelReportsFile.toPath(), panelJson());
        } catch (IOException exception) {
            notifications.console("Could not export reports-panel.json: " + exception.getMessage());
        }
    }

    private String panelJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(jsonEscape(Instant.now().toString())).append("\",\n");
        json.append("  \"openCount\": ").append(openReportCount()).append(",\n");
        json.append("  \"reports\": [\n");

        List<Integer> ids = reportIds();
        for (int index = 0; index < ids.size(); index++) {
            String id = String.valueOf(ids.get(index));
            String path = "reports." + id;
            json.append("    {\n");
            json.append("      \"id\": ").append(id).append(",\n");
            json.append("      \"status\": \"").append(jsonEscape(reports.getString(path + ".status", "unknown"))).append("\",\n");
            json.append("      \"category\": \"").append(jsonEscape(reports.getString(path + ".category", "unknown"))).append("\",\n");
            json.append("      \"categoryTitle\": \"").append(jsonEscape(reports.getString(path + ".category-title", "Unknown"))).append("\",\n");
            json.append("      \"playerName\": \"").append(jsonEscape(reports.getString(path + ".player-name", "Unknown"))).append("\",\n");
            json.append("      \"playerUuid\": \"").append(jsonEscape(reports.getString(path + ".player-uuid", ""))).append("\",\n");
            json.append("      \"world\": \"").append(jsonEscape(reports.getString(path + ".world", "unknown"))).append("\",\n");
            json.append("      \"x\": ").append(reports.getInt(path + ".x")).append(",\n");
            json.append("      \"y\": ").append(reports.getInt(path + ".y")).append(",\n");
            json.append("      \"z\": ").append(reports.getInt(path + ".z")).append(",\n");
            json.append("      \"createdAt\": \"").append(jsonEscape(reports.getString(path + ".created-at", "unknown"))).append("\",\n");
            json.append("      \"note\": \"").append(jsonEscape(reports.getString(path + ".note", ""))).append("\"\n");
            json.append("    }");
            if (index < ids.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private void fillReportList(Inventory inventory, List<Integer> ids) {
        if (ids.isEmpty()) {
            inventory.setItem(22, menuItem(Material.BARRIER, "&cNo reports found", List.of("&7Nothing to show yet.")));
            return;
        }

        int slot = 0;
        for (Integer id : ids) {
            inventory.setItem(slot, reportItem(String.valueOf(id)));
            slot++;
        }
    }

    private List<Integer> reportIds() {
        ConfigurationSection section = reports.getConfigurationSection("reports");
        if (section == null) {
            return List.of();
        }

        List<Integer> ids = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            try {
                ids.add(Integer.parseInt(key));
            } catch (NumberFormatException ignored) {
            }
        }
        ids.sort(Comparator.reverseOrder());
        return ids;
    }

    private ItemStack reportItem(String id) {
        String path = "reports." + id;
        String status = reports.getString(path + ".status", "unknown");
        String category = reports.getString(path + ".category-title", "Unknown");
        String player = reports.getString(path + ".player-name", "Unknown");
        String world = reports.getString(path + ".world", "unknown");
        int x = reports.getInt(path + ".x");
        int y = reports.getInt(path + ".y");
        int z = reports.getInt(path + ".z");
        String note = reports.getString(path + ".note", "");

        return menuItem(Material.PAPER, "&dReport #" + id + " &8(" + status + ")", List.of(
                "&7Category: &f" + category,
                "&7Player: &f" + player,
                "&7Location: &f" + world + " " + x + " " + y + " " + z,
                "&7Note: &f" + shorten(note, 38),
                "&8Click for details"
        ));
    }

    private void sendReportDetails(Player player, String id) {
        String path = "reports." + id;
        if (!reports.isConfigurationSection(path)) {
            notifications.send(player, "&cThat report no longer exists.");
            return;
        }

        notifications.send(player, "&dReport #" + id + " &8- &f" + reports.getString(path + ".category-title", "Unknown"));
        notifications.send(player, "&7Status: &f" + reports.getString(path + ".status", "unknown"));
        notifications.send(player, "&7Player: &f" + reports.getString(path + ".player-name", "Unknown"));
        notifications.send(player, "&7Location: &f" + reports.getString(path + ".world", "unknown")
                + " " + reports.getInt(path + ".x")
                + " " + reports.getInt(path + ".y")
                + " " + reports.getInt(path + ".z"));
        notifications.send(player, "&7Note: &f" + reports.getString(path + ".note", ""));
        notifications.send(player, "&7Created: &f" + reports.getString(path + ".created-at", "unknown"));
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

    private ItemStack menuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(name));
        meta.setLore(lore.stream().map(notifications::color).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private String shorten(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value == null ? "" : value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private String jsonEscape(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
