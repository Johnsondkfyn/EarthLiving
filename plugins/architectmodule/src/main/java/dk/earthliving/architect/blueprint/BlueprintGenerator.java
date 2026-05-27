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
        BuildingShape shape = BuildingShape.from(spec.query(), spec.scale());
        BlockVector3 min = BlockVector3.ZERO;
        BlockVector3 max = BlockVector3.at(shape.width() - 1, shape.height() - 1, shape.depth() - 1);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(new CuboidRegion(min, max));
        clipboard.setOrigin(BlockVector3.ZERO);

        drawBuilding(clipboard, shape, spec.style());

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
                "Generated Minecraft interpretation",
                schematicPath,
                metadataPath,
                Instant.now()
        ));
        return new Result(shape.width(), shape.height(), shape.depth());
    }

    private void drawBuilding(BlockArrayClipboard clipboard, BuildingShape shape, BlueprintStyle style) {
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
        private static BuildingShape from(String query, int scale) {
            String lower = query.toLowerCase(Locale.ROOT);
            BuildingKind kind = BuildingKind.CIVIC;
            if (lower.contains("station") || lower.contains("terminal")) {
                kind = BuildingKind.STATION;
            } else if (lower.contains("airport") || lower.contains("lufthavn")) {
                kind = BuildingKind.AIRPORT;
            } else if (lower.contains("port") || lower.contains("harbor") || lower.contains("havn")) {
                kind = BuildingKind.PORT;
            } else if (lower.contains("tower") || lower.contains("skyscraper") || lower.contains("tarn")) {
                kind = BuildingKind.TOWER;
            } else if (lower.contains("landmark") || lower.contains("monument")) {
                kind = BuildingKind.LANDMARK;
            }

            return switch (kind) {
                case STATION -> new BuildingShape(kind, 18 + scale * 10, 8 + scale * 4, 10 + scale * 5);
                case AIRPORT -> new BuildingShape(kind, 22 + scale * 12, 8 + scale * 4, 14 + scale * 6);
                case PORT -> new BuildingShape(kind, 18 + scale * 9, 7 + scale * 3, 16 + scale * 7);
                case TOWER -> new BuildingShape(kind, 10 + scale * 4, 18 + scale * 12, 10 + scale * 4);
                case LANDMARK -> new BuildingShape(kind, 14 + scale * 6, 16 + scale * 8, 14 + scale * 6);
                case CIVIC -> new BuildingShape(kind, 14 + scale * 7, 9 + scale * 5, 12 + scale * 6);
            };
        }
    }
}
