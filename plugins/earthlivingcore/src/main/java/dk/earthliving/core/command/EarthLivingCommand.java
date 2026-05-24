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
            if (args.length >= 4 && args[1].equalsIgnoreCase("set")) {
                try {
                    int reportId = Integer.parseInt(args[2]);
                    String note = args.length >= 5 ? joinArgs(args, 4) : "";
                    boolean changed = plugin.reportService().setReportStatus(reportId, args[3], sender.getName(), note, "command");
                    notifications.send(sender, changed
                            ? "&aReport #" + reportId + " updated to &f" + args[3] + "&a."
                            : "&cCould not update report #" + reportId + ". Use status open, repair-approved or completed.");
                } catch (NumberFormatException exception) {
                    notifications.send(sender, "&cReport id must be a whole number.");
                }
                return true;
            }
            notifications.send(sender, "&dOpen reports: &f" + plugin.reportService().openReportCount());
            notifications.send(sender, "&7Reports are saved in the plugin data folder as reports.yml.");
            notifications.send(sender, "&eUsage: /" + label + " reports set <id> <open|repair-approved|completed> [note]");
            return true;
        }

        if (args[0].equalsIgnoreCase("portal")) {
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to manage the web portal.");
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("code")) {
                String profileId = joinArgs(args, 2);
                String code = plugin.webPortalService().createLinkCode(profileId);
                notifications.send(sender, "&aWebsite link code created: &f" + code);
                notifications.send(sender, "&7Profile id: &f" + profileId);
                notifications.send(sender, "&7The player enters this code in EarthOS -> My EarthLiving.");
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("export")) {
                plugin.webPortalService().exportAll();
                notifications.send(sender, "&aWeb portal JSON exports refreshed.");
                notifications.send(sender, "&7Folder: &fplugins/EarthLivingCore/web-exports/");
                return true;
            }
            notifications.send(sender, "&eUsage: /" + label + " portal code <website-profile-id>");
            notifications.send(sender, "&eUsage: /" + label + " portal export");
            return true;
        }

        if (args[0].equalsIgnoreCase("event")) {
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to announce events.");
                return true;
            }
            if (args.length < 2) {
                notifications.send(sender, "&eUsage: /" + label + " event <message>");
                return true;
            }
            plugin.eventService().announceEvent(sender, joinArgs(args, 1));
            return true;
        }

        if (args[0].equalsIgnoreCase("restart")) {
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to schedule restarts.");
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("cancel")) {
                plugin.eventService().cancelRestart(sender);
                return true;
            }
            if (args.length < 3) {
                notifications.send(sender, "&eUsage: /" + label + " restart <minutes> <reason>");
                notifications.send(sender, "&eUsage: /" + label + " restart cancel");
                return true;
            }
            try {
                plugin.eventService().scheduleRestart(sender, Integer.parseInt(args[1]), joinArgs(args, 2));
            } catch (NumberFormatException exception) {
                notifications.send(sender, "&cMinutes must be a whole number.");
            }
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

        notifications.send(sender, "&eUsage: /" + label + " <status|modules|reports|portal|event|restart|earthos|reload>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(List.of("status", "modules", "earthos"));
            if (sender.hasPermission("earthliving.admin")) {
                options.add("reports");
                options.add("portal");
                options.add("event");
                options.add("restart");
                options.add("reload");
            }

            String input = args[0].toLowerCase();
            return options.stream().filter(option -> option.startsWith(input)).toList();
        }

        if (sender.hasPermission("earthliving.admin")
                && args[0].equalsIgnoreCase("reports")
                && args.length == 2) {
            return List.of("set").stream()
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (sender.hasPermission("earthliving.admin")
                && args[0].equalsIgnoreCase("portal")
                && args.length == 2) {
            return List.of("code", "export").stream()
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (sender.hasPermission("earthliving.admin")
                && args[0].equalsIgnoreCase("reports")
                && args.length == 4) {
            return List.of("open", "repair-approved", "completed").stream()
                    .filter(option -> option.startsWith(args[3].toLowerCase()))
                    .toList();
        }

        return List.of();
    }

    private String joinArgs(String[] args, int startIndex) {
        StringBuilder joined = new StringBuilder();
        for (int index = startIndex; index < args.length; index++) {
            if (joined.length() > 0) {
                joined.append(' ');
            }
            joined.append(args[index]);
        }
        return joined.toString();
    }
}
