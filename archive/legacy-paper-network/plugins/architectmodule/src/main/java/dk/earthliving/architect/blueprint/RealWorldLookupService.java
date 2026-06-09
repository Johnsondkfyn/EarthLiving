package dk.earthliving.architect.blueprint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.earthliving.architect.ArchitectModulePlugin;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

public final class RealWorldLookupService {
    private final ArchitectModulePlugin plugin;
    private HttpClient httpClient;
    private String endpoint;
    private String userAgent;
    private Duration timeout;

    public RealWorldLookupService(ArchitectModulePlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Math.max(3, plugin.getConfig().getInt("generation.request-timeout-seconds", 12))))
                .build();
        endpoint = trimSlash(plugin.getConfig().getString("generation.web-endpoint", "https://en.wikipedia.org"));
        userAgent = plugin.getConfig().getString("generation.user-agent",
                "EarthLivingArchitect/0.2 (admin tool; no secrets)");
        timeout = Duration.ofSeconds(Math.max(3, plugin.getConfig().getInt("generation.request-timeout-seconds", 12)));
    }

    public Optional<RealWorldBuildingData> lookup(String query) {
        try {
            String title = searchTitle(query).orElse(query);
            return summary(title);
        } catch (Exception exception) {
            plugin.getLogger().warning("Real-world lookup failed for '" + query + "': " + exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> searchTitle(String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        URI uri = URI.create(endpoint + "/w/api.php?action=query&list=search&srsearch=" + encoded
                + "&srlimit=1&format=json&utf8=1");
        JsonObject root = requestJson(uri);
        JsonArray results = root.getAsJsonObject("query").getAsJsonArray("search");
        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(text(results.get(0).getAsJsonObject(), "title"));
    }

    private Optional<RealWorldBuildingData> summary(String title) throws Exception {
        String encoded = URLEncoder.encode(title, StandardCharsets.UTF_8).replace("+", "%20");
        URI uri = URI.create(endpoint + "/api/rest_v1/page/summary/" + encoded);
        JsonObject root = requestJson(uri);
        String pageTitle = text(root, "title");
        String description = text(root, "description");
        String extract = text(root, "extract");
        String url = "";
        JsonObject contentUrls = object(root, "content_urls");
        if (contentUrls != null) {
            JsonObject desktop = object(contentUrls, "desktop");
            if (desktop != null) {
                url = text(desktop, "page");
            }
        }
        if ((pageTitle == null || pageTitle.isBlank()) && (extract == null || extract.isBlank())) {
            return Optional.empty();
        }
        return Optional.of(new RealWorldBuildingData(
                pageTitle == null ? title : pageTitle,
                description == null ? "" : description,
                extract == null ? "" : extract,
                url == null ? "" : url,
                "Wikipedia"
        ));
    }

    private JsonObject requestJson(URI uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("User-Agent", userAgent)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("HTTP " + response.statusCode() + " from " + uri.getHost());
        }
        JsonElement parsed = JsonParser.parseString(response.body());
        if (!parsed.isJsonObject()) {
            throw new IllegalStateException("Response was not JSON object");
        }
        return parsed.getAsJsonObject();
    }

    private JsonObject object(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element == null || !element.isJsonObject() ? null : element.getAsJsonObject();
    }

    private String text(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    private String trimSlash(String value) {
        if (value == null || value.isBlank()) {
            return "https://en.wikipedia.org";
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
