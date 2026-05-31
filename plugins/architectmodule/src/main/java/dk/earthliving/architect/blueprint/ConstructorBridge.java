package dk.earthliving.architect.blueprint;

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import dk.earthliving.architect.ArchitectModulePlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ConstructorBridge {
    private final ArchitectModulePlugin plugin;
    private final ArchitectService architectService;
    private final Map<UUID, Integer> selectedBuilders = new java.util.concurrent.ConcurrentHashMap<>();
    private ExecutorService executor;
    private boolean enabled;
    private boolean npcOnlyPlacement;
    private int defaultNpcId;
    private float npcSpeed;
    private File constructorSchematicsFolder;

    public ConstructorBridge(ArchitectModulePlugin plugin, ArchitectService architectService) {
        this.plugin = plugin;
        this.architectService = architectService;
        reload();
    }

    public void reload() {
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "ArchitectModule-ConstructorBridge");
            thread.setDaemon(true);
            return thread;
        });
        enabled = plugin.getConfig().getBoolean("constructor.enabled", true);
        npcOnlyPlacement = plugin.getConfig().getBoolean("constructor.npc-only-placement", true);
        defaultNpcId = plugin.getConfig().getInt("constructor.default-npc-id", -1);
        npcSpeed = (float) plugin.getConfig().getDouble("constructor.npc-speed", 1.0D);
        constructorSchematicsFolder = new File(plugin.getDataFolder().getParentFile(),
                plugin.getConfig().getString("constructor.schematics-folder", "Constructor/schematics/architect"));
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public boolean npcOnlyPlacement() {
        return enabled && npcOnlyPlacement;
    }

    public void selectBuilder(Player player, int npcId) {
        if (!constructorAvailable()) {
            plugin.tell(player, "&cConstructor eller Citizens er ikke aktiv paa denne server.");
            return;
        }
        try {
            if (findNpc(npcId) == null) {
                plugin.tell(player, "&cCitizens NPC &f#" + npcId + " &cfindes ikke.");
                return;
            }
            selectedBuilders.put(player.getUniqueId(), npcId);
            plugin.tell(player, "&aBuilder-NPC valgt: &f#" + npcId + "&a. Venstreklik i preview starter nu NPC-byggeriet.");
        } catch (ReflectiveOperationException exception) {
            plugin.tell(player, "&cKunne ikke kontrollere Citizens NPC: &f" + exception.getMessage());
        }
    }

    public void queueBuild(Player player, BlueprintJob job, Location origin, int rotation) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return architectService.loadClipboard(job);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }, executor).whenComplete((clipboard, throwable) -> {
            if (throwable != null) {
                plugin.getServer().getScheduler().runTask(plugin,
                        () -> plugin.tell(player, "&cKunne ikke laese schematic: &f" + rootMessage(throwable)));
                return;
            }
            queueBuild(player, job, clipboard, origin, rotation);
        });
    }

    public void queueBuild(Player player, BlueprintJob job, Clipboard clipboard, Location origin, int rotation) {
        if (!enabled) {
            plugin.tell(player, "&cConstructor-integration er slaaet fra.");
            return;
        }
        int npcId = selectedBuilders.getOrDefault(player.getUniqueId(), defaultNpcId);
        if (npcId < 0) {
            plugin.tell(player, "&cVaelg foerst en builder-NPC med &f/architect builder <citizens-npc-id>&c.");
            return;
        }
        plugin.tell(player, "&7Eksporterer schematic til Constructor og opretter NPC-byggeordre...");
        CompletableFuture.supplyAsync(() -> exportForConstructor(job, clipboard, rotation), executor)
                .whenComplete((schematicName, throwable) -> plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (throwable != null) {
                        plugin.tell(player, "&cKunne ikke forberede NPC-byggeordre: &f" + rootMessage(throwable));
                        return;
                    }
                    startNpcBuild(player, npcId, origin, schematicName);
                }));
    }

    private String exportForConstructor(BlueprintJob job, Clipboard source, int rotation) {
        try {
            Files.createDirectories(constructorSchematicsFolder.toPath());
            int turns = Math.floorMod(rotation, 4);
            BlockVector3 sourceMin = source.getMinimumPoint();
            BlockVector3 sourceMax = source.getMaximumPoint();
            int width = sourceMax.x() - sourceMin.x() + 1;
            int height = sourceMax.y() - sourceMin.y() + 1;
            int depth = sourceMax.z() - sourceMin.z() + 1;
            int rotatedWidth = turns % 2 == 0 ? width : depth;
            int rotatedDepth = turns % 2 == 0 ? depth : width;
            BlockArrayClipboard target = new BlockArrayClipboard(new CuboidRegion(
                    BlockVector3.ZERO,
                    BlockVector3.at(rotatedWidth - 1, height - 1, rotatedDepth - 1)));
            for (int x = sourceMin.x(); x <= sourceMax.x(); x++) {
                for (int y = sourceMin.y(); y <= sourceMax.y(); y++) {
                    for (int z = sourceMin.z(); z <= sourceMax.z(); z++) {
                        RotatedPoint rotated = rotate(x - sourceMin.x(), z - sourceMin.z(), width, depth, turns);
                        target.setBlock(BlockVector3.at(rotated.x(), y - sourceMin.y(), rotated.z()),
                                source.getBlock(BlockVector3.at(x, y, z)));
                    }
                }
            }
            String fileName = job.id() + "-r" + turns + ".schem";
            File outputFile = new File(constructorSchematicsFolder, fileName);
            ClipboardFormat format = ClipboardFormats.findByAlias("sponge");
            if (format == null) {
                format = ClipboardFormats.findByAlias("schem");
            }
            if (format == null) {
                throw new IllegalStateException("WorldEdit schematic writer mangler");
            }
            try (FileOutputStream output = new FileOutputStream(outputFile);
                 ClipboardWriter writer = format.getWriter(output)) {
                writer.write(target);
            }
            return "architect/" + fileName;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private void startNpcBuild(Player player, int npcId, Location origin, String schematicName) {
        if (!constructorAvailable()) {
            plugin.tell(player, "&cConstructor eller Citizens er ikke aktiv paa denne server.");
            return;
        }
        try {
            if (findNpc(npcId) == null) {
                plugin.tell(player, "&cCitizens NPC &f#" + npcId + " &cfindes ikke laengere.");
                return;
            }
            Class<?> apiClass = Class.forName("fr.weefle.constructor.API.ConstructorAPI");
            Class<?> patternClass = Class.forName("fr.weefle.constructor.hooks.citizens.BuilderTrait$BuildPatternXZ");
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object spiral = Enum.valueOf((Class<? extends Enum>) patternClass.asSubclass(Enum.class), "SPIRAL");
            Method npcBuild = apiClass.getMethod("npcBuild", int.class, Location.class, Float.class,
                    boolean.class, boolean.class, boolean.class, patternClass, String.class, Player.class);
            boolean started = (boolean) npcBuild.invoke(null, npcId, origin, npcSpeed,
                    true, true, false, spiral, schematicName, player);
            if (started) {
                plugin.tell(player, "&aNPC-builder &f#" + npcId + " &ahar faaet byggeordren for &f" + schematicName + "&a.");
            } else {
                plugin.tell(player, "&cConstructor afviste byggeordren. NPC'en bygger muligvis allerede.");
            }
        } catch (InvocationTargetException exception) {
            plugin.tell(player, "&cConstructor-fejl: &f" + rootMessage(exception.getCause()));
        } catch (ReflectiveOperationException exception) {
            plugin.tell(player, "&cKunne ikke starte Constructor: &f" + exception.getMessage());
        }
    }

    private Object findNpc(int npcId) throws ReflectiveOperationException {
        Class<?> apiClass = Class.forName("net.citizensnpcs.api.CitizensAPI");
        Object registry = apiClass.getMethod("getNPCRegistry").invoke(null);
        return registry.getClass().getMethod("getById", int.class).invoke(registry, npcId);
    }

    private boolean constructorAvailable() {
        Plugin constructor = plugin.getServer().getPluginManager().getPlugin("Constructor");
        Plugin citizens = plugin.getServer().getPluginManager().getPlugin("Citizens");
        return constructor != null && constructor.isEnabled() && citizens != null && citizens.isEnabled();
    }

    private RotatedPoint rotate(int x, int z, int width, int depth, int turns) {
        return switch (turns) {
            case 1 -> new RotatedPoint(z, width - 1 - x);
            case 2 -> new RotatedPoint(width - 1 - x, depth - 1 - z);
            case 3 -> new RotatedPoint(depth - 1 - z, x);
            default -> new RotatedPoint(x, z);
        };
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }

    private record RotatedPoint(int x, int z) {
    }
}
