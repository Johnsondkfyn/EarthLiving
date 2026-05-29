package dk.earthliving.core.command;

import dk.earthliving.core.build.BorderControlBuildGenerator;
import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.preview.PlacementPreviewService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public final class EarthLivingBuildCommand implements CommandExecutor, TabCompleter {
    private final NotificationService notifications;
    private final BorderControlBuildGenerator borderControlGenerator;
    private final PlacementPreviewService placementPreviewService;

    public EarthLivingBuildCommand(NotificationService notifications, BorderControlBuildGenerator borderControlGenerator, PlacementPreviewService placementPreviewService) {
        this.notifications = notifications;
        this.borderControlGenerator = borderControlGenerator;
        this.placementPreviewService = placementPreviewService;
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
            notifications.send(sender, "&eUsage: /" + label + " bordercontrol <preview|confirm>");
            return true;
        }

        if (args.length >= 2 && args[1].equalsIgnoreCase("preview")) {
            placementPreviewService.showLook(player, 21, 8, 21, 0, 90, 80,
                    (target, origin) -> borderControlGenerator.generateAt(target, origin, true));
            notifications.send(sender, "&7Preview is 21x8x21. Country A is north/negative Z, Country B is south/positive Z.");
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
            return List.of("preview", "confirm").stream()
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
