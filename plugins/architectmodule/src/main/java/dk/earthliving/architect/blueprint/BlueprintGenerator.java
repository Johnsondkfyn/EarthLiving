package dk.earthliving.architect.blueprint;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockState;
import dk.earthliving.architect.ArchitectModulePlugin;
import org.bukkit.Material;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;

public final class BlueprintGenerator {
    private final ArchitectModulePlugin plugin;

    public BlueprintGenerator(ArchitectModulePlugin plugin) {
        this.plugin = plugin;
    }

    public Result generate(GenerationSpec spec, Path schematicPath, Path metadataPath, String id) throws Exception {
        BuildingShape shape = BuildingShape.from(spec);
        BlockVector3 min = BlockVector3.ZERO;
        BlockVector3 max = BlockVector3.at(shape.width() - 1, shape.height() - 1, shape.depth() - 1);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(new CuboidRegion(min, max));
        clipboard.setOrigin(BlockVector3.ZERO);

        drawBuilding(clipboard, shape, spec.style(), spec);

        Files.createDirectories(schematicPath.getParent());
        ClipboardFormat format = ClipboardFormats.findByAlias("sponge");
        if (format == null) {
            format = ClipboardFormats.findByAlias("schem");
        }
        if (format == null) {
            throw new IllegalStateException("Sponge schematic format is not available");
        }
        try (OutputStream output = Files.newOutputStream(schematicPath);
             ClipboardWriter writer = format.getWriter(output)) {
            writer.write(clipboard);
        }

        BlueprintMetadata.write(metadataPath, new BlueprintJob(
                id,
                spec.query(),
                spec.scale(),
                spec.style().id(),
                shape.width(),
                shape.height(),
                shape.depth(),
                "ready",
                spec.realWorldData()
                        .map(data -> "Generated from " + data.source() + ": " + data.summaryLine())
                        .orElse("Generated Minecraft interpretation"),
                schematicPath,
                metadataPath,
                Instant.now()
        ));
        return new Result(shape.width(), shape.height(), shape.depth());
    }

    private void drawBuilding(BlockArrayClipboard clipboard, BuildingShape shape, BlueprintStyle style, GenerationSpec spec) {
        if (isClockTower(spec)) {
            drawClockTower(clipboard, shape, style);
            return;
        }
        if (isObelisk(spec)) {
            drawObelisk(clipboard, shape);
            return;
        }
        if (shape.kind() == BuildingKind.TOWER && isSkyscraper(spec)) {
            drawSkyscraper(clipboard, shape, style);
            return;
        }
        if (shape.kind() == BuildingKind.TOWER && isEiffelLike(spec)) {
            drawEiffelTower(clipboard, shape);
            return;
        }
        if (shape.kind() == BuildingKind.TOWER) {
            drawLatticeTower(clipboard, shape, style);
            return;
        }
        if (shape.kind() == BuildingKind.LANDMARK && isSpireLandmark(spec)) {
            drawLatticeTower(clipboard, shape, style);
            return;
        }
        drawClosedBuilding(clipboard, shape, style);
    }

    private void drawClosedBuilding(BlockArrayClipboard clipboard, BuildingShape shape, BlueprintStyle style) {
        BlockState wall = state(style.wall());
        BlockState trim = state(style.trim());
        BlockState glass = state(style.glass());
        BlockState floor = state(style.floor());
        BlockState roof = state(style.roof());
        BlockState air = state(Material.AIR);

        fill(clipboard, 0, 0, 0, shape.width() - 1, 0, shape.depth() - 1, floor);
        fill(clipboard, 1, 1, 1, shape.width() - 2, 1, shape.depth() - 2, floor);

        for (int y = 1; y < shape.height(); y++) {
            for (int x = 0; x < shape.width(); x++) {
                for (int z = 0; z < shape.depth(); z++) {
                    boolean edge = x == 0 || z == 0 || x == shape.width() - 1 || z == shape.depth() - 1;
                    if (!edge) {
                        continue;
                    }
                    boolean pillar = x % 6 == 0 || z % 6 == 0 || x == 0 || z == 0
                            || x == shape.width() - 1 || z == shape.depth() - 1;
                    boolean window = y > 2 && y < shape.height() - 2 && y % 4 != 0 && !pillar;
                    set(clipboard, x, y, z, window ? glass : (pillar ? trim : wall));
                }
            }
            if (y % 5 == 0) {
                fill(clipboard, 0, y, 0, shape.width() - 1, y, shape.depth() - 1, trim);
            }
        }

        int entranceStart = Math.max(2, shape.width() / 2 - 2);
        int entranceEnd = Math.min(shape.width() - 3, shape.width() / 2 + 2);
        for (int x = entranceStart; x <= entranceEnd; x++) {
            for (int y = 1; y <= 3; y++) {
                set(clipboard, x, y, 0, air);
            }
        }

        fill(clipboard, 0, shape.height(), 0, shape.width() - 1, shape.height(), shape.depth() - 1, roof);
        fill(clipboard, 1, shape.height() + 1, 1, shape.width() - 2, shape.height() + 1, shape.depth() - 2, trim);

        if (shape.kind() == BuildingKind.TOWER || shape.kind() == BuildingKind.LANDMARK) {
            int cx = shape.width() / 2;
            int cz = shape.depth() / 2;
            for (int y = shape.height() + 2; y < shape.height() + 8; y++) {
                set(clipboard, cx, y, cz, trim);
                set(clipboard, cx + 1, y, cz, glass);
                set(clipboard, cx - 1, y, cz, glass);
                set(clipboard, cx, y, cz + 1, glass);
                set(clipboard, cx, y, cz - 1, glass);
            }
        }
    }

