package dk.earthliving.core.earthos;

import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.passport.PassportService;
import dk.earthliving.core.report.ReportService;
import dk.earthliving.core.verification.VerificationService;
import dk.earthliving.core.webportal.WebPortalService;
import dk.earthliving.core.wallet.WalletService;
import dk.earthliving.core.jobs.JobsService;
import dk.earthliving.core.guide.GuideService;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class EarthOsService {
    public static final String MENU_TITLE = "EarthOS";
    public static final String SERVER_MENU_TITLE = "EarthOS Servers";
    public static final int SLOT_PASSPORT = 11;
    public static final int SLOT_VERIFY = 13;
    public static final int SLOT_SERVERS = 15;
    public static final int SLOT_WALLET = 20;
    public static final int SLOT_JOBS = 22;
    public static final int SLOT_GUIDE = 24;
    public static final int SLOT_MAP = 29;
    public static final int SLOT_REPORTS = 31;
    public static final int SLOT_STATUS = 33;
    public static final int SLOT_SETTINGS = 36;
    public static final int SLOT_PROFILE = 38;
    private static final int SLOT_HUB = 11;
    private static final int SLOT_MAIN = 15;
    private static final int SLOT_SERVER_BACK = 22;

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final ReportService reportService;
    private final WebPortalService webPortalService;
    private final PassportService passportService;
    private final VerificationService verificationService;
    private final WalletService walletService;
    private final JobsService jobsService;
    private final GuideService guideService;
    private final NamespacedKey itemKey;

    public EarthOsService(JavaPlugin plugin, NotificationService notifications, ReportService reportService, WebPortalService webPortalService, PassportService passportService, VerificationService verificationService, WalletService walletService, JobsService jobsService, GuideService guideService) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.reportService = reportService;
        this.webPortalService = webPortalService;
        this.passportService = passportService;
        this.verificationService = verificationService;
        this.walletService = walletService;
        this.jobsService = jobsService;
        this.guideService = guideService;
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

        Inventory inventory = Bukkit.createInventory(player, 45, MENU_TITLE);
        inventory.setItem(SLOT_PASSPORT, menuItem(Material.PAPER, configText("earthos.menu.passport-name", "&6Passport"), List.of(
                "&7Your EarthLiving identity",
                "&7Player: &f" + player.getName(),
                "&7Home country: &f" + passportCountryLabel(player),
                "&8VS1: read-only player data"
        )));
        inventory.setItem(SLOT_VERIFY, menuItem(Material.LIME_DYE, configText("earthos.menu.verify-name", "&aDiscord Verification"), List.of(
                "&7Start account linking safely",
                "&7Uses DiscordSRV link flow",
                "&8No passwords handled here"
        )));
        inventory.setItem(SLOT_SERVERS, menuItem(Material.COMPASS, configText("earthos.menu.servers-name", "&bServers"), List.of(
                "&7Choose Hub or Main",
                "&7Works through Velocity"
        )));
        inventory.setItem(SLOT_WALLET, menuItem(Material.EMERALD, configText("earthos.menu.wallet-name", "&aWallet"), List.of(
                "&7Balance: &f" + walletService.format(walletService.balance(player.getUniqueId())),
                "&8VS2: simple balance only"
        )));
        inventory.setItem(SLOT_JOBS, menuItem(Material.IRON_PICKAXE, configText("earthos.menu.jobs-name", "&eJobs"), List.of(
                "&7Mining, farming and fishing rewards",
                "&8No levels or professions yet"
        )));
        inventory.setItem(SLOT_GUIDE, menuItem(Material.BOOK, configText("earthos.menu.guide-name", "&6Guide"), List.of(
                "&7New player steps",
                "&7Verify, choose server, earn coins"
        )));
        inventory.setItem(SLOT_MAP, menuItem(Material.MAP, "&bWorld Map", List.of("&7Open BlueMap in chat")));
        // TODO VS3: banking, shops, nations, transport and quest systems should become separate EarthOS apps later.
        inventory.setItem(SLOT_REPORTS, menuItem(Material.WRITABLE_BOOK, "&dReports", List.of(
                "&7Open reports: &f" + reportService.openReportCount(),
                "&8TODO VS3: polish report flow"
        )));
        inventory.setItem(SLOT_STATUS, menuItem(Material.REDSTONE_TORCH, "&cServer Status", List.of("&7Status, maintenance and updates")));
        inventory.setItem(SLOT_SETTINGS, menuItem(Material.COMPARATOR, "&fSettings", List.of("&7Refresh EarthOS device")));
        inventory.setItem(SLOT_PROFILE, menuItem(Material.PLAYER_HEAD, "&bMy EarthLiving", List.of(
                "&7Web profile is planned later",
                "&7Linked profile: &f" + linkedProfileLabel(player),
                "&8TODO later: web portal profile"
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
            case SLOT_PASSPORT -> passportService.open(player);
            case SLOT_VERIFY -> verificationService.open(player);
            case SLOT_SERVERS -> openServerMenu(player);
            case SLOT_WALLET -> walletService.open(player);
            case SLOT_JOBS -> jobsService.open(player);
            case SLOT_GUIDE -> guideService.open(player);
            case SLOT_REPORTS -> reportService.open(player);
            case SLOT_STATUS -> sendConfiguredLines(player, "earthos.server-status");
            case SLOT_SETTINGS -> {
                giveDevice(player);
                sendConfiguredLines(player, "earthos.settings");
            }
            case SLOT_PROFILE -> {
                // TODO VS2/Web: connect this to the future website profile once VS1 verification is stable.
                sendConfiguredLines(player, "earthos.profile");
            }
            default -> {
            }
        }
    }

    public void openServerMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, SERVER_MENU_TITLE);
        inventory.setItem(SLOT_HUB, menuItem(Material.BEACON, configText("earthos.servers.hub-name", "&bHub"), List.of(
                "&7Spawn, EarthOS and onboarding",
                "&7Server: &f" + configText("earthos.servers.hub-server", "hub")
        )));
        inventory.setItem(SLOT_MAIN, menuItem(Material.GRASS_BLOCK, configText("earthos.servers.main-name", "&aMain"), List.of(
                "&7EarthLiving survival world",
                "&7Server: &f" + configText("earthos.servers.main-server", "main")
        )));
        inventory.setItem(SLOT_SERVER_BACK, menuItem(Material.ARROW, "&fBack to EarthOS", List.of("&7Return to the main menu.")));
        player.openInventory(inventory);
    }

    public void handleServerClick(Player player, int slot) {
        if (slot == SLOT_SERVER_BACK) {
            open(player);
            return;
        }
        if (!player.hasPermission("earthliving.serverselect")) {
            notifications.send(player, configText("messages.no-serverselect-permission", "&cYou do not have permission to switch servers."));
            return;
        }
        if (slot == SLOT_HUB) {
            connect(player, configText("earthos.servers.hub-server", "hub"));
        } else if (slot == SLOT_MAIN) {
            connect(player, configText("earthos.servers.main-server", "main"));
        }
    }

    private void connect(Player player, String server) {
        if (server == null || server.isBlank()) {
            notifications.send(player, "&cThat server is not configured yet.");
            return;
        }
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(bytes);
            output.writeUTF("Connect");
            output.writeUTF(server);
            player.sendPluginMessage(plugin, "BungeeCord", bytes.toByteArray());
            notifications.send(player, configText("earthos.servers.connecting-message", "&aConnecting to &f{server}&a.").replace("{server}", server));
        } catch (IOException exception) {
            notifications.send(player, "&cCould not send you to that server.");
        }
    }

    private String linkedProfileLabel(Player player) {
        String profileId = webPortalService.linkedProfileId(player.getUniqueId());
        return profileId.isBlank() ? "Not linked" : profileId;
    }

    private String passportCountryLabel(Player player) {
        String country = passportService.profile(player.getUniqueId(), player.getName()).citizenshipCountry();
        return country.isBlank() ? "Not selected" : country;
    }

    private String configText(String path, String fallback) {
        return plugin.getConfig().getString(path, fallback);
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
