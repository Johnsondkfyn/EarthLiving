package dk.earthliving.core.placeholder;

import dk.earthliving.core.passport.PassportProfile;
import dk.earthliving.core.passport.PassportService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public final class EarthLivingPlaceholderExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final PassportService passportService;

    public EarthLivingPlaceholderExpansion(JavaPlugin plugin, PassportService passportService) {
        this.plugin = plugin;
        this.passportService = passportService;
    }

    @Override
    public String getIdentifier() {
        return "earthliving";
    }

    @Override
    public String getAuthor() {
        return "Codex";
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        String key = params == null ? "" : params.toLowerCase();
        if ("current_server".equals(key)) {
            return plugin.getConfig().getString("tab.current-server-name",
                    plugin.getConfig().getString("webportal.public-server-name", "EarthLiving"));
        }

        if (offlinePlayer == null) {
            return fallback(key);
        }

        return switch (key) {
            case "current_country" -> passportService.borderStatus(offlinePlayer.getUniqueId()).country();
            case "border_access" -> passportService.borderStatus(offlinePlayer.getUniqueId()).access();
            case "required_visa" -> passportService.borderStatus(offlinePlayer.getUniqueId()).requiredVisa();
            case "passport_status" -> passportService.passportStatus(
                    offlinePlayer.getUniqueId(),
                    offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName()
            );
            case "verified_status" -> verifiedStatus(offlinePlayer);
            default -> "";
        };
    }

    private String fallback(String key) {
        return switch (key) {
            case "current_country" -> "Unknown country";
            case "border_access" -> "Access unknown";
            case "required_visa" -> "No visa required";
            case "passport_status" -> "No passport data";
            case "verified_status" -> "Unverified";
            default -> "";
        };
    }

    private String verifiedStatus(OfflinePlayer offlinePlayer) {
        if (offlinePlayer instanceof Player player && player.hasPermission("earthliving.verified")) {
            return "Verified";
        }

        String discordId = discordSrvLinkedId(offlinePlayer);
        return discordId == null || discordId.isBlank() ? "Unverified" : "Verified";
    }

    private String discordSrvLinkedId(OfflinePlayer offlinePlayer) {
        Plugin discordSrv = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSrv == null || !discordSrv.isEnabled()) {
            return "";
        }
        try {
            Class<?> discordSrvClass = Class.forName("github.scarsz.discordsrv.DiscordSRV");
            Method getPlugin = discordSrvClass.getMethod("getPlugin");
            Object instance = getPlugin.invoke(null);
            Method getAccountLinkManager = instance.getClass().getMethod("getAccountLinkManager");
            Object accountLinkManager = getAccountLinkManager.invoke(instance);
            Method getDiscordId = accountLinkManager.getClass().getMethod("getDiscordId", java.util.UUID.class);
            Object result = getDiscordId.invoke(accountLinkManager, offlinePlayer.getUniqueId());
            return result == null ? "" : String.valueOf(result);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return "";
        }
    }
}
