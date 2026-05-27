package dk.earthliving.architect.command;

import dk.earthliving.architect.ArchitectModulePlugin;
import dk.earthliving.architect.blueprint.ArchitectService;
import dk.earthliving.architect.blueprint.BlueprintJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class ArchitectCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION = "earthliving.architect.admin";
    private static final List<String> SUBCOMMANDS = List.of("search", "generate", "preview", "paste", "list", "reload");

    private final ArchitectModulePlugin plugin;
    private final ArchitectService architectService;

    public ArchitectCommand(ArchitectModulePlugin plugin, ArchitectService architectService) {
        this.plugin = plugin;
        this.architectService = architectService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            plugin.tell(sender, "&cDu har ikke adgang til ArchitectModule.");
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);
        String[] remaining = Arrays.copyOfRange(args, 1, args.length);
        switch (subcommand) {
            case "search" -> search(sender, remaining);
            case "generate" -> generate(sender, remaining);
            case "preview" -> preview(sender, remaining);
            case "paste" -> paste(sender, remaining);
            case "list" -> list(sender);
            case "reload" -> reload(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return List.of();
        }
        if (args.length == 1) {
            return startsWith(SUBCOMMANDS, args[0]);
        }
        String subcommand = args[0].toLowerCase(Locale.ROOT);
        if (args.length == 2 && List.of("preview", "paste").contains(subcommand)) {
            return startsWith(architectService.jobs().stream().map(BlueprintJob::id).toList(), args[1]);
        }
        if ("generate".equals(subcommand) && args.length >= 3) {
            return startsWith(architectService.styles(), args[args.length - 1]);
        }
        return List.of();
    }

    private void search(CommandSender sender, String[] args) {
        String query = join(args);
        if (query.isBlank()) {
            plugin.tell(sender, "&cBrug: &f/architect search <building>");
            return;
        }
        plugin.tell(sender, "&7Soger lokalt efter blueprint-type for &f" + query + "&7...");
        architectService.search(sender, query);
    }

    private void generate(CommandSender sender, String[] args) {
        ParsedGeneration parsed = parseGeneration(args);
        if (parsed.query().isBlank()) {
            plugin.tell(sender, "&cBrug: &f/architect generate <building> [scale] [style]");
            return;
        }
        plugin.tell(sender, "&7Generator starter async: &f" + parsed.query()
                + " &8scale=&f" + parsed.scale() + " &8style=&f" + parsed.style());
        architectService.generate(sender, parsed.query(), parsed.scale(), parsed.style());
    }

    private void preview(CommandSender sender, String[] args) {
        if (args.length != 1) {
            plugin.tell(sender, "&cBrug: &f/architect preview <id>");
            return;
        }
        BlueprintJob job = architectService.find(args[0]).orElse(null);
        if (job == null) {
            plugin.tell(sender, "&cUkendt blueprint-id.");
            return;
        }
        plugin.tell(sender, "&b" + job.id() + " &7- &f" + job.query());
        plugin.tell(sender, "&7Status: &f" + job.status() + " &8| &7Style: &f" + job.style()
                + " &8| &7Scale: &f" + job.scale());
        plugin.tell(sender, "&7Size: &f" + job.width() + "x" + job.height() + "x" + job.depth());
        plugin.tell(sender, "&7Preview forslag: &f/el preview look " + job.width() + " " + job.height()
                + " " + job.depth() + " 0 90");
        plugin.tell(sender, "&7Schem: &f" + job.schematicPath().getFileName());
    }

    private void paste(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.tell(sender, "&cPaste skal kores af en spiller in-game.");
            return;
        }
        if (args.length != 1) {
            plugin.tell(sender, "&cBrug: &f/architect paste <id>");
            return;
        }
        BlueprintJob job = architectService.find(args[0]).orElse(null);
        if (job == null) {
            plugin.tell(sender, "&cUkendt blueprint-id.");
            return;
        }
        plugin.tell(sender, "&7Loader schematic async. Paste sker ved din nuvaerende placering...");
        architectService.pasteAsync(player, job);
    }

    private void list(CommandSender sender) {
        List<BlueprintJob> jobs = architectService.jobs();
        if (jobs.isEmpty()) {
            plugin.tell(sender, "&7Ingen blueprints genereret endnu.");
            return;
        }
        plugin.tell(sender, "&bSeneste blueprints:");
        for (BlueprintJob job : jobs.stream().limit(8).toList()) {
            plugin.tell(sender, "&8- &f" + job.id() + " &7" + job.status() + " &8"
                    + job.width() + "x" + job.height() + "x" + job.depth() + " &7" + job.query());
        }
    }

    private void reload(CommandSender sender) {
        plugin.reloadModule();
        plugin.tell(sender, "&aArchitectModule config genindlaest.");
    }

    private void sendHelp(CommandSender sender) {
        plugin.tell(sender, "&bArchitectModule &7admin commands:");
        plugin.tell(sender, "&f/architect search <building>");
        plugin.tell(sender, "&f/architect generate <building> [scale] [style]");
        plugin.tell(sender, "&f/architect preview <id>");
        plugin.tell(sender, "&f/architect paste <id>");
        plugin.tell(sender, "&f/architect list");
    }

    private ParsedGeneration parseGeneration(String[] args) {
        if (args.length == 0) {
            return new ParsedGeneration("", architectService.defaultScale(), architectService.defaultStyle());
        }
        List<String> parts = new ArrayList<>(Arrays.asList(args));
        String style = architectService.defaultStyle();
        int scale = architectService.defaultScale();

        if (!parts.isEmpty() && architectService.styles().contains(parts.get(parts.size() - 1).toLowerCase(Locale.ROOT))) {
            style = parts.remove(parts.size() - 1).toLowerCase(Locale.ROOT);
        }
        if (!parts.isEmpty() && isInteger(parts.get(parts.size() - 1))) {
            scale = architectService.clampScale(Integer.parseInt(parts.remove(parts.size() - 1)));
        }

        return new ParsedGeneration(String.join(" ", parts), scale, style);
    }

    private static String join(String[] args) {
        return String.join(" ", args).trim();
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static List<String> startsWith(List<String> values, String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        return values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(lower)).toList();
    }

    private record ParsedGeneration(String query, int scale, String style) {
    }
}
