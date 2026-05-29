package dk.earthliving.core.build;

import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BorderControlBuildGenerator {
    private static final int MIN = -10;
    private static final int MAX = 10;

    private final JavaPlugin plugin;
    private final NotificationService notifications;
    private final Map<UUID, PreviewSession> previews = new HashMap<>();
    private final Map<UUID, List<SavedBlock>> undoHistory = new HashMap<>();
    private List<GeneratedBlock> capture;
    private BukkitTask previewTask;

    public BorderControlBuildGenerator(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    public void generate(Player player, boolean confirmed) {
        Location origin = player.getLocation().toBlockLocation();
        generateAt(player, origin, confirmed);
    }

    public void generateAt(Player player, Location origin, boolean confirmed) {
        origin = origin.toBlockLocation();
        if (!confirmed && hasBlockingBuildSpace(origin)) {
            notifications.send(player, "&eThis will place a 21x21 Border Control building at your feet.");
            notifications.send(player, "&eRun &f/elbuild bordercontrol confirm &eto confirm and overwrite the compact build area.");
            return;
        }

        saveUndo(player, origin);
        buildFoundation(origin);
        buildRoads(origin);
        buildWalls(origin);
        buildGlass(origin);
        buildRooms(origin);
        buildRoof(origin);
        buildGates(origin);
        buildLighting(origin);
        buildInterior(origin);
        buildPlanters(origin);
        buildSigns(origin);

        notifications.send(player, "&aEarth Living Border Control generated.");
        notifications.send(player, "&7Center/neutral hall: &f" + origin.getBlockX() + " " + origin.getBlockY() + " " + origin.getBlockZ());
        notifications.send(player, "&7Country A side: &fnorth/negative Z&7. Country B side: &fsouth/positive Z&7.");
    }

    public void startPreview(Player player) {
        cancelPreview(player, false);
        List<GeneratedBlock> blocks = generatedBlocks();
        PreviewSession session = new PreviewSession(blocks, Map.of(), null, System.currentTimeMillis() + 90_000L);
        previews.put(player.getUniqueId(), session);
        ensurePreviewTask();
        renderPreview(player, session);
        notifications.send(player, "&aBorder Control ghost preview started.");
        notifications.send(player, "&7Look at the placement block. Left-click places it. Use &f/elbuild bordercontrol cancel &7to stop.");
    }

    public boolean placePreview(Player player) {
        PreviewSession session = previews.remove(player.getUniqueId());
        if (session == null) {
            return false;
        }
        restorePreview(player, session);
        Location origin = targetOrigin(player);
        if (origin == null) {
            notifications.send(player, "&cLook at a solid block before placing the build.");
            return true;
        }
        generateAt(player, origin, true);
        return true;
    }

    public boolean cancelPreview(Player player, boolean notify) {
        PreviewSession session = previews.remove(player.getUniqueId());
        if (session == null) {
            if (notify) {
                notifications.send(player, "&7No active Border Control preview.");
            }
            return false;
        }
        restorePreview(player, session);
        if (notify) {
            notifications.send(player, "&7Border Control preview cancelled.");
        }
        return true;
    }

    public boolean undo(Player player) {
        List<SavedBlock> savedBlocks = undoHistory.remove(player.getUniqueId());
        if (savedBlocks == null || savedBlocks.isEmpty()) {
            notifications.send(player, "&7No Border Control build to undo.");
            return false;
        }
        for (SavedBlock savedBlock : savedBlocks) {
            savedBlock.location().getBlock().setBlockData(savedBlock.blockData(), false);
        }
        notifications.send(player, "&aLast Border Control build restored.");
        return true;
    }

    public void stop() {
        for (UUID uuid : List.copyOf(previews.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            PreviewSession session = previews.remove(uuid);
            if (player != null && session != null) {
                restorePreview(player, session);
            }
        }
        if (previewTask != null) {
            previewTask.cancel();
            previewTask = null;
        }
    }

    private void saveUndo(Player player, Location origin) {
        List<SavedBlock> savedBlocks = new ArrayList<>();
        World world = origin.getWorld();
        for (int x = MIN - 4; x <= MAX + 4; x++) {
            for (int y = -1; y <= 8; y++) {
                for (int z = MIN - 4; z <= MAX + 4; z++) {
                    Location location = new Location(world, origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);
                    savedBlocks.add(new SavedBlock(location, location.getBlock().getBlockData()));
                }
            }
        }
        undoHistory.put(player.getUniqueId(), savedBlocks);
    }

    private List<GeneratedBlock> generatedBlocks() {
        capture = new ArrayList<>();
        try {
            buildFoundation(null);
            buildRoads(null);
            buildWalls(null);
            buildGlass(null);
            buildRooms(null);
            buildRoof(null);
            buildGates(null);
            buildLighting(null);
            buildInterior(null);
            buildPlanters(null);
            buildSigns(null);
            return List.copyOf(capture);
        } finally {
            capture = null;
        }
    }

    private void ensurePreviewTask() {
        if (previewTask != null) {
            return;
        }
        previewTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (UUID uuid : List.copyOf(previews.keySet())) {
                Player player = plugin.getServer().getPlayer(uuid);
                PreviewSession session = previews.get(uuid);
                if (player == null || session == null || session.expiresAt() <= now) {
                    if (player != null && session != null) {
                        restorePreview(player, session);
                    }
                    previews.remove(uuid);
                    continue;
                }
                renderPreview(player, session);
            }
            if (previews.isEmpty() && previewTask != null) {
                previewTask.cancel();
                previewTask = null;
            }
        }, 0L, 8L);
    }

    private void renderPreview(Player player, PreviewSession session) {
        Location origin = targetOrigin(player);
        if (origin == null) {
            restorePreview(player, session);
            return;
        }
        if (session.lastOrigin() != null && sameBlock(origin, session.lastOrigin())) {
            return;
        }
        restorePreview(player, session);
        World world = origin.getWorld();
        Map<Location, BlockData> sent = new HashMap<>();
        for (GeneratedBlock block : session.blocks()) {
            Location location = new Location(world, origin.getBlockX() + block.x(), origin.getBlockY() + block.y(), origin.getBlockZ() + block.z());
            player.sendBlockChange(location, block.material().createBlockData());
            sent.put(location, block.material().createBlockData());
        }
        session.sentBlocks(sent);
        session.lastOrigin(origin.clone());
    }

    private void restorePreview(Player player, PreviewSession session) {
        for (Location location : session.sentBlocks().keySet()) {
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }
        session.sentBlocks(Map.of());
    }

    private Location targetOrigin(Player player) {
        Block block = player.getTargetBlockExact(90);
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

    private boolean hasBlockingBuildSpace(Location origin) {
        World world = origin.getWorld();
        int baseY = origin.getBlockY();
        int blocking = 0;
        for (int x = MIN; x <= MAX; x++) {
            for (int y = 1; y <= 8; y++) {
                for (int z = MIN; z <= MAX; z++) {
                    Material type = world.getBlockAt(origin.getBlockX() + x, baseY + y, origin.getBlockZ() + z).getType();
                    if (!type.isAir() && type != Material.WATER && type != Material.LAVA) {
                        blocking++;
                        if (blocking > 25) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Coordinate layout: player origin is the center of the neutral hall.
    // Z- is Country A, Z+ is Country B, and the border line runs through Z=0.
    private void buildFoundation(Location origin) {
        fill(origin, MIN, -1, MIN, MAX, -1, MAX, Material.STONE_BRICKS);
        fill(origin, -9, 0, -9, 9, 0, 9, Material.POLISHED_ANDESITE);
        fill(origin, -3, 0, MIN, 3, 0, MAX, Material.LIGHT_GRAY_CONCRETE);
        fill(origin, -10, 0, -1, 10, 0, 1, Material.GRAY_CONCRETE);
        outline(origin, MIN, 0, MIN, MAX, 0, MAX, Material.GRAY_CONCRETE);
    }

    private void buildRoads(Location origin) {
        fill(origin, -2, 0, MIN - 4, 2, 0, -8, Material.GRAY_CONCRETE);
        fill(origin, -2, 0, 8, 2, 0, MAX + 4, Material.GRAY_CONCRETE);
        line(origin, 0, 1, MIN - 4, 0, 1, -8, Material.WHITE_CONCRETE);
        line(origin, 0, 1, 8, 0, 1, MAX + 4, Material.WHITE_CONCRETE);
    }

    private void buildWalls(Location origin) {
        fill(origin, -10, 1, -10, 10, 5, -10, Material.WHITE_CONCRETE);
        fill(origin, -10, 1, 10, 10, 5, 10, Material.WHITE_CONCRETE);
        fill(origin, -10, 1, -10, -10, 5, 10, Material.WHITE_CONCRETE);
        fill(origin, 10, 1, -10, 10, 5, 10, Material.WHITE_CONCRETE);

        clear(origin, -3, 1, -10, 3, 4, -10);
        clear(origin, -3, 1, 10, 3, 4, 10);
        fill(origin, -10, 1, -2, -10, 4, 2, Material.BLACK_STAINED_GLASS);
        fill(origin, 10, 1, -2, 10, 4, 2, Material.BLACK_STAINED_GLASS);
    }

    private void buildGlass(Location origin) {
        fill(origin, -8, 2, -10, -4, 4, -10, Material.BLACK_STAINED_GLASS);
        fill(origin, 4, 2, -10, 8, 4, -10, Material.BLACK_STAINED_GLASS);
        fill(origin, -8, 2, 10, -4, 4, 10, Material.BLACK_STAINED_GLASS);
        fill(origin, 4, 2, 10, 8, 4, 10, Material.BLACK_STAINED_GLASS);
        fill(origin, -10, 2, -8, -10, 4, -4, Material.BLACK_STAINED_GLASS);
        fill(origin, 10, 2, 4, 10, 4, 8, Material.BLACK_STAINED_GLASS);
    }

    private void buildRooms(Location origin) {
        // Passport booths mirror each other near the two country entrances.
        buildBooth(origin, -6, -6, "CHECK A");
        buildBooth(origin, 6, 6, "CHECK B");

        // Neutral side rooms: visa office west, staff/admin east.
        fill(origin, -10, 1, -3, -6, 4, -3, Material.LIGHT_GRAY_CONCRETE);
        fill(origin, -10, 1, 3, -6, 4, 3, Material.LIGHT_GRAY_CONCRETE);
        fill(origin, -6, 1, -3, -6, 4, 3, Material.LIGHT_GRAY_CONCRETE);
        clear(origin, -6, 1, -1, -6, 3, 1);

        fill(origin, 6, 1, -3, 10, 4, -3, Material.LIGHT_GRAY_CONCRETE);
        fill(origin, 6, 1, 3, 10, 4, 3, Material.LIGHT_GRAY_CONCRETE);
        fill(origin, 6, 1, -3, 6, 4, 3, Material.LIGHT_GRAY_CONCRETE);
        clear(origin, 6, 1, -1, 6, 3, 1);
    }

    private void buildBooth(Location origin, int centerX, int centerZ, String label) {
        fill(origin, centerX - 2, 1, centerZ - 1, centerX + 2, 3, centerZ - 1, Material.GRAY_CONCRETE);
        fill(origin, centerX - 2, 1, centerZ + 1, centerX + 2, 3, centerZ + 1, Material.GRAY_CONCRETE);
        fill(origin, centerX - 2, 1, centerZ - 1, centerX - 2, 3, centerZ + 1, Material.GRAY_CONCRETE);
        fill(origin, centerX + 2, 1, centerZ - 1, centerX + 2, 3, centerZ + 1, Material.BLACK_STAINED_GLASS);
        fill(origin, centerX - 1, 1, centerZ, centerX + 1, 1, centerZ, Material.SPRUCE_TRAPDOOR);
        placeSign(origin, centerX, 2, centerZ - (centerZ > 0 ? 2 : -2), label, "Passport", "Control");
    }

    private void buildRoof(Location origin) {
        fill(origin, -10, 6, -10, 10, 6, 10, Material.QUARTZ_SLAB);
        fill(origin, -8, 6, -8, 8, 6, 8, Material.LIGHT_GRAY_CONCRETE);
        fill(origin, -4, 6, -4, 4, 6, 4, Material.BLACK_STAINED_GLASS);
        fill(origin, -5, 7, -5, 5, 7, 5, Material.WHITE_CONCRETE);
        fill(origin, -3, 7, -3, 3, 7, 3, Material.TINTED_GLASS);
        fill(origin, -8, 7, 7, -6, 7, 8, Material.GRAY_CONCRETE);
        fill(origin, 6, 7, -8, 8, 7, -7, Material.GRAY_CONCRETE);
        outline(origin, MIN, 6, MIN, MAX, 6, MAX, Material.BLACK_CONCRETE);
    }

    private void buildGates(Location origin) {
        buildGate(origin, -1, -9, "A");
        buildGate(origin, -1, 9, "B");
        fill(origin, -3, 1, -8, -3, 3, -6, Material.IRON_BARS);
        fill(origin, 3, 1, 6, 3, 3, 8, Material.IRON_BARS);
        set(origin, -2, 2, -8, Material.LIME_CONCRETE);
        set(origin, 2, 2, 8, Material.LIME_CONCRETE);
        set(origin, 2, 2, -8, Material.RED_CONCRETE);
        set(origin, -2, 2, 8, Material.RED_CONCRETE);
    }

    private void buildGate(Location origin, int x, int z, String country) {
        fill(origin, x - 3, 1, z, x - 3, 4, z, Material.BLACK_CONCRETE);
        fill(origin, x + 5, 1, z, x + 5, 4, z, Material.BLACK_CONCRETE);
        fill(origin, x - 3, 4, z, x + 5, 4, z, Material.BLACK_CONCRETE);
        placeSign(origin, x + 1, 3, z, "EXIT GATE " + country, "Visa check", "ahead");
    }

    private void buildLighting(Location origin) {
        for (int x : new int[]{-8, 0, 8}) {
            for (int z : new int[]{-8, 0, 8}) {
                set(origin, x, 5, z, Material.SEA_LANTERN);
            }
        }
        set(origin, -4, 2, -4, Material.LANTERN);
        set(origin, 4, 2, 4, Material.LANTERN);
    }

    private void buildInterior(Location origin) {
        fill(origin, -4, 1, -1, 4, 1, 1, Material.WHITE_CONCRETE);
        fill(origin, -4, 1, 0, 4, 1, 0, Material.LIGHT_GRAY_CONCRETE);
        set(origin, -8, 1, 0, Material.LECTERN);
        set(origin, 8, 1, 0, Material.CRAFTING_TABLE);
        placeSign(origin, -8, 2, 0, "VISA", "OFFICE", "");
        placeSign(origin, 8, 2, 0, "STAFF", "ADMIN", "");
    }

    private void buildPlanters(Location origin) {
        for (int x : new int[]{-8, 8}) {
            for (int z : new int[]{-8, 8}) {
                fill(origin, x - 1, 1, z - 1, x + 1, 1, z + 1, Material.SPRUCE_TRAPDOOR);
                set(origin, x, 2, z, Material.OAK_LEAVES);
            }
        }
    }

    private void buildSigns(Location origin) {
        placeSign(origin, 0, 2, -10, "EARTH LIVING", "BORDER CONTROL", "COUNTRY A");
        placeSign(origin, 0, 2, 10, "EARTH LIVING", "BORDER CONTROL", "COUNTRY B");
        placeSign(origin, 0, 2, 0, "NEUTRAL", "VISA HALL", "");
    }

    private void placeSign(Location origin, int x, int y, int z, String line1, String line2, String line3) {
        set(origin, x, y, z, Material.OAK_SIGN);
        if (capture != null) {
            return;
        }
        Block block = block(origin, x, y, z);
        if (block.getState() instanceof Sign sign) {
            sign.setLine(0, line1);
            sign.setLine(1, line2);
            sign.setLine(2, line3);
            sign.update();
        }
    }

    private void fill(Location origin, int x1, int y1, int z1, int x2, int y2, int z2, Material material) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    set(origin, x, y, z, material);
                }
            }
        }
    }

    private void clear(Location origin, int x1, int y1, int z1, int x2, int y2, int z2) {
        fill(origin, x1, y1, z1, x2, y2, z2, Material.AIR);
    }

    private void outline(Location origin, int x1, int y, int z1, int x2, int ignoredY, int z2, Material material) {
        line(origin, x1, y, z1, x2, y, z1, material);
        line(origin, x1, y, z2, x2, y, z2, material);
        line(origin, x1, y, z1, x1, y, z2, material);
        line(origin, x2, y, z1, x2, y, z2, material);
    }

    private void line(Location origin, int x1, int y1, int z1, int x2, int y2, int z2, Material material) {
        int steps = Math.max(Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)), Math.abs(z2 - z1));
        for (int index = 0; index <= steps; index++) {
            double progress = steps == 0 ? 0.0D : index / (double) steps;
            set(origin,
                    (int) Math.round(x1 + (x2 - x1) * progress),
                    (int) Math.round(y1 + (y2 - y1) * progress),
                    (int) Math.round(z1 + (z2 - z1) * progress),
                    material);
        }
    }

    private void set(Location origin, int x, int y, int z, Material material) {
        if (capture != null) {
            if (material != Material.AIR) {
                capture.add(new GeneratedBlock(x, y, z, material));
            }
            return;
        }
        block(origin, x, y, z).setType(material, false);
    }

    private Block block(Location origin, int x, int y, int z) {
        World world = origin.getWorld();
        return world.getBlockAt(origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);
    }

    private record GeneratedBlock(int x, int y, int z, Material material) {
    }

    private record SavedBlock(Location location, BlockData blockData) {
    }

    private static final class PreviewSession {
        private final List<GeneratedBlock> blocks;
        private Map<Location, BlockData> sentBlocks;
        private Location lastOrigin;
        private final long expiresAt;

        private PreviewSession(List<GeneratedBlock> blocks, Map<Location, BlockData> sentBlocks, Location lastOrigin, long expiresAt) {
            this.blocks = blocks;
            this.sentBlocks = sentBlocks;
            this.lastOrigin = lastOrigin;
            this.expiresAt = expiresAt;
        }

        private List<GeneratedBlock> blocks() {
            return blocks;
        }

        private Map<Location, BlockData> sentBlocks() {
            return sentBlocks;
        }

        private void sentBlocks(Map<Location, BlockData> sentBlocks) {
            this.sentBlocks = sentBlocks;
        }

        private Location lastOrigin() {
            return lastOrigin;
        }

        private void lastOrigin(Location lastOrigin) {
            this.lastOrigin = lastOrigin;
        }

        private long expiresAt() {
            return expiresAt;
        }
    }
}
