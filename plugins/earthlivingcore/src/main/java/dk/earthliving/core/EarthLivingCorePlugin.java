package dk.earthliving.core;

import dk.earthliving.core.command.EarthLivingCommand;
import dk.earthliving.core.discord.DiscordBridgeService;
import dk.earthliving.core.discord.DiscordReportImportService;
import dk.earthliving.core.command.EarthOsCommand;
import dk.earthliving.core.earthos.EarthOsListener;
import dk.earthliving.core.earthos.EarthOsService;
import dk.earthliving.core.event.EarthLivingEventService;
import dk.earthliving.core.module.CoreModule;
import dk.earthliving.core.module.ModuleRegistry;
import dk.earthliving.core.notification.DiscordNotificationService;
import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.passport.PassportService;
import dk.earthliving.core.report.ReportService;
import dk.earthliving.core.webportal.WebPortalService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class EarthLivingCorePlugin extends JavaPlugin {
    private ModuleRegistry moduleRegistry;
    private NotificationService notificationService;
    private DiscordNotificationService discordNotificationService;
    private DiscordBridgeService discordBridgeService;
    private DiscordReportImportService discordReportImportService;
    private EarthLivingEventService eventService;
    private EarthOsService earthOsService;
    private ReportService reportService;
    private WebPortalService webPortalService;
    private PassportService passportService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        notificationService = new NotificationService(this);
        discordBridgeService = new DiscordBridgeService(this, notificationService);
        discordNotificationService = new DiscordNotificationService(this, notificationService, discordBridgeService);
        eventService = new EarthLivingEventService(this, notificationService, discordBridgeService);
        moduleRegistry = new ModuleRegistry();
        reportService = new ReportService(this, notificationService, discordNotificationService);
        passportService = new PassportService(this, notificationService);
        webPortalService = new WebPortalService(this, notificationService, reportService);
        discordReportImportService = new DiscordReportImportService(this, notificationService, reportService);
        earthOsService = new EarthOsService(this, notificationService, reportService, webPortalService, passportService);

        registerModules();
        registerCommands();
        getServer().getPluginManager().registerEvents(new EarthOsListener(this, earthOsService, reportService, webPortalService, passportService), this);
        reportService.startPanelActionProcessor();
        webPortalService.startExporter();
        discordReportImportService.startLater();

        notificationService.console("EarthLivingCore enabled with " + moduleRegistry.enabledModules().size() + " active modules.");
    }

    @Override
    public void onDisable() {
        if (discordReportImportService != null) {
            discordReportImportService.stop();
        }
        if (reportService != null) {
            reportService.stopPanelActionProcessor();
        }
        if (webPortalService != null) {
            webPortalService.stopExporter();
        }
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

    public ReportService reportService() {
        return reportService;
    }

    public WebPortalService webPortalService() {
        return webPortalService;
    }

    public PassportService passportService() {
        return passportService;
    }

    public EarthLivingEventService eventService() {
        return eventService;
    }

    private void registerModules() {
        List<CoreModule> modules = List.of(
                new CoreModule("earthos", "EarthOS menu/device"),
                new CoreModule("webportal", "Website profile bridge and read-only exports"),
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
