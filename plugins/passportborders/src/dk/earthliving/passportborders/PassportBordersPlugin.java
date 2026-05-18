package dk.earthliving.passportborders;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.configuration.file.YamlConfiguration;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PassportBordersPlugin extends JavaPlugin implements Listener {
    private static final String MAIN_MENU_TITLE = "Server Menu";
    private static final String PASSPORT_MENU_TITLE = "Pas";

    private final CountryBorderService borderService = new CountryBorderService();
    private final Map<UUID, String> currentCountry = new HashMap<>();
    private final Map<UUID, Long> deniedMessageCooldown = new HashMap<>();
    private final Map<UUID, List<String>> ownedPassports = new HashMap<>();
    private NamespacedKey menuItemKey;
    private Economy economy;
    private File passportsFile;
    private YamlConfiguration passportsConfig;
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
            sendDenied(player, targetCountry);
            return;
        }

        if (targetCountry == null) {
            currentCountry.remove(player.getUniqueId());
            send(player, getConfig().getString("messages.wilderness", "&7Du forlader et land."));
            return;
        }

        currentCountry.put(player.getUniqueId(), targetCountry.id);
        send(player, getConfig().getString("messages.entered", "&aDu går ind i &f%country%&a.")
                .replace("%country%", targetCountry.name));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> giveMenuItem(event.getPlayer()), 20L);
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
        openMainMenu(event.getPlayer());
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
            lore.add(owned ? "&aEjet." : "&ePris: &f" + money(country.price));
            lore.add(owned ? "&7Dette pas er registreret paa dig." : "&7Klik for at koebe pas.");
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
                buyPassport(player, country);
                openPassportMenu(player);
            }
        }
    }

    private void buyPassport(Player player, Country country) {
        if (ownsPassport(player, country)) {
            player.sendMessage(color("&aDu har allerede pas til &f" + country.name + "&a."));
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
        if (!economy.has(offlinePlayer, country.price)) {
            player.sendMessage(color("&cDu mangler penge. &f" + country.name + " &ckoster &f" + money(country.price) + "&c."));
            return;
        }

        economy.withdrawPlayer(offlinePlayer, country.price);
        addPassport(player, country);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "lp user " + player.getName() + " permission set " + country.permission + " true");
        player.sendMessage(color("&aDu koebte pas til &f" + country.name + " &afor &f" + money(country.price) + "&a."));
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
        currentCountry.clear();
        getLogger().info("Loaded " + borderService.countries().size() + " countries.");
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
        return ownedPassports.getOrDefault(player.getUniqueId(), List.of()).contains(country.id);
    }

    private void addPassport(Player player, Country country) {
        List<String> passports = new ArrayList<>(ownedPassports.getOrDefault(player.getUniqueId(), List.of()));
        if (!passports.contains(country.id)) {
            passports.add(country.id);
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
        return player.hasPermission("passportborders.bypass")
                || ownsPassport(player, country)
                || player.hasPermission(country.permission);
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
        send(player, getConfig().getString("messages.denied", "&cDu skal bruge pas/visum for at komme ind i &f%country%&c.")
                .replace("%country%", country.name));
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
}
