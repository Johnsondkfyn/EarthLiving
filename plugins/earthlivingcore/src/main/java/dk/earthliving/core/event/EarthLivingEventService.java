package dk.earthliving.core.event;

import dk.earthliving.core.discord.DiscordBridgeService;
import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public final class EarthLivingEventService {
    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final DiscordBridgeService discord;
    private BukkitTask restartTask;

    public EarthLivingEventService(JavaPlugin plugin, NotificationService notifications, DiscordBridgeService discord) {
        this.plugin = plugin;
        this.notifications = notifications;
        this.discord = discord;
    }

    public void announceEvent(CommandSender sender, String message) {
        String cleanMessage = message == null || message.isBlank()
                ? plugin.getConfig().getString("discord.events.default-message", "A random Earth Living event has started.")
                : message.trim();

        String minecraftMessage = plugin.getConfig().getString("discord.events.minecraft-format",
                "&6[Event] &f{message}");
        String discordMessage = plugin.getConfig().getString("discord.events.discord-format",
                "**Random event started**\n{message}");

        broadcast(minecraftMessage.replace("{message}", cleanMessage));
        boolean sent = discord.send("events", discordText(discordMessage, cleanMessage));

        notifications.send(sender, sent
                ? "&aEvent announcement sent to Minecraft and Discord."
                : "&eEvent announced in Minecraft. DiscordSRV is not available.");
    }

    public void scheduleRestart(CommandSender sender, int minutes, String reason) {
        int safeMinutes = Math.max(1, minutes);
        String cleanReason = reason == null || reason.isBlank()
                ? plugin.getConfig().getString("discord.restart.default-reason", "Server maintenance")
                : reason.trim();

        if (restartTask != null) {
            restartTask.cancel();
            restartTask = null;
        }

        long totalSeconds = safeMinutes * 60L;
        restartTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            private long remaining = totalSeconds;

            @Override
            public void run() {
                if (remaining <= 0) {
                    announceRestartNow(cleanReason);
                    restartTask.cancel();
                    restartTask = null;
                    if (plugin.getConfig().getBoolean("discord.restart.execute-command.enabled", false)) {
                        String command = plugin.getConfig().getString("discord.restart.execute-command.command", "restart");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                    return;
                }

                if (shouldAnnounce(remaining)) {
                    announceRestartCountdown(remaining, cleanReason);
                }

                remaining -= 10;
            }
        }, 0L, 20L * 10L);

        notifications.send(sender, "&aRestart countdown scheduled for &f" + safeMinutes + " minute(s)&a.");
    }

    public void cancelRestart(CommandSender sender) {
        if (restartTask == null) {
            notifications.send(sender, "&eNo Earth Living restart countdown is active.");
            return;
        }

        restartTask.cancel();
        restartTask = null;
        String message = plugin.getConfig().getString("discord.restart.cancelled-format",
                "**Restart cancelled**");
        broadcast("&aRestart countdown cancelled.");
        discord.send("status", message);
        notifications.send(sender, "&aRestart countdown cancelled.");
    }

    private boolean shouldAnnounce(long remainingSeconds) {
        List<Integer> points = plugin.getConfig().getIntegerList("discord.restart.warning-seconds");
        return points.contains((int) remainingSeconds);
    }

    private void announceRestartCountdown(long remainingSeconds, String reason) {
        String time = formatDuration(remainingSeconds);
        String minecraftFormat = plugin.getConfig().getString("discord.restart.minecraft-format",
                "&cRestart in &f{time}&c: &7{reason}");
        String discordFormat = plugin.getConfig().getString("discord.restart.discord-format",
                "**Restart in {time}**\n{reason}");

        broadcast(minecraftFormat
                .replace("{time}", time)
                .replace("{reason}", reason));
        discord.send("status", discordFormat
                .replace("{time}", time)
                .replace("{reason}", reason)
                .replace("\\n", "\n"));
    }

    private void announceRestartNow(String reason) {
        String minecraftFormat = plugin.getConfig().getString("discord.restart.now-minecraft-format",
                "&cRestarting now: &7{reason}");
        String discordFormat = plugin.getConfig().getString("discord.restart.now-discord-format",
                "**Restarting now**\n{reason}");

        broadcast(minecraftFormat.replace("{reason}", reason));
        discord.send("status", discordFormat
                .replace("{reason}", reason)
                .replace("\\n", "\n"));
    }

    private void broadcast(String message) {
        Bukkit.broadcastMessage(notifications.color(message));
    }

    private String discordText(String format, String message) {
        return format
                .replace("{message}", message)
                .replace("\\n", "\n");
    }

    private String formatDuration(long seconds) {
        if (seconds >= 60 && seconds % 60 == 0) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        }
        if (seconds >= 60) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        }
        return seconds + " seconds";
    }
}
