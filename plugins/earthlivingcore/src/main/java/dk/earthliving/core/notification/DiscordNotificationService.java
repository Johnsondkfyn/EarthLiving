package dk.earthliving.core.notification;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class DiscordNotificationService {
    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final HttpClient httpClient;

    public DiscordNotificationService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void reportCreated(int id, String categoryTitle, String playerName, String world, int x, int y, int z, String note) {
        if (!isReportWebhookEnabled()) {
            return;
        }

        String webhookUrl = plugin.getConfig().getString("discord.report-notifications.webhook-url", "").trim();
        if (webhookUrl.isBlank()) {
            return;
        }

        String payload = reportPayload(id, categoryTitle, playerName, world, x, y, z, note);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendWebhook(webhookUrl, payload, id));
    }

    private boolean isReportWebhookEnabled() {
        return plugin.getConfig().getBoolean("modules.discord", false)
                && plugin.getConfig().getBoolean("discord.report-notifications.enabled", false);
    }

    private void sendWebhook(String webhookUrl, String payload, int reportId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                notifications.console("Discord report notification for #" + reportId + " failed with HTTP " + response.statusCode() + ".");
            }
        } catch (IllegalArgumentException | IOException exception) {
            notifications.console("Discord report notification for #" + reportId + " failed: " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            notifications.console("Discord report notification for #" + reportId + " was interrupted.");
        }
    }

    private String reportPayload(int id, String categoryTitle, String playerName, String world, int x, int y, int z, String note) {
        String location = world + " " + x + " " + y + " " + z;
        String title = "New EarthLiving report #" + id;
        String description = "**Category:** " + categoryTitle
                + "\\n**Player:** " + playerName
                + "\\n**Location:** " + location
                + "\\n**Note:** " + shorten(note, 900);

        return "{"
                + "\"username\":\"EarthLiving Reports\","
                + "\"allowed_mentions\":{\"parse\":[]},"
                + "\"embeds\":[{"
                + "\"title\":\"" + jsonEscape(title) + "\","
                + "\"description\":\"" + jsonEscape(description) + "\","
                + "\"color\":16753920"
                + "}]"
                + "}";
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
