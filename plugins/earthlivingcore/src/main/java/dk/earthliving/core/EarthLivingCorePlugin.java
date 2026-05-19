package dk.earthliving.core;

import dk.earthliving.core.command.EarthLivingCommand;
import dk.earthliving.core.command.EarthOsCommand;
import dk.earthliving.core.earthos.EarthOsListener;
import dk.earthliving.core.earthos.EarthOsService;
import dk.earthliving.core.module.CoreModule;
import dk.earthliving.core.module.ModuleRegistry;
import dk.earthliving.core.notification.NotificationService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class EarthLivingCorePlugin extends JavaPlugin {
    private ModuleRegistry moduleRegistry;
    private NotificationService notificationService;
    private EarthOsService earthOsService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        notificationService = new NotificationService(this);
        moduleRegistry = new ModuleRegistry();
        earthOsService = new EarthOsService(this, notificationService);

        registerModules();
        registerCommands();
        getServer().getPluginManager().registerEvents(new EarthOsListener(earthOsService), this);

        notificationService.console("EarthLivingCore enabled with " + moduleRegistry.enabledModules().size() + " active modules.");
    }

    @Override
    public void onDisable() {
        if (notificationService != null) {
            notificationService.console("EarthLivingCore disabled.");
        }
    }

    public void reloadCore() {
        reloadConfig();
        moduleRegistry.clear();
        registerModules();
        notificationService.reloadPrefix();
    }

    public ModuleRegistry moduleRegistry() {
        return moduleRegistry;
    }

    public NotificationService notificationService() {
        return notificationService;
    }

    public EarthOsService earthOsService() {
        return earthOsService;
    }

    private void registerModules() {
        List<CoreModule> modules = List.of(
                new CoreModule("earthos", "EarthOS menu/device"),
                new CoreModule("notifications", "Player and admin notifications"),
                new CoreModule("events", "Server event feed foundation"),
                new CoreModule("reports", "Support/report workflow foundation"),
                new CoreModule("passports", "Country/passport integration foundation"),
                new CoreModule("discord", "Discord integration foundation")
        );

        for (CoreModule module : modules) {
            boolean enabled = getConfig().getBoolean("modules." + module.id(), true);
            moduleRegistry.register(module.withEnabled(enabled));
        }
    }

    private void registerCommands() {
        PluginCommand earthLivingCommand = Objects.requireNonNull(getCommand("earthliving"));
        EarthLivingCommand commandExecutor = new EarthLivingCommand(this);
        earthLivingCommand.setExecutor(commandExecutor);
        earthLivingCommand.setTabCompleter(commandExecutor);

        PluginCommand earthOsCommand = Objects.requireNonNull(getCommand("earthos"));
        EarthOsCommand earthOsExecutor = new EarthOsCommand(this);
        earthOsCommand.setExecutor(earthOsExecutor);
    }
}
