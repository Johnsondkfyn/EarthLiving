package dk.earthliving.passportborders;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PassportBordersPlugin extends JavaPlugin implements Listener {
    private static final String MAIN_MENU_TITLE = "Server Menu";
    private static final String PASSPORT_MENU_TITLE = "Pas";

    private final CountryBorderService borderService = new CountryBorderService();
    private final Map<UUID, String> currentCountry = new HashMap<>();
    private final Map<UUID, Long> deniedMessageCooldown = new HashMap<>();
    private final Map<UUID, List<String>> ownedPassports = new HashMap<>();
    private final Set<UUID> builderVisualPlayers = new HashSet<>();
    private NamespacedKey menuItemKey;
    private Economy economy;
    private BukkitTask builderVisualTask;
    private File passportsFile;
    private YamlConfiguration passportsConfig;
    private File borderStatusFile;
    private YamlConfiguration borderStatusConfig;
    private double scale;
    private double tiles;

    @Override
    public void onEnable() {
        menuItemKey = new NamespacedKey(this, "server_menu_item");
        saveDefaultConfig();
        saveResourceIfMissing("countries.yml");
        setupEconomy();
        loadPassports();
        reloadPlugin();
        getServer().getPluginManager().registerEvents(this, this);
        startBuilderVisualTask();
    }

    @Override
    public void onDisable() {
        if (builderVisualTask != null) {
            builderVisualTask.cancel();
            builderVisualTask = null;
        }
        builderVisualPlayers.clear();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || !changedBlock(from, to) || !isCheckedWorld(to)) {
            return;
        }

        Player player = event.getPlayer();
        Country targetCountry = countryAt(to);
        String targetId = targetCountry == null ? null : targetCountry.id;
        String previousId = currentCountry.get(player.getUniqueId());
        if (same(previousId, targetId)) {
            return;
        }

        if (targetCountry != null && !canEnter(player, targetCountry)) {
            event.setCancelled(true);
            event.setTo(from);
            exportBorderStatus(player, targetCountry, false, to);
            showBorderParticles(player, from, to);
            sendDenied(player, targetCountry);
            return;
        }

        if (targetCountry == null) {
            currentCountry.remove(player.getUniqueId());
            exportBorderStatus(player, null, true, to);
            if (getConfig().getBoolean("visual-border.show-on-leave", true)) {
                showBorderParticles(player, from, to);
                player.sendActionBar(Component.text(stripColor(getConfig().getString("messages.wilderness-actionbar", "Leaving country border"))));
            }
            send(player, getConfig().getString("messages.wilderness", "&7Du forlader et land."));
            return;
        }

        currentCountry.put(player.getUniqueId(), targetCountry.id);
        exportBorderStatus(player, targetCountry, true, to);
        if (getConfig().getBoolean("visual-border.show-on-enter", true)) {
            showBorderParticles(player, from, to);
        }
        String message = hasBorderBypass(player)
                ? getConfig().getString("messages.entered-bypass", "&eDu går ind i &f%country%&e med border bypass.")
                : getConfig().getString("messages.entered", "&aDu går ind i &f%country%&a.");
        send(player, message.replace("%country%", targetCountry.name));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> giveMenuItem(event.getPlayer()), 20L);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Player player = event.getPlayer();
            Country country = countryAt(player.getLocation());
            exportBorderStatus(player, country, country == null || canEnter(player, country), player.getLocation());
        }, 40L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        builderVisualPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!isMenuItem(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        Bukkit.getScheduler().runTask(this, () -> openMainMenu(event.getPlayer()));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isMenuItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            if (isMenuItem(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isMenuItem(event.getCurrentItem()) || isMenuItem(event.getCursor())) {
            event.setCancelled(true);
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        String title = event.getView().getTitle();
        if (!title.equals(MAIN_MENU_TITLE) && !title.equals(PASSPORT_MENU_TITLE)) {
            return;
        }

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (title.equals(MAIN_MENU_TITLE)) {
            handleMainMenuClick(player, event.getSlot());
        } else {
            handlePassportMenuClick(player, event.getSlot());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("menu")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use /menu.");
                return true;
            }
            openMainMenu(player);
            return true;
        }

        if (command.getName().equalsIgnoreCase("passport")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use /passport.");
                return true;
            }
            if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                int page = args.length >= 2 ? parsePage(args[1]) : 1;
                sendCountryList(player, page);
                return true;
            }
            if (args.length > 0 && args[0].equalsIgnoreCase("buy")) {
                if (args.length < 2) {
                    player.sendMessage(color("&eBrug: &f/passport buy <country> [visitor|event|work|resident|citizenship]"));
                    return true;
                }
                Country country = borderService.findByIdOrName(args[1]);
                if (country == null) {
                    player.sendMessage(color("&cUkendt land. Brug &f/passport list &cfor at se lande."));
                    return true;
                }
                buyVisa(player, country, args.length >= 3 ? args[2] : defaultVisaType());
                return true;
            }
            if (args.length > 0 && args[0].equalsIgnoreCase("info")) {
                Country country = args.length >= 2 ? borderService.findByIdOrName(args[1]) : countryAt(player.getLocation());
                if (country == null) {
                    player.sendMessage(color("&7Du står ikke i et registreret land, eller landet blev ikke fundet."));
                    return true;
                }
                sendCountryInfo(player, country);
                return true;
            }
            openPassportMenu(player);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("passportborders.admin")) {
                sender.sendMessage(color("&cDu har ikke adgang til den kommando."));
                return true;
            }
            reloadPlugin();
            sender.sendMessage(color("&aPassportBorders er genindlæst."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("visual")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can toggle border visuals.");
                return true;
            }
            if (!player.hasPermission("passportborders.admin")) {
                player.sendMessage(color("&cDu har ikke adgang til den kommando."));
                return true;
            }
            toggleBuilderVisual(player, args.length >= 2 ? args[1] : "");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("setservice")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can set service locations.");
                return true;
            }
            if (!player.hasPermission("passportborders.admin")) {
                player.sendMessage(color("&cDu har ikke adgang til den kommando."));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(color("&eBrug: &f/border setservice <passport-office|shop|jobcenter>"));
                return true;
            }
            setServiceLocation(player, args[1]);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(color("&eLande:"));
            for (Country country : borderService.countries()) {
                sender.sendMessage(color("&7- &f" + country.name + " &8(" + country.permission + ")"));
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /border info.");
            return true;
        }

        Country country = countryAt(player.getLocation());
        GeoPoint geo = toGeo(player.getLocation());
        if (country == null) {
            player.sendMessage(color("&7Du står ikke i et registreret land."));
        } else {
            player.sendMessage(color("&eLand: &f" + country.name));
            player.sendMessage(color("&eKræver: &f" + country.permission));
            player.sendMessage(color(canEnter(player, country) ? "&aDu har adgang." : "&cDu mangler pas/visum."));
        }
        player.sendMessage(color("&eLat/Lon: &f" + round(geo.latitude) + ", " + round(geo.longitude)));
        return true;
    }

    private void openMainMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, MAIN_MENU_TITLE);
        inventory.setItem(10, menuButton(Material.PAPER, "&aPas", List.of("&7Koeb pas ved paskontoret.", nearbyText(player, "passport-office"))));
        inventory.setItem(11, menuButton(Material.MINECART, "&eTransport", List.of("&7Se stationer og ruter.", "&8Ingen teleport.")));
        inventory.setItem(12, menuButton(Material.IRON_PICKAXE, "&6Jobs", List.of("&7Bruges ved jobcenter.", nearbyText(player, "jobcenter"))));
        inventory.setItem(13, menuButton(Material.EMERALD, "&2Butik", List.of("&7Bruges ved fysisk butik.", nearbyText(player, "shop"))));
        inventory.setItem(14, menuButton(Material.OAK_DOOR, "&bByer", List.of("&7Aabner Towny-menuen.")));
        inventory.setItem(15, menuButton(Material.COMPASS, "&dKort", List.of("&7Viser koordinat-hjaelp.")));
        inventory.setItem(16, menuButton(Material.BOOK, "&fRegler", List.of("&7Viser korte serverregler.")));
        player.openInventory(inventory);
    }

    private void openPassportMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, PASSPORT_MENU_TITLE);
        int slot = 10;
        for (Country country : borderService.countries()) {
            boolean owned = ownsPassport(player, country);
            Material material = owned ? Material.LIME_DYE : Material.PAPER;
            List<String> lore = new ArrayList<>();
            String defaultType = defaultVisaType();
            lore.add(owned ? "&aEjet." : "&e" + visaDisplayName(defaultType) + ": &f" + money(visaPrice(country, defaultType)));
            lore.add(owned ? "&7Dette visum/pas er registreret paa dig." : "&7Klik for at koebe standardvisum.");
            if (canEnter(player, country) && !owned) {
                lore.add("&8Admin/bypass giver adgang, men passet er ikke koebt.");
            }
            lore.add("&7Permission: &f" + country.permission);
            inventory.setItem(slot, menuButton(material, "&f" + country.name, lore));
            slot++;
            if (slot == 17) {
                break;
            }
        }
        inventory.setItem(22, menuButton(Material.ARROW, "&eTilbage", List.of("&7Til servermenuen.")));
        player.openInventory(inventory);
    }

    private void handleMainMenuClick(Player player, int slot) {
        if (slot == 10) {
            if (!isNearService(player, "passport-office")) {
                player.closeInventory();
                sendServiceRequired(player, "paskontor", "passport-office");
                return;
            }
            openPassportMenu(player);
            return;
        }
        if (slot == 11) {
            player.closeInventory();
            sendTransportInfo(player);
            return;
        }
        if (slot == 12) {
            if (!isNearService(player, "jobcenter")) {
                player.closeInventory();
                sendServiceRequired(player, "jobcenter", "jobcenter");
                return;
            }
            runPlayerCommand(player, "jobs browse");
            return;
        }
        if (slot == 13) {
            if (!isNearService(player, "shop")) {
                player.closeInventory();
                sendServiceRequired(player, "butik", "shop");
                return;
            }
            runPlayerCommand(player, "shop");
            return;
        }
        if (slot == 14) {
            runPlayerCommand(player, "towny menu");
            return;
        }
        if (slot == 15) {
            player.closeInventory();
            player.sendMessage(color("&eKort: &fBrug &b/getlocation &ffor din Google Maps-position."));
            player.sendMessage(color("&eKort: &fBrug &b/coordinate <lat> <lon> &ffor Minecraft-koordinater."));
            return;
        }
        if (slot == 16) {
            player.closeInventory();
            player.sendMessage(color("&eRegler: &fIngen grief, ingen snyd, og brug transport i stedet for teleport."));
        }
    }

    private void handlePassportMenuClick(Player player, int slot) {
        if (slot == 22) {
            openMainMenu(player);
            return;
        }
        if (slot >= 10 && slot < 17) {
            Country country = borderService.findBySlotIndex(slot - 10);
            if (country != null) {
                buyVisa(player, country, defaultVisaType());
                openPassportMenu(player);
            }
        }
    }

    private void buyVisa(Player player, Country country, String visaType) {
        String normalizedVisaType = normalizeVisaType(visaType);
        if (normalizedVisaType == null) {
            player.sendMessage(color("&cUkendt visa-type. Brug: &f" + String.join(", ", visaTypes())));
            return;
        }
        if (ownsVisa(player, country, normalizedVisaType)) {
            player.sendMessage(color("&aDu har allerede &f" + visaDisplayName(normalizedVisaType) + " &atil &f" + country.name + "&a."));
            return;
        }
        if (economy == null) {
            player.sendMessage(color("&cOekonomi er ikke klar endnu."));
            return;
        }
        if (!isNearService(player, "passport-office")) {
            sendServiceRequired(player, "paskontor", "passport-office");
            return;
        }

        OfflinePlayer offlinePlayer = player;
        double price = visaPrice(country, normalizedVisaType);
        if (!economy.has(offlinePlayer, price)) {
            player.sendMessage(color("&cDu mangler penge. &f" + country.name + " " + visaDisplayName(normalizedVisaType)
                    + " &ckoster &f" + money(price) + "&c."));
            return;
        }

        economy.withdrawPlayer(offlinePlayer, price);
        addVisa(player, country, normalizedVisaType);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + player.getName() + " permission set " + country.permission + " true");
        player.sendMessage(color("&aDu koebte &f" + visaDisplayName(normalizedVisaType) + " &atil &f" + country.name
                + " &afor &f" + money(price) + "&a."));
    }

    private void runPlayerCommand(Player player, String command) {
        player.closeInventory();
        player.performCommand(command);
    }

    private void sendTransportInfo(Player player) {
        player.sendMessage(color("&eTransportsteder:"));
        List<String> lines = getConfig().getStringList("menu.transport");
        if (lines.isEmpty()) {
            lines = List.of("&eKobenhavn H &7- X 4290, Z -19004", "&eHamburg Hbf &7- X 3473, Z -18142");
        }
        for (String line : lines) {
            player.sendMessage(color(line));
        }
    }

    private void sendServiceRequired(Player player, String displayName, String serviceId) {
        player.sendMessage(color("&cDu skal vaere ved et fysisk &f" + displayName + " &cfor at bruge dette."));
        String nearest = nearestServiceLine(player, serviceId);
        if (nearest != null) {
            player.sendMessage(color("&eNaermeste sted: &f" + nearest));
        }
    }

    private String nearbyText(Player player, String serviceId) {
        return isNearService(player, serviceId) ? "&aDu er ved stedet." : "&8Find et fysisk sted.";
    }

    private boolean isNearService(Player player, String serviceId) {
        if (player.hasPermission("passportborders.admin")) {
            return true;
        }
        double radius = getConfig().getDouble("services.radius", 8.0);
        for (Location location : serviceLocations(serviceId)) {
            if (!location.getWorld().equals(player.getWorld())) {
                continue;
            }
            if (location.distance(player.getLocation()) <= radius) {
                return true;
            }
        }
        return false;
    }

    private String nearestServiceLine(Player player, String serviceId) {
        Location nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Location location : serviceLocations(serviceId)) {
            if (!location.getWorld().equals(player.getWorld())) {
                continue;
            }
            double distance = location.distance(player.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = location;
            }
        }
        if (nearest == null) {
            return null;
        }
        return "X " + nearest.getBlockX() + ", Y " + nearest.getBlockY() + ", Z " + nearest.getBlockZ();
    }

    private List<Location> serviceLocations(String serviceId) {
        List<Location> locations = new ArrayList<>();
        for (String line : getConfig().getStringList("services." + serviceId + ".locations")) {
            String[] parts = line.split(",");
            if (parts.length < 4 || Bukkit.getWorld(parts[0]) == null) {
                continue;
            }
            try {
                locations.add(new Location(Bukkit.getWorld(parts[0]),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])));
            } catch (NumberFormatException ignored) {
                // Ignore invalid config lines.
            }
        }
        return locations;
    }

    private void setServiceLocation(Player player, String serviceId) {
        if (!List.of("passport-office", "shop", "jobcenter").contains(serviceId)) {
            player.sendMessage(color("&cUkendt service. Brug passport-office, shop eller jobcenter."));
            return;
        }
        Location location = player.getLocation();
        String line = location.getWorld().getName() + ","
                + location.getBlockX() + ","
                + location.getBlockY() + ","
                + location.getBlockZ();
        getConfig().set("services." + serviceId + ".locations", List.of(line));
        saveConfig();
        player.sendMessage(color("&aService-sted sat: &f" + serviceId + " &7(" + line + ")"));
    }

    private ItemStack menuButton(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(color(line));
        }
        meta.setLore(coloredLore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private void giveMenuItem(Player player) {
        if (!getConfig().getBoolean("menu.enabled", true)) {
            return;
        }
        int slot = Math.max(0, Math.min(8, getConfig().getInt("menu.slot", 8)));
        ItemStack current = player.getInventory().getItem(slot);
        if (current != null && !current.getType().isAir() && !isMenuItem(current)) {
            return;
        }
        player.getInventory().setItem(slot, createMenuItem());
    }

    private ItemStack createMenuItem() {
        Material material = Material.matchMaterial(getConfig().getString("menu.item-material", "NETHER_STAR"));
        if (material == null || material.isAir()) {
            material = Material.NETHER_STAR;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(getConfig().getString("menu.item-name", "&aServer Menu")));
        meta.setLore(List.of(color("&7Hojreklik for at aabne menuen.")));
        meta.getPersistentDataContainer().set(menuItemKey, PersistentDataType.BYTE, (byte) 1);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isMenuItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(menuItemKey, PersistentDataType.BYTE);
    }

    private void reloadPlugin() {
        reloadConfig();
        loadPassports();
        scale = getConfig().getDouble("scale", 5120.0);
        tiles = getConfig().getDouble("tiles", 15.0);
        borderService.load(new File(getDataFolder(), "countries.yml"));
        loadBorderStatusExport();
        currentCountry.clear();
        getLogger().info("Loaded " + borderService.countries().size() + " countries.");
    }

    private void startBuilderVisualTask() {
        if (builderVisualTask != null) {
            builderVisualTask.cancel();
        }
        long interval = Math.max(5L, getConfig().getLong("builder-visual.interval-ticks", 20L));
        builderVisualTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!getConfig().getBoolean("builder-visual.enabled", true) || builderVisualPlayers.isEmpty()) {
                return;
            }
            for (UUID uuid : new HashSet<>(builderVisualPlayers)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    builderVisualPlayers.remove(uuid);
                    continue;
                }
                showNearbyBorders(player);
            }
        }, interval, interval);
    }

    private void toggleBuilderVisual(Player player, String mode) {
        boolean enable = switch (mode.toLowerCase(Locale.ROOT)) {
            case "on", "true", "start" -> true;
            case "off", "false", "stop" -> false;
            default -> !builderVisualPlayers.contains(player.getUniqueId());
        };

        if (enable) {
            builderVisualPlayers.add(player.getUniqueId());
            player.sendMessage(color("&aBorder build-visual er slået til. Brug &f/border visual off &afor at stoppe."));
            showNearbyBorders(player);
            return;
        }

        builderVisualPlayers.remove(player.getUniqueId());
        player.sendMessage(color("&7Border build-visual er slået fra."));
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> registration = getServer().getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            economy = registration.getProvider();
        }
    }

    private void loadPassports() {
        passportsFile = new File(getDataFolder(), "passports.yml");
        passportsConfig = YamlConfiguration.loadConfiguration(passportsFile);
        ownedPassports.clear();
        if (!passportsConfig.isConfigurationSection("players")) {
            return;
        }
        for (String uuidText : passportsConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidText);
                ownedPassports.put(uuid, new ArrayList<>(passportsConfig.getStringList("players." + uuidText)));
            } catch (IllegalArgumentException ignored) {
                // Ignore broken UUID entries.
            }
        }
    }

    private boolean ownsPassport(Player player, Country country) {
        for (String record : ownedPassports.getOrDefault(player.getUniqueId(), List.of())) {
            if (record.equals(country.id) || record.startsWith(country.id + ":")) {
                return true;
            }
        }
        return false;
    }

    private boolean ownsVisa(Player player, Country country, String visaType) {
        List<String> records = ownedPassports.getOrDefault(player.getUniqueId(), List.of());
        return records.contains(country.id) || records.contains(country.id + ":" + visaType);
    }

    private void addVisa(Player player, Country country, String visaType) {
        List<String> passports = new ArrayList<>(ownedPassports.getOrDefault(player.getUniqueId(), List.of()));
        String record = country.id + ":" + visaType;
        if (!passports.contains(record) && !passports.contains(country.id)) {
            passports.add(record);
        }
        ownedPassports.put(player.getUniqueId(), passports);
        passportsConfig.set("players." + player.getUniqueId(), passports);
        try {
            passportsConfig.save(passportsFile);
        } catch (Exception exception) {
            getLogger().warning("Could not save passports.yml: " + exception.getMessage());
        }
    }

    private Country countryAt(Location location) {
        GeoPoint geo = toGeo(location);
        return borderService.findCountry(geo.latitude, geo.longitude);
    }

    private GeoPoint toGeo(Location location) {
        double latitude = -location.getZ() / scale * tiles;
        double longitude = location.getX() / scale * tiles;
        return new GeoPoint(latitude, longitude);
    }

    private boolean canEnter(Player player, Country country) {
        return hasBorderBypass(player)
                || ownsPassport(player, country)
                || player.hasPermission(country.permission);
    }

    private boolean hasBorderBypass(Player player) {
        return player.hasPermission("passportborders.bypass")
                || player.hasPermission("earthliving.border.bypass");
    }

    private void sendCountryList(Player player, int page) {
        List<Country> countries = borderService.countries();
        int pageSize = 12;
        int maxPage = Math.max(1, (int) Math.ceil(countries.size() / (double) pageSize));
        int safePage = Math.max(1, Math.min(maxPage, page));
        player.sendMessage(color("&ePassport countries &7(" + safePage + "/" + maxPage + "):"));
        int start = (safePage - 1) * pageSize;
        for (int index = start; index < Math.min(start + pageSize, countries.size()); index++) {
            Country country = countries.get(index);
            player.sendMessage(color("&7- &f" + country.id + " &8- &e" + country.name
                    + " &7base &f" + money(country.price)));
        }
        player.sendMessage(color("&7Koeb: &f/passport buy <country> [visitor|event|work|resident|citizenship]"));
    }

    private void sendCountryInfo(Player player, Country country) {
        player.sendMessage(color("&eLand: &f" + country.name + " &8(" + country.id + ")"));
        player.sendMessage(color("&eAdgang: &f" + (canEnter(player, country) ? "ja" : "nej")));
        for (String type : visaTypes()) {
            player.sendMessage(color("&7- &f" + visaDisplayName(type) + "&7: &e" + money(visaPrice(country, type))
                    + visaDurationText(type)));
        }
    }

    private double visaPrice(Country country, String visaType) {
        return Math.max(1.0, country.price * getConfig().getDouble("visa.types." + visaType + ".price-multiplier", 1.0));
    }

    private String visaDisplayName(String visaType) {
        return getConfig().getString("visa.types." + visaType + ".display-name", visaType);
    }

    private String visaDurationText(String visaType) {
        int days = getConfig().getInt("visa.types." + visaType + ".duration-days", 0);
        return days <= 0 ? " &8(permanent)" : " &8(" + days + " days)";
    }

    private String defaultVisaType() {
        String type = normalizeVisaType(getConfig().getString("visa.default-type", "visitor"));
        return type == null ? "visitor" : type;
    }

    private String normalizeVisaType(String visaType) {
        String normalized = visaType == null ? "" : visaType.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "");
        return visaTypes().contains(normalized) ? normalized : null;
    }

    private List<String> visaTypes() {
        ConfigurationSection section = getConfig().getConfigurationSection("visa.types");
        if (section == null) {
            return List.of("visitor", "event", "work", "resident", "citizenship");
        }
        return new ArrayList<>(section.getKeys(false));
    }

    private int parsePage(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private boolean isCheckedWorld(Location location) {
        List<String> worlds = getConfig().getStringList("check-worlds");
        return worlds.isEmpty() || worlds.contains(location.getWorld().getName());
    }

    private boolean changedBlock(Location from, Location to) {
        return from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
    }

    private void sendDenied(Player player, Country country) {
        long now = System.currentTimeMillis();
        long last = deniedMessageCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < 2000) {
            return;
        }
        deniedMessageCooldown.put(player.getUniqueId(), now);
        String visaName = visaDisplayName(defaultVisaType());
        player.sendActionBar(Component.text(stripColor(getConfig().getString("messages.denied-actionbar", "Visa required for %country%")
                .replace("%country%", country.name)
                .replace("%visa%", visaName))));
        player.showTitle(Title.title(
                Component.text(stripColor(getConfig().getString("messages.denied-title", "Border closed"))),
                Component.text(stripColor(getConfig().getString("messages.denied-subtitle", "Visa required: %visa%")
                        .replace("%country%", country.name)
                        .replace("%visa%", visaName))),
                Title.Times.times(Duration.ofMillis(150), Duration.ofMillis(1200), Duration.ofMillis(300))
        ));
        send(player, getConfig().getString("messages.denied", "&cDu skal bruge pas/visum for at komme ind i &f%country%&c.")
                .replace("%country%", country.name));
    }

    private void showBorderParticles(Player player, Location from, Location to) {
        if (!getConfig().getBoolean("visual-border.enabled", true)) {
            return;
        }
        Particle particle;
        try {
            particle = Particle.valueOf(getConfig().getString("visual-border.particle", "END_ROD"));
        } catch (IllegalArgumentException exception) {
            particle = Particle.END_ROD;
        }
        int widthSteps = Math.max(4, getConfig().getInt("visual-border.count", 18));
        double halfWidth = Math.max(1.5, getConfig().getDouble("visual-border.radius", 1.6) * 2.5D);
        double height = Math.max(1.5, getConfig().getDouble("visual-border.wall-height", 3.5D));
        double heightStep = Math.max(0.35, getConfig().getDouble("visual-border.wall-step", 0.5D));
        double yOffset = getConfig().getDouble("visual-border.y-offset", 1.0);
        Location base = to.clone().add(0.0, yOffset, 0.0);

        double moveX = to.getX() - from.getX();
        double moveZ = to.getZ() - from.getZ();
        double length = Math.sqrt(moveX * moveX + moveZ * moveZ);
        double sideX = length < 0.001D ? 1.0D : -moveZ / length;
        double sideZ = length < 0.001D ? 0.0D : moveX / length;

        for (int widthIndex = 0; widthIndex <= widthSteps; widthIndex++) {
            double progress = widthIndex / (double) widthSteps;
            double sideOffset = (progress - 0.5D) * halfWidth * 2.0D;
            for (double y = 0.0D; y <= height; y += heightStep) {
                Location point = base.clone().add(sideX * sideOffset, y, sideZ * sideOffset);
                player.spawnParticle(particle, point, 1, 0.0, 0.02, 0.0, 0.0);
            }
        }
    }

    private void showNearbyBorders(Player player) {
        Location location = player.getLocation();
        if (!isCheckedWorld(location)) {
            return;
        }

        Particle particle = builderVisualParticle();
        double radius = Math.max(16.0D, getConfig().getDouble("builder-visual.radius", 96.0D));
        double radiusSquared = radius * radius;
        double sampleStep = Math.max(1.0D, getConfig().getDouble("builder-visual.sample-step", 4.0D));
        double height = Math.max(1.0D, getConfig().getDouble("builder-visual.height", 4.0D));
        double heightStep = Math.max(0.5D, getConfig().getDouble("builder-visual.height-step", 1.0D));
        int maxParticles = Math.max(100, getConfig().getInt("builder-visual.max-particles", 900));
        int spawned = 0;

        double px = location.getX();
        double pz = location.getZ();
        double y = location.getY() + 0.25D;

        for (Country country : borderService.countries()) {
            for (List<GeoPoint> polygon : country.polygons) {
                for (int index = 0; index < polygon.size(); index++) {
                    GeoPoint first = polygon.get(index);
                    GeoPoint second = polygon.get((index + 1) % polygon.size());
                    double x1 = worldX(first);
                    double z1 = worldZ(first);
                    double x2 = worldX(second);
                    double z2 = worldZ(second);
                    if (distanceSquaredToSegment(px, pz, x1, z1, x2, z2) > radiusSquared) {
                        continue;
                    }

                    double dx = x2 - x1;
                    double dz = z2 - z1;
                    double length = Math.max(1.0D, Math.sqrt(dx * dx + dz * dz));
                    int steps = Math.max(1, (int) Math.ceil(length / sampleStep));
                    for (int step = 0; step <= steps; step++) {
                        double progress = step / (double) steps;
                        double x = x1 + dx * progress;
                        double z = z1 + dz * progress;
                        double distanceSquared = (x - px) * (x - px) + (z - pz) * (z - pz);
                        if (distanceSquared > radiusSquared) {
                            continue;
                        }
                        for (double offset = 0.0D; offset <= height; offset += heightStep) {
                            player.spawnParticle(particle, x, y + offset, z, 1, 0.0, 0.02, 0.0, 0.0);
                            spawned++;
                            if (spawned >= maxParticles) {
                                player.sendActionBar(Component.text("Border visual: " + country.name));
                                return;
                            }
                        }
                    }
                }
            }
        }

        player.sendActionBar(Component.text("Border visual: nearby borders"));
    }

    private Particle builderVisualParticle() {
        try {
            return Particle.valueOf(getConfig().getString("builder-visual.particle", "END_ROD"));
        } catch (IllegalArgumentException exception) {
            return Particle.END_ROD;
        }
    }

    private double worldX(GeoPoint point) {
        return point.longitude / tiles * scale;
    }

    private double worldZ(GeoPoint point) {
        return -point.latitude / tiles * scale;
    }

    private double distanceSquaredToSegment(double px, double pz, double x1, double z1, double x2, double z2) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double lengthSquared = dx * dx + dz * dz;
        if (lengthSquared <= 0.0001D) {
            double pointDx = px - x1;
            double pointDz = pz - z1;
            return pointDx * pointDx + pointDz * pointDz;
        }
        double progress = ((px - x1) * dx + (pz - z1) * dz) / lengthSquared;
        progress = Math.max(0.0D, Math.min(1.0D, progress));
        double closestX = x1 + progress * dx;
        double closestZ = z1 + progress * dz;
        double closestDx = px - closestX;
        double closestDz = pz - closestZ;
        return closestDx * closestDx + closestDz * closestDz;
    }

    private void loadBorderStatusExport() {
        String configured = getConfig().getString("status-export.file", "../EarthLivingCore/border-status.yml");
        borderStatusFile = new File(getDataFolder(), configured);
        if (!borderStatusFile.isAbsolute()) {
            borderStatusFile = new File(getDataFolder(), configured);
        }
        borderStatusConfig = YamlConfiguration.loadConfiguration(borderStatusFile);
    }

    private void exportBorderStatus(Player player, Country country, boolean allowed, Location location) {
        if (!getConfig().getBoolean("status-export.enabled", true) || borderStatusConfig == null || borderStatusFile == null) {
            return;
        }
        String path = "players." + player.getUniqueId();
        GeoPoint geo = toGeo(location);
        borderStatusConfig.set(path + ".player-name", player.getName());
        borderStatusConfig.set(path + ".updated-at", Instant.now().toString());
        borderStatusConfig.set(path + ".country-id", country == null ? "" : country.id);
        borderStatusConfig.set(path + ".country-name", country == null ? "" : country.name);
        borderStatusConfig.set(path + ".allowed", allowed);
        borderStatusConfig.set(path + ".required-visa", country == null || allowed ? "" : defaultVisaType());
        borderStatusConfig.set(path + ".required-permission", country == null || allowed ? "" : country.permission);
        borderStatusConfig.set(path + ".latitude", geo.latitude);
        borderStatusConfig.set(path + ".longitude", geo.longitude);
        try {
            borderStatusFile.getParentFile().mkdirs();
            borderStatusConfig.save(borderStatusFile);
        } catch (Exception exception) {
            getLogger().warning("Could not save border status export: " + exception.getMessage());
        }
    }

    private void send(Player player, String message) {
        if (message != null && !message.isBlank()) {
            player.sendMessage(color(message));
        }
    }

    private void saveResourceIfMissing(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) {
            saveResource(name, false);
        }
    }

    private boolean same(String first, String second) {
        if (first == null) {
            return second == null;
        }
        return first.equals(second);
    }

    private String round(double value) {
        return String.format("%.5f", value);
    }

    private String money(double value) {
        if (economy != null) {
            return economy.format(value);
        }
        return String.format("%.0f", value);
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String stripColor(String message) {
        return ChatColor.stripColor(color(message == null ? "" : message));
    }
}
