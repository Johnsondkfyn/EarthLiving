package dk.earthliving.core.discord;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public final class DiscordBridgeService {
    private final JavaPlugin plugin;
    private final NotificationService notifications;

    public DiscordBridgeService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    public boolean enabled() {
        return plugin.getConfig().getBoolean("modules.discord", false)
                && plugin.getConfig().getBoolean("discord.discordsrv.enabled", true);
    }

    public boolean send(String channelName, String message) {
        if (!enabled()) {
            return false;
        }

        Plugin discordSrv = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSrv == null || !discordSrv.isEnabled()) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendAsync(channelName, message));
        return true;
    }

    private void sendAsync(String channelName, String message) {
        try {
            Class<?> discordSrvClass = Class.forName("github.scarsz.discordsrv.DiscordSRV");
            Method getPlugin = discordSrvClass.getMethod("getPlugin");
            Object discordSrvPlugin = getPlugin.invoke(null);
            Method getChannel = discordSrvPlugin.getClass().getMethod("getDestinationTextChannelForGameChannelName", String.class);
            Object channel = getChannel.invoke(discordSrvPlugin, channelName);

            if (channel == null) {
                notifications.console("DiscordSRV channel '" + channelName + "' is not available.");
                return;
            }

            Method sendMessage = channel.getClass().getMethod("sendMessage", CharSequence.class);
            Object messageAction = sendMessage.invoke(channel, message);
            Method queue = messageAction.getClass().getMethod("queue");
            queue.invoke(messageAction);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            notifications.console("Could not send DiscordSRV message to '" + channelName + "': " + exception.getMessage());
        }
    }
}
