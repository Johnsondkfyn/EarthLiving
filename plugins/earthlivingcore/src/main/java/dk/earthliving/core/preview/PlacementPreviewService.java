package dk.earthliving.core.preview;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlacementPreviewService {
    private static final int MAX_SIZE = 256;
    private static final int MAX_LOOK_DISTANCE = 120;
    private static final double EDGE_STEP = 0.45D;
    private static final double GRID_STEP = 2.0D;

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final Map<UUID, PreviewSession> previews = new HashMap<>();
    private BukkitTask task;

    public PlacementPreviewService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    public boolean show(Player player, int width, int height, int depth, int yOffset, int seconds) {
        return showAt(player, player.getLocation().getBlock().getLocation().add(0.5D, yOffset, 0.5D),
                width, height, depth, seconds, false, yOffset, 0);
    }

    public boolean showLook(Player player, int width, int height, int depth, int yOffset, int seconds, int distance) {
        int safeDistance = Math.max(5, Math.min(MAX_LOOK_DISTANCE, distance));
        Location origin = findLookOrigin(player, yOffset, safeDistance);
        if (origin == null) {
            notifications.send(player, "&cLook at a solid block within &f" + MAX_LOOK_DISTANCE + " &cblocks.");
            return false;
        }
        return showAt(player, origin, width, height, depth, seconds, true, yOffset, safeDistance);
    }

    private boolean showAt(Player player, Location origin, int width, int height, int depth, int seconds, boolean followLook, int yOffset, int distance) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            notifications.send(player, "&cPreview size must be positive.");
            return false;
        }
        if (width > MAX_SIZE || height > MAX_SIZE || depth > MAX_SIZE) {
            notifications.send(player, "&cPreview max size is &f" + MAX_SIZE + " &cblocks per axis.");
            return false;
        }

        int safeSeconds = Math.max(5, Math.min(180, seconds));
        PreviewSession session = new PreviewSession(origin, width, height, depth,
                System.currentTimeMillis() + safeSeconds * 1000L, followLook, yOffset, distance);
        previews.put(player.getUniqueId(), session);
        ensureTask();
        draw(player, session);

        notifications.send(player, "&aPlacement preview shown for &f" + safeSeconds + "s&a.");
        if (followLook) {
            notifications.send(player, "&7Free-roam mode: preview follows the block you look at.");
        }
        notifications.send(player, "&7Origin: &f" + origin.getBlockX() + " " + origin.getBlockY() + " " + origin.getBlockZ()
                + " &7Size: &f" + width + "x" + height + "x" + depth);
        notifications.send(player, "&7Use /el preview clear to stop it.");
        return true;
    }

    public boolean clear(Player player) {
        boolean removed = previews.remove(player.getUniqueId()) != null;
        if (removed) {
            notifications.send(player, "&aPlacement preview cleared.");
        } else {
            notifications.send(player, "&7No active placement preview.");
        }
        return removed;
    }

    public void stop() {
        previews.clear();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void ensureTask() {
        if (task != null) {
            return;
        }
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            previews.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= now);
            for (Map.Entry<UUID, PreviewSession> entry : previews.entrySet()) {
                Player player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    PreviewSession session = entry.getValue();
                    if (session.followLook()) {
                        Location origin = findLookOrigin(player, session.yOffset(), session.distance());
                        if (origin != null) {
                            session = session.withOrigin(origin);
                            entry.setValue(session);
                        }
                    }
                    draw(player, session);
                }
            }
            if (previews.isEmpty() && task != null) {
                task.cancel();
                task = null;
            }
        }, 0L, 10L);
    }

    private void draw(Player player, PreviewSession session) {
        Location origin = session.origin();
        double minX = origin.getX();
        double minY = origin.getY();
        double minZ = origin.getZ();
        double maxX = minX + session.width();
        double maxY = minY + session.height();
        double maxZ = minZ + session.depth();

        Particle.DustOptions edge = new Particle.DustOptions(Color.fromRGB(48, 230, 255), 1.9F);
        Particle.DustOptions floor = new Particle.DustOptions(Color.fromRGB(143, 255, 96), 1.45F);
        Particle.DustOptions mid = new Particle.DustOptions(Color.fromRGB(255, 221, 96), 1.35F);
        Particle.DustOptions corner = new Particle.DustOptions(Color.fromRGB(255, 96, 64), 2.2F);
        Particle.DustOptions originColor = new Particle.DustOptions(Color.fromRGB(255, 199, 89), 2.4F);

        drawFloorGrid(player, minX, minY, minZ, maxX, maxZ, floor);
        drawBoxLine(player, minX, minY, minZ, maxX, minY, minZ, floor);
        drawBoxLine(player, minX, minY, maxZ, maxX, minY, maxZ, floor);
        drawBoxLine(player, minX, minY, minZ, minX, minY, maxZ, floor);
        drawBoxLine(player, maxX, minY, minZ, maxX, minY, maxZ, floor);

        drawBoxLine(player, minX, maxY, minZ, maxX, maxY, minZ, edge);
        drawBoxLine(player, minX, maxY, maxZ, maxX, maxY, maxZ, edge);
        drawBoxLine(player, minX, maxY, minZ, minX, maxY, maxZ, edge);
        drawBoxLine(player, maxX, maxY, minZ, maxX, maxY, maxZ, edge);

        drawBoxLine(player, minX, minY, minZ, minX, maxY, minZ, edge);
        drawBoxLine(player, maxX, minY, minZ, maxX, maxY, minZ, edge);
        drawBoxLine(player, minX, minY, maxZ, minX, maxY, maxZ, edge);
        drawBoxLine(player, maxX, minY, maxZ, maxX, maxY, maxZ, edge);

        double midY = minY + (session.height() / 2.0D);
        drawBoxLine(player, minX, midY, minZ, maxX, midY, minZ, mid);
        drawBoxLine(player, minX, midY, maxZ, maxX, midY, maxZ, mid);
        drawBoxLine(player, minX, midY, minZ, minX, midY, maxZ, mid);
        drawBoxLine(player, maxX, midY, minZ, maxX, midY, maxZ, mid);

        drawCorner(player, minX, minY, minZ, corner);
        drawCorner(player, maxX, minY, minZ, corner);
        drawCorner(player, minX, minY, maxZ, corner);
        drawCorner(player, maxX, minY, maxZ, corner);
        drawCorner(player, minX, maxY, minZ, corner);
        drawCorner(player, maxX, maxY, minZ, corner);
        drawCorner(player, minX, maxY, maxZ, corner);
        drawCorner(player, maxX, maxY, maxZ, corner);

        drawOriginMarker(player, minX, minY, minZ, originColor);
    }

    private void drawBoxLine(Player player, double x1, double y1, double z1, double x2, double y2, double z2, Particle.DustOptions color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) / EDGE_STEP));
        for (int index = 0; index <= steps; index++) {
            double progress = (double) index / steps;
            spawn(player, x1 + dx * progress, y1 + dy * progress, z1 + dz * progress, color);
        }
    }

    private void drawFloorGrid(Player player, double minX, double minY, double minZ, double maxX, double maxZ, Particle.DustOptions color) {
        for (double x = minX; x <= maxX + 0.001D; x += GRID_STEP) {
            drawBoxLine(player, x, minY, minZ, x, minY, maxZ, color);
        }
        for (double z = minZ; z <= maxZ + 0.001D; z += GRID_STEP) {
            drawBoxLine(player, minX, minY, z, maxX, minY, z, color);
        }
    }

    private void drawCorner(Player player, double x, double y, double z, Particle.DustOptions color) {
        spawn(player, x, y, z, color);
        spawn(player, x, y + 0.25D, z, color);
        spawn(player, x, y - 0.25D, z, color);
        spawn(player, x + 0.25D, y, z, color);
        spawn(player, x - 0.25D, y, z, color);
        spawn(player, x, y, z + 0.25D, color);
        spawn(player, x, y, z - 0.25D, color);
    }

    private void drawOriginMarker(Player player, double x, double y, double z, Particle.DustOptions color) {
        for (double offset = 0; offset <= 2.5D; offset += 0.25D) {
            spawn(player, x, y + offset, z, color);
        }
        drawBoxLine(player, x - 1.0D, y + 0.1D, z, x + 1.0D, y + 0.1D, z, color);
        drawBoxLine(player, x, y + 0.1D, z - 1.0D, x, y + 0.1D, z + 1.0D, color);
    }

    private void spawn(Player player, double x, double y, double z, Particle.DustOptions color) {
        player.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0, color);
    }

    private Location findLookOrigin(Player player, int yOffset, int distance) {
        RayTraceResult result = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);
        if (result == null || result.getHitBlock() == null) {
            return null;
        }
        Block block = result.getHitBlock();
        return block.getLocation().add(0.5D, 1.0D + yOffset, 0.5D);
    }

    private record PreviewSession(Location origin, int width, int height, int depth, long expiresAt,
                                  boolean followLook, int yOffset, int distance) {
        private PreviewSession withOrigin(Location newOrigin) {
            return new PreviewSession(newOrigin, width, height, depth, expiresAt, followLook, yOffset, distance);
        }
    }
}
