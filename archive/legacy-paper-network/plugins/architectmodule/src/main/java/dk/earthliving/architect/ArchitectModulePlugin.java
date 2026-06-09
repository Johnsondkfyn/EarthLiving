package dk.earthliving.architect;

import dk.earthliving.architect.blueprint.ArchitectService;
import dk.earthliving.architect.blueprint.ConstructorBridge;
import dk.earthliving.architect.blueprint.SchematicPreviewService;
import dk.earthliving.architect.command.ArchitectCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ArchitectModulePlugin extends JavaPlugin {
    private ArchitectService architectService;
    private ConstructorBridge constructorBridge;
    private SchematicPreviewService previewService;
    private String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadLocalConfig();
        architectService = new ArchitectService(this);
        constructorBridge = new ConstructorBridge(this, architectService);
        previewService = new SchematicPreviewService(this, architectService, constructorBridge);

        ArchitectCommand architectCommand = new ArchitectCommand(this, architectService, constructorBridge, previewService);
        PluginCommand command = Objects.requireNonNull(getCommand("architect"));
        command.setExecutor(architectCommand);
        command.setTabCompleter(architectCommand);
        getServer().getPluginManager().registerEvents(previewService, this);

        getLogger().info("ArchitectModule enabled. Generated folder: " + architectService.generatedFolder().getAbsolutePath());
    }

    @Override
    public void onDisable() {
        if (architectService != null) {
            architectService.shutdown();
        }
        if (previewService != null) {
            previewService.shutdown();
        }
        if (constructorBridge != null) {
            constructorBridge.shutdown();
        }
    }

    public void reloadModule() {
        reloadConfig();
        reloadLocalConfig();
        if (architectService != null) {
            architectService.reload();
        }
        if (previewService != null) {
            previewService.reload();
        }
        if (constructorBridge != null) {
            constructorBridge.reload();
        }
    }

    public void tell(org.bukkit.command.CommandSender sender, String message) {
        sender.sendMessage(color(prefix + message));
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message == null ? "" : message);
    }

    private void reloadLocalConfig() {
        prefix = getConfig().getString("messages.prefix", "&8[&bArchitect&8] &r");
    }
}
