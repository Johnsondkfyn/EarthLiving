package dk.earthliving.core.passport;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class PassportService {
    public static final String MENU_TITLE = "EarthOS Passport";

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final File dataFile;
    private final File exportFile;
    private final File borderStatusFile;
    private FileConfiguration data;

    public PassportService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.dataFile = new File(plugin.getDataFolder(), "passports.yml");
        this.exportFile = new File(plugin.getDataFolder(), "web-exports/player-passports.json");
        this.borderStatusFile = new File(plugin.getDataFolder(), "border-status.yml");
        load();
        exportAll();
    }

    public void recordJoin(Player player) {
        ensureProfile(player.getUniqueId(), player.getName());
        data.set(playerPath(player.getUniqueId()) + ".player-name", player.getName());
        data.set(playerPath(player.getUniqueId()) + ".last-seen-at", Instant.now().toString());
        save();
    }

    public void open(Player player) {
        ensureProfile(player.getUniqueId(), player.getName());
        PassportProfile profile = profile(player.getUniqueId(), player.getName());
        Inventory inventory = Bukkit.createInventory(player, 45, MENU_TITLE);
        inventory.setItem(10, item(Material.WRITABLE_BOOK, "&6Passport profile", List.of(
                "&7Player: &f" + profile.playerName(),
                "&7UUID: &f" + shortUuid(profile.uuid()),
                "&7Passport issued: &f" + shortDate(profile.issuedAt()),
                "&8VS1 identity record"
        )));
        inventory.setItem(12, item(Material.GLOBE_BANNER_PATTERN, "&bCitizenship", List.of(
                "&7Home country: &f" + emptyLabel(profile.citizenshipCountry(), "Not selected"),
                "&7Status: &f" + emptyLabel(profile.citizenshipStatus(), "pending"),
                "&7Granted: &f" + emptyLabel(shortDate(profile.citizenshipGrantedAt()), "-")
        )));
        inventory.setItem(14, item(Material.MAP, "&eVisas", visaLore(profile)));
        inventory.setItem(16, item(Material.EMERALD, "&aCountry reputation", reputationLore(profile)));
        inventory.setItem(30, item(Material.NAME_TAG, "&fVerification status", List.of(
                "&7Discord verification is handled",
                "&7from EarthOS -> Discord Verification.",
                "&8TODO VS1: mirror linked state when final source is stable."
        )));
        inventory.setItem(34, item(Material.BARRIER, "&cBorder access", borderStatusLore(player)));
        inventory.setItem(32, item(Material.BARRIER, "&cClose", List.of("&7Passport VS1 is read-only for players.")));
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot) {
        if (slot == 32) {
            player.closeInventory();
            return;
        }
        notifications.send(player, "&6Passport V1");
        notifications.send(player, "&7Citizenship, visas and reputation are managed by staff while border gameplay is being tested.");
    }

    public boolean setCitizenship(String playerName, String country, String status) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (player.getUniqueId() == null) {
            return false;
        }
        String normalizedCountry = normalizeId(country);
        String normalizedStatus = status == null || status.isBlank() ? "active" : normalizeId(status);
        ensureProfile(player.getUniqueId(), playerName);
        String path = playerPath(player.getUniqueId()) + ".citizenship";
        data.set(path + ".country", normalizedCountry);
        data.set(path + ".status", normalizedStatus);
        data.set(path + ".granted-at", Instant.now().toString());
        save();
        return true;
    }

    public boolean addVisa(String playerName, String country, String type, String status, String expiresAt) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (player.getUniqueId() == null) {
            return false;
        }
        String normalizedCountry = normalizeId(country);
        ensureProfile(player.getUniqueId(), playerName);
        String path = playerPath(player.getUniqueId()) + ".visas." + normalizedCountry;
        data.set(path + ".country", normalizedCountry);
        data.set(path + ".type", normalizeId(type == null || type.isBlank() ? "visitor" : type));
        data.set(path + ".status", normalizeId(status == null || status.isBlank() ? "active" : status));
        data.set(path + ".issued-at", Instant.now().toString());
        data.set(path + ".expires-at", expiresAt == null ? "" : expiresAt.trim());
        save();
        return true;
    }

    public boolean setReputation(String playerName, String country, int value) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (player.getUniqueId() == null) {
            return false;
        }
        ensureProfile(player.getUniqueId(), playerName);
        data.set(playerPath(player.getUniqueId()) + ".reputation." + normalizeId(country), Math.max(-100, Math.min(100, value)));
        save();
        return true;
    }

    public PassportProfile profile(UUID uuid, String fallbackName) {
        ensureProfile(uuid, fallbackName);
        String path = playerPath(uuid);
        Map<String, PassportProfile.VisaEntry> visas = new LinkedHashMap<>();
        ConfigurationSection visaSection = data.getConfigurationSection(path + ".visas");
        if (visaSection != null) {
            for (String country : visaSection.getKeys(false)) {
                String visaPath = path + ".visas." + country;
                visas.put(country, new PassportProfile.VisaEntry(
                        data.getString(visaPath + ".country", country),
                        data.getString(visaPath + ".type", "visitor"),
                        data.getString(visaPath + ".status", "active"),
                        data.getString(visaPath + ".issued-at", ""),
                        data.getString(visaPath + ".expires-at", "")
                ));
            }
        }

        Map<String, Integer> reputation = new LinkedHashMap<>();
        ConfigurationSection reputationSection = data.getConfigurationSection(path + ".reputation");
        if (reputationSection != null) {
            for (String country : reputationSection.getKeys(false)) {
                reputation.put(country, data.getInt(path + ".reputation." + country, 0));
            }
        }

        return new PassportProfile(
                uuid.toString(),
                data.getString(path + ".player-name", fallbackName),
                data.getString(path + ".issued-at", ""),
                data.getString(path + ".citizenship.country", ""),
                data.getString(path + ".citizenship.status", "pending"),
                data.getString(path + ".citizenship.granted-at", ""),
                visas,
                reputation
        );
    }

    public void exportAll() {
        try {
            exportFile.getParentFile().mkdirs();
            Files.writeString(exportFile.toPath(), passportProfilesJson(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            notifications.console("Could not export passport JSON: " + exception.getMessage());
        }
    }

    public String passportProfilesJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(jsonEscape(Instant.now().toString())).append("\",\n");
        json.append("  \"players\": [\n");

        ConfigurationSection section = data.getConfigurationSection("players");
        if (section != null) {
            int index = 0;
            int size = section.getKeys(false).size();
            for (String uuidText : section.getKeys(false)) {
                UUID uuid = parseUuid(uuidText);
                if (uuid == null) {
                    continue;
                }
                PassportProfile profile = profile(uuid, "Unknown");
                json.append("    ").append(profileJson(profile));
                if (index < size - 1) {
                    json.append(",");
                }
                json.append("\n");
                index++;
            }
        }

        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private String profileJson(PassportProfile profile) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("      \"minecraftUuid\": \"").append(jsonEscape(profile.uuid())).append("\",\n");
        json.append("      \"playerName\": \"").append(jsonEscape(profile.playerName())).append("\",\n");
        json.append("      \"issuedAt\": \"").append(jsonEscape(profile.issuedAt())).append("\",\n");
        json.append("      \"citizenship\": {\n");
        json.append("        \"country\": \"").append(jsonEscape(profile.citizenshipCountry())).append("\",\n");
        json.append("        \"status\": \"").append(jsonEscape(profile.citizenshipStatus())).append("\",\n");
        json.append("        \"grantedAt\": \"").append(jsonEscape(profile.citizenshipGrantedAt())).append("\"\n");
        json.append("      },\n");
        json.append("      \"visas\": [\n");
        int visaIndex = 0;
        for (PassportProfile.VisaEntry visa : profile.visas().values()) {
            json.append("        {\"country\": \"").append(jsonEscape(visa.country()))
                    .append("\", \"type\": \"").append(jsonEscape(visa.type()))
                    .append("\", \"status\": \"").append(jsonEscape(visa.status()))
                    .append("\", \"issuedAt\": \"").append(jsonEscape(visa.issuedAt()))
                    .append("\", \"expiresAt\": \"").append(jsonEscape(visa.expiresAt())).append("\"}");
            if (visaIndex < profile.visas().size() - 1) {
                json.append(",");
            }
            json.append("\n");
            visaIndex++;
        }
        json.append("      ],\n");
        json.append("      \"reputation\": {");
        int repIndex = 0;
        for (Map.Entry<String, Integer> entry : profile.reputation().entrySet()) {
            if (repIndex > 0) {
                json.append(", ");
            }
            json.append("\"").append(jsonEscape(entry.getKey())).append("\": ").append(entry.getValue());
            repIndex++;
        }
        json.append("}\n");
        json.append("    }");
        return json.toString();
    }

    private List<String> visaLore(PassportProfile profile) {
        if (profile.visas().isEmpty()) {
            return List.of("&7No active visas yet.", "&8Staff can add visas from commands.");
        }
        List<String> lore = new ArrayList<>();
        for (PassportProfile.VisaEntry visa : profile.visas().values()) {
            lore.add("&f" + visa.country() + " &8- &e" + visa.type() + " &7(" + visa.status() + ")");
        }
        return lore;
    }

    private List<String> reputationLore(PassportProfile profile) {
        if (profile.reputation().isEmpty()) {
            return List.of("&7No country reputation yet.", "&8Range will be -100 to +100.");
        }
        List<String> lore = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : profile.reputation().entrySet()) {
            lore.add("&f" + entry.getKey() + ": &a" + entry.getValue());
        }
        return lore;
    }

    private List<String> borderStatusLore(Player player) {
        if (!borderStatusFile.exists()) {
            return List.of(
                    "&7Current country: &fUnknown",
                    "&7Access: &fUnknown",
                    "&8PassportBorders has not exported status yet."
            );
        }
        YamlConfiguration borderStatus = YamlConfiguration.loadConfiguration(borderStatusFile);
        String path = "players." + player.getUniqueId();
        String country = borderStatus.getString(path + ".country-name", "");
        boolean allowed = borderStatus.getBoolean(path + ".allowed", true);
        String requiredVisa = borderStatus.getString(path + ".required-visa", "");
        String updatedAt = shortDate(borderStatus.getString(path + ".updated-at", ""));
        List<String> lore = new ArrayList<>();
        lore.add("&7Current country: &f" + emptyLabel(country, "Wilderness / unknown"));
        lore.add(allowed ? "&7Access: &aAllowed" : "&7Access: &cDenied");
        lore.add("&7Required visa: &f" + emptyLabel(requiredVisa, "-"));
        lore.add("&7Updated: &f" + emptyLabel(updatedAt, "-"));
        lore.add("&8Live border status from PassportBorders.");
        return lore;
    }

    private void ensureProfile(UUID uuid, String playerName) {
        String path = playerPath(uuid);
        if (data.getString(path + ".issued-at", "").isBlank()) {
            data.set(path + ".issued-at", Instant.now().toString());
        }
        if (data.getString(path + ".player-name", "").isBlank()) {
            data.set(path + ".player-name", playerName);
        }
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
            exportAll();
        } catch (IOException exception) {
            notifications.console("Could not save passports.yml: " + exception.getMessage());
        }
    }

    private ItemStack item(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(name));
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(notifications.color(line));
        }
        meta.setLore(coloredLore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private String playerPath(UUID uuid) {
        return "players." + uuid;
    }

    private String normalizeId(String value) {
        return (value == null ? "" : value.trim().toLowerCase(Locale.ROOT))
                .replaceAll("[^a-z0-9_-]", "-");
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String shortDate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.length() >= 10 ? value.substring(0, 10) : value;
    }

    private String shortUuid(String value) {
        return value == null || value.length() < 8 ? "" : value.substring(0, 8) + "...";
    }

    private String emptyLabel(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
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
