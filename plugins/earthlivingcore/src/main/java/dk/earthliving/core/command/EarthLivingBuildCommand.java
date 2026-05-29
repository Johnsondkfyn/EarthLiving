package dk.earthliving.core.command;

import dk.earthliving.core.build.BorderControlBuildGenerator;
import dk.earthliving.core.notification.NotificationService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public final class EarthLivingBuildCommand implements CommandExecutor, TabCompleter {
    private final NotificationService notifications;
    private final BorderControlBuildGenerator borderControlGenerator;

    public EarthLivingBuildCommand(NotificationService notifications, BorderControlBuildGenerator borderControlGenerator) {
        this.notifications = notifications;
        this.borderControlGenerator = borderControlGenerator;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            notifications.send(sender, "&cOnly players can use developer build generators.");
            return true;
        }

        if (!sender.hasPermission("earthliving.build.bordercontrol")) {
            notifications.send(sender, "&cYou do not have permission to use this build generator.");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("bordercontrol")) {
            notifications.send(sender, "&eUsage: /" + label + " bordercontrol [confirm]");
            return true;
        }

        boolean confirmed = args.length >= 2 && args[1].equalsIgnoreCase("confirm");
        borderControlGenerator.generate(player, confirmed);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("earthliving.build.bordercontrol")) {
            return List.of();
        }
        if (args.length == 1) {
            return List.of("bordercontrol").stream()
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("bordercontrol")) {
            return List.of("confirm").stream()
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
