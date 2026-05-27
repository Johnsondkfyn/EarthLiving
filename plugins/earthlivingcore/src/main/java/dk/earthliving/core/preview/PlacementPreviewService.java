package dk.earthliving.core.preview;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlacementPreviewService {
    private static final int MAX_SIZE = 256;
    private static final int DEFAULT_SECONDS = 45;
    private static final double STEP = 1.0D;

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final Map<UUID, PreviewSession> previews = new HashMap<>();
    private BukkitTask task;

    public PlacementPreviewService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    public boolean show(Player player, int width, int height, int depth, int yOffset, int seconds) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            notifications.send(player, "&cPreview size must be positive.");
            return false;
        }
        if (width > MAX_SIZE || height > MAX_SIZE || depth > MAX_SIZE) {
            notifications.send(player, "&cPreview max size is &f" + MAX_SIZE + " &cblocks per axis.");
            return false;
        }

        int safeSeconds = Math.max(5, Math.min(180, seconds));
        Location origin = player.getLocation().getBlock().getLocation().add(0.5D, yOffset, 0.5D);
        PreviewSession session = new PreviewSession(origin, width, height, depth, System.currentTimeMillis() + safeSeconds * 1000L);
        previews.put(player.getUniqueId(), session);
        ensureTask();
        draw(player, session);

        notifications.send(player, "&aPlacement preview shown for &f" + safeSeconds + "s&a.");
        notifications.send(player, "&7Origin: &f" + origin.getBlockX() + " " + origin.getBlockY() + " " + origin.getBlockZ()
                + " &7Size: &f" + width + "x" + height + "x" + depth);
        notifications.send(player, "&7Move and run the command again to test a new height/position.");
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
                    draw(player, entry.getValue());
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

        Particle.DustOptions edge = new Particle.DustOptions(Color.fromRGB(85, 199, 216), 1.2F);
        Particle.DustOptions floor = new Particle.DustOptions(Color.fromRGB(143, 214, 111), 1.0F);
        Particle.DustOptions originColor = new Particle.DustOptions(Color.fromRGB(255, 199, 89), 1.5F);

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

        spawn(player, minX, minY, minZ, originColor);
        spawn(player, minX, minY + 1, minZ, originColor);
    }

    private void drawBoxLine(Player player, double x1, double y1, double z1, double x2, double y2, double z2, Particle.DustOptions color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) / STEP));
        for (int index = 0; index <= steps; index++) {
            double progress = (double) index / steps;
            spawn(player, x1 + dx * progress, y1 + dy * progress, z1 + dz * progress, color);
        }
    }

    private void spawn(Player player, double x, double y, double z, Particle.DustOptions color) {
        player.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0, color);
    }

    private record PreviewSession(Location origin, int width, int height, int depth, long expiresAt) {
    }
}
