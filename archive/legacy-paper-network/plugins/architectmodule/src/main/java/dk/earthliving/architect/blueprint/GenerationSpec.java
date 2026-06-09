package dk.earthliving.architect.blueprint;

import java.util.Optional;

public record GenerationSpec(String query, int scale, BlueprintStyle style, Optional<RealWorldBuildingData> realWorldData) {
}
