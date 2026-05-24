package dk.earthliving.core.earthos;

import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.report.ReportService;
import dk.earthliving.core.webportal.WebPortalService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public static final int SLOT_MAP = 10;
    public static final int SLOT_EVENTS = 11;
    public static final int SLOT_PASSPORT = 12;
    public static final int SLOT_WALLET = 13;
    public static final int SLOT_REPORTS = 14;
    public static final int SLOT_STATUS = 15;
    public static final int SLOT_SETTINGS = 16;
    public static final int SLOT_PROFILE = 22;

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final ReportService reportService;
    private final WebPortalService webPortalService;
    private final NamespacedKey itemKey;

    public EarthOsService(JavaPlugin plugin, NotificationService notifications, ReportService reportService, WebPortalService webPortalService) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.reportService = reportService;
        this.webPortalService = webPortalService;
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
        inventory.setItem(SLOT_MAP, menuItem(Material.MAP, "&bWorld Map", List.of("&7Open BlueMap in chat")));
        inventory.setItem(SLOT_EVENTS, menuItem(Material.CLOCK, "&eServer Events", List.of("&7Random events and competitions")));
        inventory.setItem(SLOT_PASSPORT, menuItem(Material.PAPER, "&6Passport", List.of("&7Countries, access and travel")));
        inventory.setItem(SLOT_WALLET, menuItem(Material.EMERALD, "&aWallet", List.of("&7Economy placeholder")));
        inventory.setItem(SLOT_REPORTS, menuItem(Material.WRITABLE_BOOK, "&dReports", List.of(
                "&7Create a quick report",
                "&7Open reports: &f" + reportService.openReportCount()
        )));
        inventory.setItem(SLOT_STATUS, menuItem(Material.REDSTONE_TORCH, "&cServer Status", List.of("&7Status, maintenance and updates")));
        inventory.setItem(SLOT_SETTINGS, menuItem(Material.COMPARATOR, "&fSettings", List.of("&7Player preferences")));
        inventory.setItem(SLOT_PROFILE, menuItem(Material.PLAYER_HEAD, "&bMy EarthLiving", List.of(
                "&7Link your website profile",
                "&7Linked profile: &f" + linkedProfileLabel(player)
        )));
        player.openInventory(inventory);
    }

    public void handleMenuClick(Player player, int slot) {
        player.closeInventory();

        switch (slot) {
            case SLOT_MAP -> sendClickableLink(
                    player,
                    "BlueMap",
                    plugin.getConfig().getString("earthos.bluemap-url", "http://159.195.149.253:8100/")
            );
            case SLOT_EVENTS -> sendConfiguredLines(player, "earthos.events");
            case SLOT_PASSPORT -> sendConfiguredLines(player, "earthos.passport");
            case SLOT_WALLET -> sendConfiguredLines(player, "earthos.wallet");
            case SLOT_REPORTS -> reportService.open(player);
            case SLOT_STATUS -> sendConfiguredLines(player, "earthos.server-status");
            case SLOT_SETTINGS -> {
                giveDevice(player);
                sendConfiguredLines(player, "earthos.settings");
            }
            case SLOT_PROFILE -> {
                sendConfiguredLines(player, "earthos.profile");
                webPortalService.beginLinkInput(player);
            }
            default -> {
            }
        }
    }

    private String linkedProfileLabel(Player player) {
        String profileId = webPortalService.linkedProfileId(player.getUniqueId());
        return profileId.isBlank() ? "Not linked" : profileId;
    }

    private void sendConfiguredLines(Player player, String path) {
        List<String> lines = plugin.getConfig().getStringList(path);
        if (lines.isEmpty()) {
            notifications.send(player, "&7This EarthOS app is not configured yet.");
            return;
        }

        for (String line : lines) {
            notifications.send(player, line);
        }
    }

    private void sendClickableLink(Player player, String label, String url) {
        notifications.send(player, "&b" + label + ": &f" + url);
        player.sendMessage(Component.text("Click here to open " + label)
                .color(NamedTextColor.AQUA)
                .clickEvent(ClickEvent.openUrl(url)));
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
