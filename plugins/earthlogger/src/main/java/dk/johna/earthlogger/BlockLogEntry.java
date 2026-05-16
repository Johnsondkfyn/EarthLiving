package dk.johna.earthlogger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

record BlockLogEntry(
        long id,
        long timestamp,
        String action,
        String playerName,
        String blockType,
        String world,
        int x,
        int y,
        int z
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    String format() {
        return "#" + id
                + " " + FORMATTER.format(Instant.ofEpochMilli(timestamp))
                + " " + action
                + " " + blockType
                + " by " + playerName
                + " at " + world + " " + x + " " + y + " " + z;
    }
}