    private void drawSkyscraper(BlockArrayClipboard clipboard, BuildingShape shape, BlueprintStyle style) {
        BlockState frame = state(style.trim());
        BlockState glass = state(style.glass());
        BlockState floor = state(style.floor());
        BlockState roof = state(Material.DEEPSLATE_TILES);
        BlockState light = state(Material.SEA_LANTERN);
        int maxY = shape.height() - 1;

        fill(clipboard, 0, 0, 0, shape.width() - 1, 0, shape.depth() - 1, floor);
        for (int y = 1; y <= maxY; y++) {
            for (int x = 0; x < shape.width(); x++) {
                for (int z = 0; z < shape.depth(); z++) {
                    boolean edge = x == 0 || z == 0 || x == shape.width() - 1 || z == shape.depth() - 1;
                    if (!edge) {
                        continue;
                    }
                    boolean pillar = x == 0 || z == 0 || x == shape.width() - 1 || z == shape.depth() - 1
                            || x % 5 == 0 || z % 5 == 0 || y % 7 == 0;
                    set(clipboard, x, y, z, pillar ? frame : glass);
                }
            }
            if (y % 8 == 4) {
                set(clipboard, shape.width() / 2, y, 0, light);
                set(clipboard, shape.width() / 2, y, shape.depth() - 1, light);
            }
        }
        fill(clipboard, 1, maxY, 1, shape.width() - 2, maxY, shape.depth() - 2, roof);
    }

