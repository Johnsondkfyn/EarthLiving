package dk.johna.earthlogger;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class EarthLoggerPlugin extends JavaPlugin implements Listener {
    private final Set<UUID> inspectingPlayers = ConcurrentHashMap.newKeySet();
    private ExecutorService databaseExecutor;
    private Database database;
    private int lookupLimit;
    private boolean logCreativeActions;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        lookupLimit = Math.max(1, getConfig().getInt("lookup-limit", 10));
        logCreativeActions = getConfig().getBoolean("log-creative-actions", true);

        databaseExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "EarthLogger-Database");
            thread.setDaemon(true);
            return thread;
        });

        try {
            File databaseFile = new File(getDataFolder(), getConfig().getString("database-file", "earthlogger.db"));
            getDataFolder().mkdirs();
            database = new Database(databaseFile);
        } catch (SQLException exception) {
            getLogger().severe("Could not open EarthLogger database: " + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("EarthLogger enabled.");
    }

    @Override
    public void onDisable() {
        inspectingPlayers.clear();
        if (databaseExecutor != null) {
            databaseExecutor.shutdown();
        }
        if (database != null) {
            try {
                database.close();
            } catch (SQLException exception) {
                getLogger().warning("Could not close EarthLogger database cleanly: " + exception.getMessage());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("earthlogger.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use EarthLogger.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("inspect")) {
            return handleInspect(sender);
        }

        if (args[0].equalsIgnoreCase("status")) {
            return handleStatus(sender);
        }

        if (args[0].equalsIgnoreCase("lookup")) {
            return handleLookup(sender, args);
        }

        sendHelp(sender);
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (shouldSkip(event.getPlayer())) {
            return;
        }
        log("PLACE", event.getPlayer(), event.getBlockPlaced().getLocation(), event.getBlockPlaced().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (shouldSkip(event.getPlayer())) {
            return;
        }
        log("BREAK", event.getPlayer(), event.getBlock().getLocation(), event.getBlock().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInspect(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!inspectingPlayers.contains(player.getUniqueId())) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        event.setCancelled(true);
        lookupAndSend(player, block.getLocation());
    }

    private boolean handleInspect(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use inspect mode.");
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (inspectingPlayers.remove(uuid)) {
            player.sendMessage(ChatColor.YELLOW + "EarthLogger inspect mode disabled.");
        } else {
            inspectingPlayers.add(uuid);
            player.sendMessage(ChatColor.GREEN + "EarthLogger inspect mode enabled. Click blocks to inspect history.");
        }
        return true;
    }

    private boolean handleStatus(CommandSender sender) {
        databaseExecutor.execute(() -> {
            try {
                long count = database.countLogs();
                getServer().getScheduler().runTask(this, () ->
                        sender.sendMessage(ChatColor.GREEN + "EarthLogger has " + count + " block log entries."));
            } catch (SQLException exception) {
                sendDatabaseError(sender, exception);
            }
        });
        return true;
    }

    private boolean handleLookup(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use /elog lookup in v1.");
            return true;
        }

        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /elog lookup <x> <y> <z>");
            return true;
        }

        try {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            lookupAndSend(player, new Location(player.getWorld(), x, y, z));
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + "Coordinates must be whole numbers.");
        }
        return true;
    }

    private void log(String action, Player player, Location location, Material blockType) {
        databaseExecutor.execute(() -> {
            try {
                database.log(action, player, location, blockType);
            } catch (SQLException exception) {
                getLogger().warning("Could not log block action: " + exception.getMessage());
            }
        });
    }

    private void lookupAndSend(Player player, Location location) {
        databaseExecutor.execute(() -> {
            try {
                List<BlockLogEntry> entries = database.lookup(location, lookupLimit);
                getServer().getScheduler().runTask(this, () -> sendLookup(player, location, entries));
            } catch (SQLException exception) {
                sendDatabaseError(player, exception);
            }
        });
    }

    private void sendLookup(Player player, Location location, List<BlockLogEntry> entries) {
        player.sendMessage(ChatColor.GOLD + "EarthLogger history for "
                + location.getWorld().getName() + " "
                + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + ":");
        if (entries.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "No logged block changes found.");
            return;
        }

        for (BlockLogEntry entry : entries) {
            player.sendMessage(ChatColor.GRAY + entry.format());
        }
    }

    private void sendDatabaseError(CommandSender sender, SQLException exception) {
        getLogger().warning("Database query failed: " + exception.getMessage());
        getServer().getScheduler().runTask(this, () ->
                sender.sendMessage(ChatColor.RED + "EarthLogger database query failed. Check console."));
    }

    private boolean shouldSkip(Player player) {
        return !logCreativeActions && player.getGameMode() == GameMode.CREATIVE;
    }

    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "EarthLogger commands:");
        sender.sendMessage(ChatColor.YELLOW + "/elog inspect" + ChatColor.GRAY + " - Toggle block inspection mode.");
        sender.sendMessage(ChatColor.YELLOW + "/elog lookup <x> <y> <z>" + ChatColor.GRAY + " - Lookup a block in your world.");
        sender.sendMessage(ChatColor.YELLOW + "/elog status" + ChatColor.GRAY + " - Show total logged block changes.");
    }
}
