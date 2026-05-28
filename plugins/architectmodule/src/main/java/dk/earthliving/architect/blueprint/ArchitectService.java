package dk.earthliving.architect.blueprint;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import dk.earthliving.architect.ArchitectModulePlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ArchitectService {
    private final ArchitectModulePlugin plugin;
    private final Map<String, BlueprintJob> jobs = new LinkedHashMap<>();
    private ExecutorService executor;
    private File generatedFolder;
    private int maxScale;
    private int defaultScale;
    private String defaultStyle;
    private boolean allowWebLookup;
    private boolean pasteIgnoreAir;
    private int pasteMaxBlocks;
    private int maxJobs;

    public ArchitectService(ArchitectModulePlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable, "ArchitectModule-Worker");
            thread.setDaemon(true);
            return thread;
        });
        maxScale = Math.max(1, plugin.getConfig().getInt("generation.max-scale", 4));
        defaultScale = clampScale(plugin.getConfig().getInt("generation.default-scale", 1));
        defaultStyle = plugin.getConfig().getString("generation.default-style", "modern");
        allowWebLookup = plugin.getConfig().getBoolean("generation.allow-web-lookup", false);
        pasteIgnoreAir = plugin.getConfig().getBoolean("generation.paste-ignore-air", true);
        pasteMaxBlocks = Math.max(1000, plugin.getConfig().getInt("generation.paste-max-blocks", 200000));
        maxJobs = Math.max(10, plugin.getConfig().getInt("generation.max-jobs", 100));
        generatedFolder = new File(plugin.getDataFolder(), plugin.getConfig().getString("generation.generated-folder", "generated"));
        if (!generatedFolder.exists() && !generatedFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create generated folder: " + generatedFolder.getAbsolutePath());
        }
        loadExistingJobs();
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public File generatedFolder() {
        return generatedFolder;
    }

    public boolean webLookupEnabled() {
        return allowWebLookup;
    }

    public List<String> styles() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("styles");
        return section == null ? List.of(defaultStyle) : new ArrayList<>(section.getKeys(false));
    }

    public BlueprintStyle style(String styleId) {
        String id = styleId == null || styleId.isBlank() ? defaultStyle : styleId.toLowerCase(Locale.ROOT);
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("styles." + id);
        if (section == null) {
            section = plugin.getConfig().getConfigurationSection("styles." + defaultStyle);
            id = defaultStyle;
        }
        return BlueprintStyle.fromConfig(id, section);
    }

    public int defaultScale() {
        return defaultScale;
    }

    public String defaultStyle() {
        return defaultStyle;
    }

    public int clampScale(int scale) {
        return Math.max(1, Math.min(maxScale, scale));
    }

    public List<BlueprintJob> jobs() {
        return jobs.values().stream()
                .sorted(Comparator.comparing(BlueprintJob::createdAt).reversed())
                .toList();
    }

    public Optional<BlueprintJob> find(String id) {
        return Optional.ofNullable(jobs.get(id));
    }

    public CompletableFuture<BlueprintJob> generate(CommandSender requester, String query, int scale, String styleId) {
        trimJobs();
        String id = "arch-" + UUID.randomUUID().toString().substring(0, 8);
        Path schematic = generatedFolder.toPath().resolve(id + ".schem");
        Path metadata = generatedFolder.toPath().resolve(id + ".yml");
        BlueprintJob pending = new BlueprintJob(id, query, clampScale(scale), style(styleId).id(), 0, 0, 0,
                "queued", "Waiting for local generator", schematic, metadata, Instant.now());
        jobs.put(id, pending);

        return CompletableFuture.supplyAsync(() -> {
            BlueprintJob running = pending.withStatus("running", allowWebLookup
                    ? "Web lookup is configured but V1 uses local generator only"
                    : "Web lookup disabled; using local generator");
            jobs.put(id, running);
            try {
                BlueprintGenerator.Result result = new BlueprintGenerator(plugin).generate(
                        new GenerationSpec(query, pending.scale(), style(styleId)),
                        schematic,
                        metadata,
                        id
                );
                BlueprintJob done = running.withDimensions(result.width(), result.height(), result.depth())
                        .withStatus("ready", "Generated Minecraft interpretation");
                jobs.put(id, done);
                return done;
            } catch (Exception exception) {
                BlueprintJob failed = running.withStatus("failed", exception.getMessage());
                jobs.put(id, failed);
                throw new IllegalStateException(exception);
            }
        }, executor).whenComplete((job, throwable) -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (throwable == null) {
                plugin.tell(requester, "&aGenerated &f" + job.id() + " &7(" + job.width() + "x" + job.height()
                        + "x" + job.depth() + ")&a.");
            } else {
                plugin.tell(requester, "&cGeneration failed: &f" + throwable.getCause().getMessage());
            }
        }));
    }

    public void search(CommandSender requester, String query) {
        CompletableFuture.supplyAsync(() -> BuildingPlan.describe(query, allowWebLookup), executor)
                .whenComplete((plan, throwable) -> plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (throwable != null) {
                        plugin.tell(requester, "&cSearch failed: &f" + throwable.getCause().getMessage());
                        return;
                    }
                    plugin.tell(requester, "&bBlueprint plan: &f" + plan.kind());
                    plugin.tell(requester, "&7Forslag: &f/architect generate " + query + " "
                            + defaultScale + " " + defaultStyle);
                    plugin.tell(requester, "&7Note: &f" + plan.note());
                }));
    }

    public void pasteAsync(Player player, BlueprintJob job, boolean pasteAtLook) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return loadClipboard(job);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }, executor).whenComplete((clipboard, throwable) -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (throwable != null) {
                plugin.tell(player, "&cCould not load schematic: &f" + throwable.getCause().getMessage());
                return;
            }
            try {
                Location origin = pasteAtLook ? targetLocation(player) : player.getLocation();
                pasteLoaded(player, job, clipboard, origin, 0);
            } catch (Exception exception) {
                plugin.tell(player, "&cPaste failed: &f" + exception.getMessage());
                plugin.getLogger().warning("Paste failed for " + job.id() + ": " + exception.getMessage());
            }
        }));
    }

    Clipboard loadClipboard(BlueprintJob job) throws Exception {
        if (!"ready".equalsIgnoreCase(job.status())) {
            throw new IllegalStateException("Blueprint is not ready: " + job.status());
        }
        File file = job.schematicPath().toFile();
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IllegalStateException("WorldEdit could not detect schematic format");
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        }
    }

    private Location targetLocation(Player player) {
        Block block = player.getTargetBlockExact(120);
        if (block == null) {
            return player.getLocation();
        }
        return block.getLocation().add(0, 1, 0);
    }

    public void pasteLoaded(Player player, BlueprintJob job, Clipboard clipboard, Location location) throws Exception {
        pasteLoaded(player, job, clipboard, location, 0);
    }

    public void pasteLoaded(Player player, BlueprintJob job, Clipboard clipboard, Location location, int rotation) throws Exception {
        long blockCount = (long) job.width() * job.height() * job.depth();
        if (blockCount > pasteMaxBlocks) {
            throw new IllegalStateException("Blueprint is too large for paste limit: " + blockCount + "/" + pasteMaxBlocks);
        }
        BlockVector3 pasteAt = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(player.getWorld()))) {
            BlockVector3 min = clipboard.getMinimumPoint();
            BlockVector3 max = clipboard.getMaximumPoint();
            int width = max.x() - min.x() + 1;
            int depth = max.z() - min.z() + 1;
            int turns = Math.floorMod(rotation, 4);
            for (int x = min.x(); x <= max.x(); x++) {
                for (int y = min.y(); y <= max.y(); y++) {
                    for (int z = min.z(); z <= max.z(); z++) {
                        BlockState state = clipboard.getBlock(BlockVector3.at(x, y, z));
                        if (pasteIgnoreAir && state.getBlockType() == BlockTypes.AIR) {
                            continue;
                        }
                        int localX = x - min.x();
                        int localY = y - min.y();
                        int localZ = z - min.z();
                        RotatedPoint rotated = rotate(localX, localZ, width, depth, turns);
                        editSession.setBlock(pasteAt.add(rotated.x(), localY, rotated.z()), state);
                    }
                }
            }
            WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).remember(editSession);
        }
        plugin.tell(player, "&aPasted &f" + job.id() + " &aat din position.");
    }

    public void undoLastPaste(Player player) throws Exception {
        Actor actor = BukkitAdapter.adapt(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
        EditSession undoSession = session.undo(null, actor);
        if (undoSession != null) {
            undoSession.close();
        }
        plugin.tell(player, "&aSidste Architect/WorldEdit handling er rullet tilbage.");
    }

    private RotatedPoint rotate(int x, int z, int width, int depth, int turns) {
        return switch (turns) {
            case 1 -> new RotatedPoint(z, width - 1 - x);
            case 2 -> new RotatedPoint(width - 1 - x, depth - 1 - z);
            case 3 -> new RotatedPoint(depth - 1 - z, x);
            default -> new RotatedPoint(x, z);
        };
    }

    private record RotatedPoint(int x, int z) {
    }

    private void loadExistingJobs() {
        jobs.clear();
        File[] files = generatedFolder.listFiles((directory, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            BlueprintMetadata.read(file.toPath()).ifPresent(job -> jobs.put(job.id(), job));
        }
    }

    private void trimJobs() {
        while (jobs.size() >= maxJobs) {
            String first = jobs.keySet().iterator().next();
            jobs.remove(first);
        }
    }

    private record BuildingPlan(String kind, String note) {
        private static BuildingPlan describe(String query, boolean webLookup) {
            String lower = query.toLowerCase(Locale.ROOT);
            String kind;
            if (lower.contains("station") || lower.contains("terminal")) {
                kind = "transport station";
            } else if (lower.contains("airport") || lower.contains("lufthavn")) {
                kind = "airport terminal";
            } else if (lower.contains("port") || lower.contains("harbor") || lower.contains("havn")) {
                kind = "port/harbor structure";
            } else if (lower.contains("tower") || lower.contains("skyscraper") || lower.contains("tarn")) {
                kind = "tower/landmark";
            } else {
                kind = "civic building";
            }
            String note = webLookup
                    ? "Web lookup is configured, but V1 still uses the safe local generator."
                    : "Web/AI lookup is disabled. V1 generates a local Minecraft interpretation.";
            return new BuildingPlan(kind, note);
        }
    }
}
