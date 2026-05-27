package dk.earthliving.architect.blueprint;

import java.nio.file.Path;
import java.time.Instant;

public record BlueprintJob(
        String id,
        String query,
        int scale,
        String style,
        int width,
        int height,
        int depth,
        String status,
        String message,
        Path schematicPath,
        Path metadataPath,
        Instant createdAt
) {
    public BlueprintJob withStatus(String nextStatus, String nextMessage) {
        return new BlueprintJob(id, query, scale, style, width, height, depth, nextStatus, nextMessage,
                schematicPath, metadataPath, createdAt);
    }

    public BlueprintJob withDimensions(int nextWidth, int nextHeight, int nextDepth) {
        return new BlueprintJob(id, query, scale, style, nextWidth, nextHeight, nextDepth, status, message,
                schematicPath, metadataPath, createdAt);
    }
}
