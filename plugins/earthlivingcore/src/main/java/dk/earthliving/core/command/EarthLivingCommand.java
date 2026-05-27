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

        if (args[0].equalsIgnoreCase("passport")) {
            if (args.length == 1 || args[1].equalsIgnoreCase("info")) {
                if (sender instanceof Player player) {
                    plugin.passportService().open(player);
                } else {
                    notifications.send(sender, "&eUsage: /" + label + " passport <setcitizenship|addvisa|reputation|export>");
                }
                return true;
            }
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to manage passports.");
                return true;
            }
            if (args[1].equalsIgnoreCase("setcitizenship")) {
                if (args.length < 4) {
                    notifications.send(sender, "&eUsage: /" + label + " passport setcitizenship <player> <country> [status]");
                    return true;
                }
                String status = args.length >= 5 ? args[4] : "active";
                boolean changed = plugin.passportService().setCitizenship(args[2], args[3], status);
                notifications.send(sender, changed
                        ? "&aCitizenship set for &f" + args[2] + "&a: &f" + args[3] + " &7(" + status + "&7)"
                        : "&cCould not update passport for " + args[2] + ".");
                return true;
            }
            if (args[1].equalsIgnoreCase("addvisa")) {
                if (args.length < 5) {
                    notifications.send(sender, "&eUsage: /" + label + " passport addvisa <player> <country> <visitor|work|resident|event> [status] [expiresAt]");
                    return true;
                }
                String status = args.length >= 6 ? args[5] : "active";
                String expiresAt = args.length >= 7 ? args[6] : "";
                boolean changed = plugin.passportService().addVisa(args[2], args[3], args[4], status, expiresAt);
                notifications.send(sender, changed
                        ? "&aVisa added for &f" + args[2] + "&a: &f" + args[3] + " / " + args[4]
                        : "&cCould not add visa for " + args[2] + ".");
                return true;
            }
            if (args[1].equalsIgnoreCase("reputation")) {
                if (args.length < 5) {
                    notifications.send(sender, "&eUsage: /" + label + " passport reputation <player> <country> <-100..100>");
                    return true;
                }
                try {
                    boolean changed = plugin.passportService().setReputation(args[2], args[3], Integer.parseInt(args[4]));
                    notifications.send(sender, changed
                            ? "&aReputation updated for &f" + args[2] + "&a in &f" + args[3] + "&a."
                            : "&cCould not update reputation for " + args[2] + ".");
                } catch (NumberFormatException exception) {
                    notifications.send(sender, "&cReputation must be a whole number from -100 to 100.");
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("export")) {
                plugin.passportService().exportAll();
                notifications.send(sender, "&aPassport JSON export refreshed.");
                notifications.send(sender, "&7File: &fplugins/EarthLivingCore/web-exports/player-passports.json");
                return true;
            }
            notifications.send(sender, "&eUsage: /" + label + " passport <info|setcitizenship|addvisa|reputation|export>");
            return true;
        }

        if (args[0].equalsIgnoreCase("preview")) {
            if (!(sender instanceof Player player)) {
                notifications.send(sender, "&cOnly players can use placement previews.");
                return true;
            }
            if (!sender.hasPermission("earthliving.admin")) {
                notifications.send(sender, "&cYou do not have permission to use placement previews.");
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("clear")) {
                plugin.placementPreviewService().clear(player);
                return true;
            }
            int offset = 1;
            boolean useLookTarget = args.length >= 2 && args[1].equalsIgnoreCase("look");
            if (useLookTarget) {
                offset = 2;
            }
            if (args.length < offset + 3) {
                notifications.send(sender, "&eUsage: /" + label + " preview <width> <height> <depth> [yOffset] [seconds]");
                notifications.send(sender, "&eUsage: /" + label + " preview look <width> <height> <depth> [yOffset] [seconds] [distance]");
                notifications.send(sender, "&7Example: /" + label + " preview 25 18 25 -1 60");
                notifications.send(sender, "&7Look example: /" + label + " preview look 25 18 25 0 60");
                return true;
            }
            try {
                int width = Integer.parseInt(args[offset]);
                int height = Integer.parseInt(args[offset + 1]);
                int depth = Integer.parseInt(args[offset + 2]);
                int yOffset = args.length >= offset + 4 ? Integer.parseInt(args[offset + 3]) : 0;
                int seconds = args.length >= offset + 5 ? Integer.parseInt(args[offset + 4]) : 45;
                if (useLookTarget) {
                    int distance = args.length >= offset + 6 ? Integer.parseInt(args[offset + 5]) : 80;
                    plugin.placementPreviewService().showLook(player, width, height, depth, yOffset, seconds, distance);
                } else {
                    plugin.placementPreviewService().show(player, width, height, depth, yOffset, seconds);
                }
            } catch (NumberFormatException exception) {
                notifications.send(sender, "&cWidth, height, depth, yOffset and seconds must be whole numbers.");
            }
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

        notifications.send(sender, "&eUsage: /" + label + " <status|modules|reports|portal|passport|preview|event|restart|earthos|reload>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(List.of("status", "modules", "earthos", "passport"));
            if (sender.hasPermission("earthliving.admin")) {
                options.add("reports");
                options.add("portal");
                options.add("preview");
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

        if (args[0].equalsIgnoreCase("passport") && args.length == 2) {
            List<String> options = sender.hasPermission("earthliving.admin")
                    ? List.of("info", "setcitizenship", "addvisa", "reputation", "export")
                    : List.of("info");
            return options.stream()
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (sender.hasPermission("earthliving.admin")
                && args[0].equalsIgnoreCase("preview")
                && args.length == 2) {
            return List.of("look", "clear", "16", "24", "32", "48").stream()
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (sender.hasPermission("earthliving.admin")
                && args[0].equalsIgnoreCase("passport")
                && args.length == 5
                && args[1].equalsIgnoreCase("addvisa")) {
            return List.of("visitor", "work", "resident", "event").stream()
                    .filter(option -> option.startsWith(args[4].toLowerCase()))
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
