package dk.earthliving.architect.blueprint;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import dk.earthliving.architect.ArchitectModulePlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SchematicPreviewService implements Listener {
    private final ArchitectModulePlugin plugin;
    private final ArchitectService architectService;
    private final Map<UUID, PreviewSession> sessions = new HashMap<>();
    private int range;
    private int seconds;
    private int updateTicks;
    private int maxVisualBlocks;

    public SchematicPreviewService(ArchitectModulePlugin plugin, ArchitectService architectService) {
        this.plugin = plugin;
        this.architectService = architectService;
        reload();
    }

    public void reload() {
        range = Math.max(16, plugin.getConfig().getInt("preview.range", 120));
        seconds = Math.max(10, plugin.getConfig().getInt("preview.seconds", 90));
        updateTicks = Math.max(2, plugin.getConfig().getInt("preview.update-ticks", 8));
        maxVisualBlocks = Math.max(250, plugin.getConfig().getInt("preview.max-visual-blocks", 3500));
    }

    public void start(Player player, BlueprintJob job) {
        cancel(player, false);
        plugin.tell(player, "&7Loader schematic async til visuel preview...");
        CompletableFuture.supplyAsync(() -> {
            try {
                Clipboard clipboard = architectService.loadClipboard(job);
                return LoadedPreview.from(clipboard, maxVisualBlocks);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }).whenComplete((loadedPreview, throwable) -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (throwable != null) {
                plugin.tell(player, "&cPreview failed: &f" + throwable.getCause().getMessage());
                return;
            }
            PreviewSession session = new PreviewSession(player.getUniqueId(), job, loadedPreview);
            sessions.put(player.getUniqueId(), session);
            session.task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> render(player, session),
                    0L, updateTicks);
            session.timeoutTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> cancel(player, true),
                    seconds * 20L);
            plugin.tell(player, "&aVisuel preview startet. &fVenstreklik &afor at placere. &f/architect cancel &7annullerer.");
        }));
    }

    public void cancel(Player player, boolean notify) {
        PreviewSession session = sessions.remove(player.getUniqueId());
        if (session == null) {
            if (notify) {
                plugin.tell(player, "&7Ingen aktiv architect-preview.");
            }
            return;
        }
        session.cancelTasks();
        restore(player, session);
        if (notify) {
            plugin.tell(player, "&7Architect-preview annulleret.");
        }
    }

    public void shutdown() {
        for (UUID uuid : List.copyOf(sessions.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            PreviewSession session = sessions.remove(uuid);
            if (session != null) {
                session.cancelTasks();
                if (player != null) {
                    restore(player, session);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        PreviewSession session = sessions.get(event.getPlayer().getUniqueId());
        if (session == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        Location origin = targetLocation(player);
        if (origin == null) {
            plugin.tell(player, "&cKig paa en blok for at placere previewet.");
            return;
        }
        sessions.remove(player.getUniqueId());
        session.cancelTasks();
        restore(player, session);
        try {
            architectService.pasteLoaded(player, session.job(), session.loadedPreview().clipboard(), origin, session.rotation());
        } catch (Exception exception) {
            plugin.tell(player, "&cPaste failed: &f" + exception.getMessage());
            plugin.getLogger().warning("Preview paste failed for " + session.job().id() + ": " + exception.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHotbarScroll(PlayerItemHeldEvent event) {
        PreviewSession session = sessions.get(event.getPlayer().getUniqueId());
        if (session == null) {
            return;
        }
        event.setCancelled(true);
        int previous = event.getPreviousSlot();
        int next = event.getNewSlot();
        int delta = (next - previous + 9) % 9;
        int direction = delta <= 4 ? 1 : -1;
        session.rotate(direction);
        session.lastOrigin(null);
        restore(event.getPlayer(), session);
        render(event.getPlayer(), session);
        plugin.tell(event.getPlayer(), "&7Rotation: &f" + (session.rotation() * 90) + " grader&7. Venstreklik placerer.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancel(event.getPlayer(), false);
    }

    private void render(Player player, PreviewSession session) {
        Location origin = targetLocation(player);
        if (origin == null) {
            restore(player, session);
            return;
        }
        if (session.lastOrigin() != null && sameBlock(session.lastOrigin(), origin)) {
            return;
        }
        restore(player, session);
        session.lastOrigin(origin.clone());
        World world = origin.getWorld();
        Set<Location> sentLocations = new LinkedHashSet<>();
        for (PreviewBlock block : session.loadedPreview().rotatedBlocks(session.rotation())) {
            Location location = new Location(world,
                    origin.getBlockX() + block.x(),
                    origin.getBlockY() + block.y(),
                    origin.getBlockZ() + block.z());
            player.sendBlockChange(location, block.blockData());
            sentLocations.add(location);
        }
        session.sentLocations(sentLocations);
    }

    private void restore(Player player, PreviewSession session) {
        for (Location location : session.sentLocations()) {
            Block block = location.getBlock();
            player.sendBlockChange(location, block.getBlockData());
        }
        session.sentLocations(Set.of());
    }

    private Location targetLocation(Player player) {
        Block block = player.getTargetBlockExact(range);
        if (block == null) {
            return null;
        }
        return block.getLocation().add(0, 1, 0);
    }

    private boolean sameBlock(Location first, Location second) {
        return first.getWorld() == second.getWorld()
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
    }

    private record PreviewBlock(int x, int y, int z, BlockData blockData) {
    }

    private record LoadedPreview(Clipboard clipboard, List<PreviewBlock> blocks, int width, int depth) {
        private static LoadedPreview from(Clipboard clipboard, int maxBlocks) {
            BlockVector3 min = clipboard.getMinimumPoint();
            BlockVector3 max = clipboard.getMaximumPoint();
            List<PreviewBlock> all = new ArrayList<>();
            for (int x = min.x(); x <= max.x(); x++) {
                for (int y = min.y(); y <= max.y(); y++) {
                    for (int z = min.z(); z <= max.z(); z++) {
                        BlockVector3 position = BlockVector3.at(x, y, z);
                        BlockState state = clipboard.getBlock(position);
                        if (state.getBlockType() == BlockTypes.AIR) {
                            continue;
                        }
                        all.add(new PreviewBlock(x - min.x(), y - min.y(), z - min.z(), BukkitAdapter.adapt(state)));
                    }
                }
            }
            if (all.size() <= maxBlocks) {
                return new LoadedPreview(clipboard, all, max.x() - min.x() + 1, max.z() - min.z() + 1);
            }
            int step = Math.max(1, (int) Math.ceil(all.size() / (double) maxBlocks));
            List<PreviewBlock> sampled = new ArrayList<>();
            for (int i = 0; i < all.size(); i += step) {
                sampled.add(all.get(i));
            }
            return new LoadedPreview(clipboard, sampled, max.x() - min.x() + 1, max.z() - min.z() + 1);
        }

        private List<PreviewBlock> rotatedBlocks(int rotation) {
            int turns = Math.floorMod(rotation, 4);
            if (turns == 0) {
                return blocks;
            }
            List<PreviewBlock> rotated = new ArrayList<>(blocks.size());
            for (PreviewBlock block : blocks) {
                RotatedPoint point = rotate(block.x(), block.z(), turns);
                rotated.add(new PreviewBlock(point.x(), block.y(), point.z(), block.blockData()));
            }
            return rotated;
        }

        private RotatedPoint rotate(int x, int z, int turns) {
            return switch (turns) {
                case 1 -> new RotatedPoint(z, width - 1 - x);
                case 2 -> new RotatedPoint(width - 1 - x, depth - 1 - z);
                case 3 -> new RotatedPoint(depth - 1 - z, x);
                default -> new RotatedPoint(x, z);
            };
        }
    }

    private record RotatedPoint(int x, int z) {
    }

    private static final class PreviewSession {
        private final UUID playerId;
        private final BlueprintJob job;
        private final LoadedPreview loadedPreview;
        private BukkitTask task;
        private BukkitTask timeoutTask;
        private Location lastOrigin;
        private Set<Location> sentLocations = Set.of();
        private int rotation;

        private PreviewSession(UUID playerId, BlueprintJob job, LoadedPreview loadedPreview) {
            this.playerId = playerId;
            this.job = job;
            this.loadedPreview = loadedPreview;
        }

        private BlueprintJob job() {
            return job;
        }

        private LoadedPreview loadedPreview() {
            return loadedPreview;
        }

        private Location lastOrigin() {
            return lastOrigin;
        }

        private void lastOrigin(Location lastOrigin) {
            this.lastOrigin = lastOrigin;
        }

        private Set<Location> sentLocations() {
            return sentLocations;
        }

        private void sentLocations(Set<Location> sentLocations) {
            this.sentLocations = sentLocations;
        }

        private int rotation() {
            return rotation;
        }

        private void rotate(int direction) {
            rotation = Math.floorMod(rotation + direction, 4);
        }

        private void cancelTasks() {
            if (task != null) {
                task.cancel();
            }
            if (timeoutTask != null) {
                timeoutTask.cancel();
            }
        }
    }
}