    private void drawClockTower(BlockArrayClipboard clipboard, BuildingShape shape, BlueprintStyle style) {
        BlockState stone = state(Material.SMOOTH_STONE);
        BlockState trim = state(Material.POLISHED_ANDESITE);
        BlockState dark = state(Material.BLACK_CONCRETE);
        BlockState face = state(Material.WHITE_CONCRETE);
        BlockState roof = state(Material.DEEPSLATE_TILES);
        int cx = shape.width() / 2;
        int cz = shape.depth() / 2;
        int maxY = shape.height() - 1;
        int radius = Math.max(3, Math.min(shape.width(), shape.depth()) / 5);

        fill(clipboard, cx - radius - 1, 0, cz - radius - 1, cx + radius + 1, 0, cz + radius + 1, trim);
        for (int y = 1; y <= maxY - 5; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    boolean wall = x == cx - radius || x == cx + radius || z == cz - radius || z == cz + radius;
                    if (!wall) {
                        continue;
                    }
                    boolean corner = (x == cx - radius || x == cx + radius) && (z == cz - radius || z == cz + radius);
                    set(clipboard, x, y, z, corner || y % 6 == 0 ? trim : stone);
                }
            }
        }
        int clockY = maxY - 8;
        drawClockFace(clipboard, cx, clockY, cz - radius, face, dark);
        drawClockFace(clipboard, cx, clockY, cz + radius, face, dark);
        drawClockFaceX(clipboard, cx - radius, clockY, cz, face, dark);
        drawClockFaceX(clipboard, cx + radius, clockY, cz, face, dark);

        fill(clipboard, cx - radius - 1, maxY - 5, cz - radius - 1, cx + radius + 1, maxY - 5, cz + radius + 1, trim);
        for (int y = maxY - 4; y <= maxY; y++) {
            int r = Math.max(0, maxY - y);
            fill(clipboard, cx - r, y, cz - r, cx + r, y, cz + r, roof);
        }
    }

    private void drawClockFace(BlockArrayClipboard clipboard, int cx, int y, int z, BlockState face, BlockState dark) {
        fill(clipboard, cx - 2, y - 2, z, cx + 2, y + 2, z, face);
        set(clipboard, cx, y, z, dark);
        set(clipboard, cx, y + 1, z, dark);
        set(clipboard, cx + 1, y, z, dark);
    }

    private void drawClockFaceX(BlockArrayClipboard clipboard, int x, int y, int cz, BlockState face, BlockState dark) {
        fill(clipboard, x, y - 2, cz - 2, x, y + 2, cz + 2, face);
        set(clipboard, x, y, cz, dark);
        set(clipboard, x, y + 1, cz, dark);
        set(clipboard, x, y, cz + 1, dark);
    }

    private void drawObelisk(BlockArrayClipboard clipboard, BuildingShape shape) {
        BlockState stone = state(Material.SMOOTH_SANDSTONE);
        BlockState trim = state(Material.CUT_SANDSTONE);
        int cx = shape.width() / 2;
        int cz = shape.depth() / 2;
        int maxY = shape.height() - 1;
        int base = Math.max(3, Math.min(shape.width(), shape.depth()) / 4);
        fill(clipboard, cx - base - 1, 0, cz - base - 1, cx + base + 1, 0, cz + base + 1, trim);
        for (int y = 1; y <= maxY; y++) {
            double p = y / (double) Math.max(1, maxY);
            int radius = Math.max(0, base - (int) Math.floor(p * base));
            fill(clipboard, cx - radius, y, cz - radius, cx + radius, y, cz + radius, y > maxY - 4 ? trim : stone);
        }
    }

    private void drawEiffelTower(BlockArrayClipboard clipboard, BuildingShape shape) {
        BlockState base = state(Material.SMOOTH_STONE);
        BlockState iron = state(Material.IRON_BLOCK);
        BlockState dark = state(Material.GRAY_CONCRETE);
        BlockState platform = state(Material.POLISHED_DEEPSLATE);
        BlockState light = state(Material.SEA_LANTERN);
        int cx = shape.width() / 2;
        int cz = shape.depth() / 2;
        int maxY = shape.height() - 1;
        int floorOne = Math.max(6, maxY / 4);
        int floorTwo = Math.max(floorOne + 6, maxY / 2);
        int floorThree = Math.max(floorTwo + 5, (maxY * 3) / 4);

        fill(clipboard, 0, 0, 0, shape.width() - 1, 0, shape.depth() - 1, base);
        for (int y = 1; y <= maxY - 4; y++) {
            double p = y / (double) Math.max(1, maxY - 4);
            int spread = Math.max(1, (int) Math.round((shape.width() / 2.0D - 2) * (1.0D - p)));
            int minX = cx - spread;
            int maxX = cx + spread;
            int minZ = cz - spread;
            int maxZ = cz + spread;

            set(clipboard, minX, y, minZ, iron);
            set(clipboard, maxX, y, minZ, iron);
            set(clipboard, minX, y, maxZ, iron);
            set(clipboard, maxX, y, maxZ, iron);
            if (y % 3 == 0) {
                lineX(clipboard, minX, maxX, y, minZ, dark, 2);
                lineX(clipboard, minX, maxX, y, maxZ, dark, 2);
                lineZ(clipboard, minX, y, minZ, maxZ, dark, 2);
                lineZ(clipboard, maxX, y, minZ, maxZ, dark, 2);
            }
            if (y % 5 == 0) {
                set(clipboard, cx, y, cz, light);
            }
        }

        platform(clipboard, cx, cz, floorOne, Math.max(4, shape.width() / 3), platform, iron);
        platform(clipboard, cx, cz, floorTwo, Math.max(3, shape.width() / 4), platform, iron);
        platform(clipboard, cx, cz, floorThree, Math.max(2, shape.width() / 6), platform, iron);
        for (int y = floorThree + 1; y <= maxY; y++) {
            set(clipboard, cx, y, cz, iron);
            if (y < maxY) {
                set(clipboard, cx + 1, y, cz, dark);
                set(clipboard, cx - 1, y, cz, dark);
                set(clipboard, cx, y, cz + 1, dark);
                set(clipboard, cx, y, cz - 1, dark);
            }
        }
    }

    private void drawLatticeTower(BlockArrayClipboard clipboard, BuildingShape shape, BlueprintStyle style) {
        BlockState base = state(Material.SMOOTH_STONE);
        BlockState metal = state(Material.IRON_BLOCK);
        BlockState darkMetal = state(Material.GRAY_CONCRETE);
        BlockState platform = state(Material.POLISHED_DEEPSLATE);
        BlockState light = state(Material.SEA_LANTERN);

        fill(clipboard, 0, 0, 0, shape.width() - 1, 0, shape.depth() - 1, base);

        int cx = shape.width() / 2;
        int cz = shape.depth() / 2;
        int maxY = shape.height() - 1;
        int baseInset = 1;
        int topInset = Math.max(3, shape.width() / 2 - 1);

        for (int y = 1; y <= maxY - 4; y++) {
            double progress = y / (double) Math.max(1, maxY - 4);
            int insetX = Math.min(topInset, baseInset + (int) Math.round(progress * (shape.width() / 2.6D)));
            int insetZ = Math.min(topInset, baseInset + (int) Math.round(progress * (shape.depth() / 2.6D)));
            int minX = Math.min(cx - 1, insetX);
            int maxX = Math.max(cx + 1, shape.width() - 1 - insetX);
            int minZ = Math.min(cz - 1, insetZ);
            int maxZ = Math.max(cz + 1, shape.depth() - 1 - insetZ);

            set(clipboard, minX, y, minZ, metal);
            set(clipboard, maxX, y, minZ, metal);
            set(clipboard, minX, y, maxZ, metal);
            set(clipboard, maxX, y, maxZ, metal);

            if (y % 2 == 0) {
                lineX(clipboard, minX, maxX, y, minZ, darkMetal, 3);
                lineX(clipboard, minX, maxX, y, maxZ, darkMetal, 3);
                lineZ(clipboard, minX, y, minZ, maxZ, darkMetal, 3);
                lineZ(clipboard, maxX, y, minZ, maxZ, darkMetal, 3);
            }

            if (y % 4 == 0) {
                set(clipboard, cx, y, cz, light);
                set(clipboard, cx - 1, y, cz, darkMetal);
                set(clipboard, cx + 1, y, cz, darkMetal);
                set(clipboard, cx, y, cz - 1, darkMetal);
                set(clipboard, cx, y, cz + 1, darkMetal);
            }
        }

        platform(clipboard, cx, cz, Math.max(5, maxY / 3), Math.max(3, shape.width() / 3), platform, metal);
        platform(clipboard, cx, cz, Math.max(8, (maxY * 2) / 3), Math.max(2, shape.width() / 4), platform, metal);
        platform(clipboard, cx, cz, maxY - 5, 2, platform, metal);

        for (int y = maxY - 4; y <= maxY; y++) {
            set(clipboard, cx, y, cz, metal);
            if (y < maxY) {
                set(clipboard, cx + 1, y, cz, darkMetal);
                set(clipboard, cx - 1, y, cz, darkMetal);
                set(clipboard, cx, y, cz + 1, darkMetal);
                set(clipboard, cx, y, cz - 1, darkMetal);
            }
        }
    }

    private void platform(BlockArrayClipboard clipboard, int cx, int cz, int y, int radius, BlockState platform, BlockState edge) {
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                boolean border = x == cx - radius || x == cx + radius || z == cz - radius || z == cz + radius;
                set(clipboard, x, y, z, border ? edge : platform);
            }
        }
    }

    private void lineX(BlockArrayClipboard clipboard, int minX, int maxX, int y, int z, BlockState state, int step) {
        for (int x = minX; x <= maxX; x += step) {
            set(clipboard, x, y, z, state);
        }
    }

    private void lineZ(BlockArrayClipboard clipboard, int x, int y, int minZ, int maxZ, BlockState state, int step) {
        for (int z = minZ; z <= maxZ; z += step) {
            set(clipboard, x, y, z, state);
        }
    }

    private boolean isEiffelLike(GenerationSpec spec) {
        String lower = metadataText(spec);
        return lower.contains("eiffel") || lower.contains("lattice tower") || lower.contains("iron tower");
    }

    private boolean isSpireLandmark(GenerationSpec spec) {
        String lower = metadataText(spec);
        return lower.contains("spire") || lower.contains("monument") || lower.contains("obelisk");
    }

    private boolean isSkyscraper(GenerationSpec spec) {
        String lower = metadataText(spec);
        return lower.contains("skyscraper") || lower.contains("high-rise") || lower.contains("high rise");
    }

    private boolean isClockTower(GenerationSpec spec) {
        String lower = metadataText(spec);
        return lower.contains("clock tower") || lower.contains("bell tower") || lower.contains("big ben");
    }

    private boolean isObelisk(GenerationSpec spec) {
        String lower = metadataText(spec);
        return lower.contains("obelisk");
    }

    private String metadataText(GenerationSpec spec) {
        return spec.realWorldData()
                .map(RealWorldBuildingData::combinedText)
                .orElse(spec.query().toLowerCase(Locale.ROOT));
    }

    private void fill(BlockArrayClipboard clipboard, int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                      BlockState state) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    set(clipboard, x, y, z, state);
                }
            }
        }
    }

    private void set(BlockArrayClipboard clipboard, int x, int y, int z, BlockState state) {
        try {
            clipboard.setBlock(BlockVector3.at(x, y, z), state);
        } catch (Exception exception) {
            plugin.getLogger().warning("Could not set generated block: " + exception.getMessage());
        }
    }

    private BlockState state(Material material) {
        return BukkitAdapter.asBlockType(material).getDefaultState();
    }

    public record Result(int width, int height, int depth) {
    }

    private enum BuildingKind {
        CIVIC,
        STATION,
        AIRPORT,
        PORT,
        TOWER,
        LANDMARK
    }

    private record BuildingShape(BuildingKind kind, int width, int height, int depth) {
        private static BuildingShape from(GenerationSpec spec) {
            int scale = spec.scale();
            String lower = spec.realWorldData()
                    .map(RealWorldBuildingData::combinedText)
                    .orElse(spec.query().toLowerCase(Locale.ROOT));
            BuildingKind kind = BuildingKind.CIVIC;
            if (lower.contains("station") || lower.contains("terminal")) {
                kind = BuildingKind.STATION;
            } else if (lower.contains("airport") || lower.contains("lufthavn")) {
                kind = BuildingKind.AIRPORT;
            } else if (lower.contains("port") || lower.contains("harbor") || lower.contains("havn")) {
                kind = BuildingKind.PORT;
            } else if (lower.contains("tower") || lower.contains("skyscraper") || lower.contains("tarn")
                    || lower.contains("taarn") || lower.contains("tårn")) {
                kind = BuildingKind.TOWER;
            } else if (lower.contains("landmark") || lower.contains("monument") || lower.contains("palace")
                    || lower.contains("castle") || lower.contains("cathedral") || lower.contains("church")
                    || lower.contains("museum")) {
                kind = BuildingKind.LANDMARK;
            }

            BuildingShape base = switch (kind) {
                case STATION -> new BuildingShape(kind, 18 + scale * 10, 8 + scale * 4, 10 + scale * 5);
                case AIRPORT -> new BuildingShape(kind, 22 + scale * 12, 8 + scale * 4, 14 + scale * 6);
                case PORT -> new BuildingShape(kind, 18 + scale * 9, 7 + scale * 3, 16 + scale * 7);
                case TOWER -> new BuildingShape(kind, 14 + scale * 5, 26 + scale * 14, 14 + scale * 5);
                case LANDMARK -> new BuildingShape(kind, 14 + scale * 6, 16 + scale * 8, 14 + scale * 6);
                case CIVIC -> new BuildingShape(kind, 14 + scale * 7, 9 + scale * 5, 12 + scale * 6);
            };
            if (spec.realWorldData().isEmpty()) {
                return base;
            }
            int extraHeight = containsAny(lower, "skyscraper", "tallest", "spire", "clock tower", "bell tower") ? 6 : 0;
            int extraWidth = containsAny(lower, "palace", "museum", "terminal", "station", "cathedral") ? 4 : 0;
            int extraDepth = containsAny(lower, "palace", "museum", "terminal", "station", "cathedral") ? 3 : 0;
            return new BuildingShape(base.kind(), base.width() + extraWidth, base.height() + extraHeight,
                    base.depth() + extraDepth);
        }

        private static boolean containsAny(String text, String... values) {
            for (String value : values) {
                if (text.contains(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
