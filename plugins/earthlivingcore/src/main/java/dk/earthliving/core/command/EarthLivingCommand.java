package dk.earthliving.core.command;

import dk.earthliving.core.EarthLivingCorePlugin;
import dk.earthliving.core.module.CoreModule;
import dk.earthliving.core.notification.NotificationService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class EarthLivingCommand implements CommandExecutor, TabCompleter {
    private final EarthLivingCorePlugin plugin;

    public EarthLivingCommand(EarthLivingCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        NotificationService notifications = plugin.notificationService();

        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            notifications.send(sender, "&aEarthLivingCore &f" + plugin.getPluginMeta().getVersion() + " &ais running.");
            notifications.send(sender, "&7Enabled modules: &f" + plugin.moduleRegistry().enabledModules().size());
            return true;
        }

        if (args[0].equalsIgnoreCase("modules")) {
            notifications.send(sender, "&6Earth Living modules:");
            for (CoreModule module : plugin.moduleRegistry().modules()) {
                String state = module.enabled() ? "&aenabled" : "&cdisabled";
                notifications.send(sender, "&7- &f" + module.id() + " &8(" + state + "&8) &7" + module.description());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reports")) {
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to view reports.");
                return true;
            }
            notifications.send(sender, "&dOpen reports: &f" + plugin.reportService().openReportCount());
            notifications.send(sender, "&7Reports are saved in the plugin data folder as reports.yml.");
            return true;
        }

        if (args[0].equalsIgnoreCase("earthos")) {
            if (!(sender instanceof Player player)) {
                notifications.send(sender, "&cOnly players can open EarthOS.");
                return true;
            }
            plugin.earthOsService().open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to reload EarthLivingCore.");
                return true;
            }
            plugin.reloadCore();
            notifications.send(sender, "&aEarthLivingCore reloaded.");
            return true;
        }

        notifications.send(sender, "&eUsage: /" + label + " <status|modules|reports|earthos|reload>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return List.of();
        }

        List<String> options = new ArrayList<>(List.of("status", "modules", "earthos"));
        if (sender.hasPermission("earthliving.admin")) {
            options.add("reports");
            options.add("reload");
        }

        String input = args[0].toLowerCase();
        return options.stream().filter(option -> option.startsWith(input)).toList();
    }
}
