package dk.earthliving.core.webportal;

import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.report.ReportService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class WebPortalService {
    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final ReportService reports;
    private final File dataFile;
    private final File exportDirectory;
    private final File serverStatusFile;
    private final File profilesFile;
    private final File reportSummariesFile;
    private final Map<UUID, Boolean> pendingLinkInputs = new ConcurrentHashMap<>();
    private FileConfiguration data;
    private BukkitTask exportTask;

    public WebPortalService(JavaPlugin plugin, NotificationService notifications, ReportService reports) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.reports = reports;
        this.dataFile = new File(plugin.getDataFolder(), "web-portal.yml");
        this.exportDirectory = new File(plugin.getDataFolder(), "web-exports");
        this.serverStatusFile = new File(exportDirectory, "server-status.json");
        this.profilesFile = new File(exportDirectory, "player-profiles.json");
        this.reportSummariesFile = new File(exportDirectory, "player-report-summaries.json");
        load();
        exportAll();
    }

    public void startExporter() {
        if (exportTask != null || !enabled()) {
            return;
        }

        long intervalTicks = Math.max(20L, plugin.getConfig().getLong("webportal.export-interval-seconds", 30L) * 20L);
        exportTask = Bukkit.getScheduler().runTaskTimer(plugin, this::exportAll, intervalTicks, intervalTicks);
    }

    public void stopExporter() {
        if (exportTask != null) {
            exportTask.cancel();
            exportTask = null;
        }
    }

    public boolean enabled() {
        return plugin.getConfig().getBoolean("webportal.enabled", true);
    }

    public String createLinkCode(String profileId) {
        String normalizedProfileId = normalizeProfileId(profileId);
        String code = randomCode();
        String path = "link-codes." + code;
        data.set(path + ".profile-id", normalizedProfileId);
        data.set(path + ".created-at", Instant.now().toString());
        data.set(path + ".expires-at", Instant.now().plus(linkCodeLifetime()).toString());
        save();
        return code;
    }

    public boolean beginLinkInput(Player player) {
        if (!enabled()) {
            notifications.send(player, "&cWebsite profile linking is disabled.");
            return false;
        }

        pendingLinkInputs.put(player.getUniqueId(), true);
        notifications.send(player, "&bWebsite profile link");
        notifications.send(player, "&7Write your one-time website code in chat now.");
        notifications.send(player, "&8Type &fcancel &8to stop. Your next chat message will not be sent publicly.");
        return true;
    }

    public boolean hasPendingLinkInput(Player player) {
        return pendingLinkInputs.containsKey(player.getUniqueId());
    }

    public void submitLinkInput(Player player, String message) {
        pendingLinkInputs.remove(player.getUniqueId());
        String code = normalizeCode(message);
        if ("cancel".equalsIgnoreCase(message.trim())) {
            notifications.send(player, "&eWebsite profile link cancelled.");
            return;
        }

        LinkResult result = linkPlayer(player, code);
        switch (result) {
            case LINKED -> {
                notifications.send(player, "&aYour website profile is now linked.");
                notifications.send(player, "&7Profile: &f" + linkedProfileId(player.getUniqueId()));
            }
            case EXPIRED -> notifications.send(player, "&cThat link code has expired.");
            case UNKNOWN -> notifications.send(player, "&cThat link code was not found.");
            case ALREADY_USED -> notifications.send(player, "&cThat link code has already been used.");
            case INVALID -> notifications.send(player, "&cThat code is invalid.");
        }
    }

    public LinkResult linkPlayer(Player player, String code) {
        if (code.isBlank()) {
            return LinkResult.INVALID;
        }

        String path = "link-codes." + code;
        if (!data.isConfigurationSection(path)) {
            return LinkResult.UNKNOWN;
        }
        if (data.getBoolean(path + ".used", false)) {
            return LinkResult.ALREADY_USED;
        }

        Instant expiresAt = parseInstant(data.getString(path + ".expires-at", ""));
        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            data.set(path + ".expired", true);
            save();
            return LinkResult.EXPIRED;
        }

        String profileId = data.getString(path + ".profile-id", "");
        String playerPath = "linked-profiles." + player.getUniqueId();
        data.set(playerPath + ".profile-id", profileId);
        data.set(playerPath + ".minecraft-uuid", player.getUniqueId().toString());
        data.set(playerPath + ".player-name", player.getName());
        data.set(playerPath + ".linked-at", Instant.now().toString());
        data.set(playerPath + ".last-seen-at", Instant.now().toString());
        data.set(path + ".used", true);
        data.set(path + ".used-by-uuid", player.getUniqueId().toString());
        data.set(path + ".used-by-name", player.getName());
        data.set(path + ".used-at", Instant.now().toString());
        save();
        notifications.console("Website profile " + profileId + " linked to " + player.getName() + ".");
        return LinkResult.LINKED;
    }

    public void recordJoin(Player player) {
        String path = "known-players." + player.getUniqueId();
        data.set(path + ".minecraft-uuid", player.getUniqueId().toString());
        data.set(path + ".player-name", player.getName());
        data.set(path + ".last-seen-at", Instant.now().toString());
        if (data.getString(path + ".first-seen-at", "").isBlank()) {
            data.set(path + ".first-seen-at", Instant.now().toString());
        }
        save();
    }

    public String linkedProfileId(UUID uuid) {
        return data.getString("linked-profiles." + uuid + ".profile-id", "");
    }

    public int linkedProfileCount() {
        ConfigurationSection section = data.getConfigurationSection("linked-profiles");
        return section == null ? 0 : section.getKeys(false).size();
    }

    public void exportAll() {
        try {
            exportDirectory.mkdirs();
            Files.writeString(serverStatusFile.toPath(), serverStatusJson(), StandardCharsets.UTF_8);
            Files.writeString(profilesFile.toPath(), playerProfilesJson(), StandardCharsets.UTF_8);
            Files.writeString(reportSummariesFile.toPath(), playerReportSummariesJson(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            notifications.console("Could not export web portal JSON: " + exception.getMessage());
        }
    }

    private String serverStatusJson() {
        return "{\n"
                + "  \"generatedAt\": \"" + jsonEscape(Instant.now().toString()) + "\",\n"
                + "  \"server\": \"" + jsonEscape(plugin.getConfig().getString("webportal.public-server-name", "Earth Living Main")) + "\",\n"
                + "  \"phase\": \"" + jsonEscape(plugin.getConfig().getString("webportal.phase", "private-test")) + "\",\n"
                + "  \"onlinePlayers\": " + Bukkit.getOnlinePlayers().size() + ",\n"
                + "  \"maxPlayers\": " + plugin.getConfig().getInt("webportal.max-players", Bukkit.getMaxPlayers()) + ",\n"
                + "  \"coreVersion\": \"" + jsonEscape(plugin.getPluginMeta().getVersion()) + "\",\n"
                + "  \"mapStatus\": \"" + jsonEscape(plugin.getConfig().getString("webportal.map-status", "foundation-live")) + "\",\n"
                + "  \"linkedProfiles\": " + linkedProfileCount() + "\n"
                + "}\n";
    }

    private String playerProfilesJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(jsonEscape(Instant.now().toString())).append("\",\n");
        json.append("  \"profiles\": [\n");

        ConfigurationSection section = data.getConfigurationSection("linked-profiles");
        if (section != null) {
            int index = 0;
            int size = section.getKeys(false).size();
            for (String uuid : section.getKeys(false)) {
                String path = "linked-profiles." + uuid;
                Player onlinePlayer = Bukkit.getPlayer(UUID.fromString(uuid));
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                String playerName = onlinePlayer == null ? data.getString(path + ".player-name", "Unknown") : onlinePlayer.getName();
                int playTicks = onlinePlayer == null
                        ? offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE)
                        : onlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
                json.append("    {\n");
                json.append("      \"profileId\": \"").append(jsonEscape(data.getString(path + ".profile-id", ""))).append("\",\n");
                json.append("      \"minecraftUuid\": \"").append(jsonEscape(uuid)).append("\",\n");
                json.append("      \"playerName\": \"").append(jsonEscape(playerName)).append("\",\n");
                json.append("      \"linkedAt\": \"").append(jsonEscape(data.getString(path + ".linked-at", ""))).append("\",\n");
                json.append("      \"lastSeenAt\": \"").append(jsonEscape(data.getString(path + ".last-seen-at", ""))).append("\",\n");
                json.append("      \"online\": ").append(onlinePlayer != null).append(",\n");
                json.append("      \"playtimeSeconds\": ").append(playTicks / 20).append(",\n");
                json.append("      \"openReports\": ").append(reports.openReportCountForPlayer(uuid)).append(",\n");
                json.append("      \"totalReports\": ").append(reports.totalReportCountForPlayer(uuid)).append("\n");
                json.append("    }");
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

    private String playerReportSummariesJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(jsonEscape(Instant.now().toString())).append("\",\n");
        json.append("  \"players\": [\n");

        ConfigurationSection section = data.getConfigurationSection("linked-profiles");
        if (section != null) {
            int index = 0;
            int size = section.getKeys(false).size();
            for (String uuid : section.getKeys(false)) {
                String path = "linked-profiles." + uuid;
                json.append("    {\n");
                json.append("      \"profileId\": \"").append(jsonEscape(data.getString(path + ".profile-id", ""))).append("\",\n");
                json.append("      \"minecraftUuid\": \"").append(jsonEscape(uuid)).append("\",\n");
                json.append("      \"playerName\": \"").append(jsonEscape(data.getString(path + ".player-name", "Unknown"))).append("\",\n");
                json.append("      \"openReports\": ").append(reports.openReportCountForPlayer(uuid)).append(",\n");
                json.append("      \"totalReports\": ").append(reports.totalReportCountForPlayer(uuid)).append(",\n");
                json.append("      \"reports\": ").append(reports.playerReportsJson(uuid)).append("\n");
                json.append("    }");
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
            notifications.console("Could not save web-portal.yml: " + exception.getMessage());
        }
    }

    private Duration linkCodeLifetime() {
        return Duration.ofMinutes(Math.max(1, plugin.getConfig().getLong("webportal.link-code-minutes", 10L)));
    }

    private String normalizeProfileId(String profileId) {
        String normalized = profileId == null ? "" : profileId.trim();
        if (normalized.isBlank()) {
            return "profile-" + Instant.now().toEpochMilli();
        }
        return normalized.replaceAll("[^A-Za-z0-9_.@-]", "-");
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT).replace("-", "");
    }

    private String randomCode() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int index = 0; index < 8; index++) {
            code.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }
        return code.toString();
    }

    private Instant parseInstant(String value) {
        try {
            return value == null || value.isBlank() ? null : Instant.parse(value);
        } catch (Exception exception) {
            return null;
        }
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

    public enum LinkResult {
        LINKED,
        EXPIRED,
        UNKNOWN,
        ALREADY_USED,
        INVALID
    }
}
