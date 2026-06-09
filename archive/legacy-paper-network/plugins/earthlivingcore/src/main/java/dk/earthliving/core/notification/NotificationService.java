package dk.earthliving.core.notification;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class NotificationService {
    private final JavaPlugin plugin;
    private String prefix;

    public NotificationService(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadPrefix();
    }

    public void reloadPrefix() {
        prefix = color(plugin.getConfig().getString("messages.prefix", "&8[&6Earth Living&8] &r"));
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(prefix + color(message));
    }

    public void send(Player player, String message) {
        player.sendMessage(prefix + color(message));
    }

    public void console(String message) {
        plugin.getLogger().info(ChatColor.stripColor(color(message)));
    }

    public String color(String value) {
        return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value);
    }
}
