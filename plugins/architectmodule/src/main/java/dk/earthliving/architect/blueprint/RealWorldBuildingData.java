package dk.earthliving.architect.blueprint;

import java.util.Locale;
import java.util.Optional;

public record RealWorldBuildingData(
        String title,
        String description,
        String extract,
        String url,
        String source
) {
    public String combinedText() {
        return (title + " " + description + " " + extract).toLowerCase(Locale.ROOT);
    }

    public Optional<String> shortDescription() {
        if (description == null || description.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(description);
    }

    public String summaryLine() {
        if (title == null || title.isBlank()) {
            return "No real-world title";
        }
        if (description == null || description.isBlank()) {
            return title;
        }
        return title + " - " + description;
    }
}
