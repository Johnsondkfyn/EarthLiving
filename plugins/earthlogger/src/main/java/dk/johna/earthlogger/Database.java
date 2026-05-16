package dk.johna.earthlogger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

final class Database implements AutoCloseable {
    private final Connection connection;

    Database(File databaseFile) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("SQLite JDBC driver was not packaged with EarthLogger.", exception);
        }

        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        initialize();
    }

    private void initialize() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS block_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp INTEGER NOT NULL,
                        action TEXT NOT NULL,
                        player_uuid TEXT NOT NULL,
                        player_name TEXT NOT NULL,
                        world TEXT NOT NULL,
                        x INTEGER NOT NULL,
                        y INTEGER NOT NULL,
                        z INTEGER NOT NULL,
                        block_type TEXT NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE INDEX IF NOT EXISTS idx_block_logs_location
                    ON block_logs(world, x, y, z, id DESC)
                    """);
            statement.executeUpdate("""
                    CREATE INDEX IF NOT EXISTS idx_block_logs_player_time
                    ON block_logs(player_uuid, timestamp DESC)
                    """);
        }
    }

    void log(String action, Player player, Location location, Material blockType) throws SQLException {
        String sql = """
                INSERT INTO block_logs
                (timestamp, action, player_uuid, player_name, world, x, y, z, block_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            UUID uuid = player.getUniqueId();
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, action);
            statement.setString(3, uuid.toString());
            statement.setString(4, player.getName());
            statement.setString(5, location.getWorld().getName());
            statement.setInt(6, location.getBlockX());
            statement.setInt(7, location.getBlockY());
            statement.setInt(8, location.getBlockZ());
            statement.setString(9, blockType.getKey().asString());
            statement.executeUpdate();
        }
    }

    List<BlockLogEntry> lookup(Location location, int limit) throws SQLException {
        String sql = """
                SELECT id, timestamp, action, player_name, block_type, world, x, y, z
                FROM block_logs
                WHERE world = ? AND x = ? AND y = ? AND z = ?
                ORDER BY id DESC
                LIMIT ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location.getWorld().getName());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.setInt(5, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                return readEntries(resultSet);
            }
        }
    }

    long countLogs() throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM block_logs")) {
            return resultSet.next() ? resultSet.getLong(1) : 0L;
        }
    }

    private static List<BlockLogEntry> readEntries(ResultSet resultSet) throws SQLException {
        List<BlockLogEntry> entries = new ArrayList<>();
        while (resultSet.next()) {
            entries.add(new BlockLogEntry(
                    resultSet.getLong("id"),
                    resultSet.getLong("timestamp"),
                    resultSet.getString("action"),
                    resultSet.getString("player_name"),
                    resultSet.getString("block_type"),
                    resultSet.getString("world"),
                    resultSet.getInt("x"),
                    resultSet.getInt("y"),
                    resultSet.getInt("z")
            ));
        }
        return entries;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
