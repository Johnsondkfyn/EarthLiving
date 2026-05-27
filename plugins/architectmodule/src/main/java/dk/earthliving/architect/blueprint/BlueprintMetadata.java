package dk.earthliving.architect.blueprint;

import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

public final class BlueprintMetadata {
    private BlueprintMetadata() {
    }

    public static void write(Path path, BlueprintJob job) throws Exception {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("id", job.id());
        yaml.set("query", job.query());
        yaml.set("scale", job.scale());
        yaml.set("style", job.style());
        yaml.set("width", job.width());
        yaml.set("height", job.height());
        yaml.set("depth", job.depth());
        yaml.set("status", job.status());
        yaml.set("message", job.message());
        yaml.set("schematic", job.schematicPath().toString());
        yaml.set("created-at", job.createdAt().toString());
        yaml.save(path.toFile());
    }

    public static Optional<BlueprintJob> read(Path path) {
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(path.toFile());
            String id = yaml.getString("id");
            String query = yaml.getString("query");
            String schematic = yaml.getString("schematic");
            if (id == null || query == null || schematic == null) {
                return Optional.empty();
            }
            return Optional.of(new BlueprintJob(
                    id,
                    query,
                    yaml.getInt("scale", 1),
                    yaml.getString("style", "modern"),
                    yaml.getInt("width", 0),
                    yaml.getInt("height", 0),
                    yaml.getInt("depth", 0),
                    yaml.getString("status", "ready"),
                    yaml.getString("message", ""),
                    Path.of(schematic),
                    path,
                    Instant.parse(yaml.getString("created-at", Instant.EPOCH.toString()))
            ));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
