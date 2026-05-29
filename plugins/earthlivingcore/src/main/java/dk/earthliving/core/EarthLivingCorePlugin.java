package dk.earthliving.core;

import dk.earthliving.core.command.EarthLivingCommand;
import dk.earthliving.core.command.EarthLivingBuildCommand;
import dk.earthliving.core.build.BorderControlBuildGenerator;
import dk.earthliving.core.discord.DiscordBridgeService;
import dk.earthliving.core.discord.DiscordReportImportService;
import dk.earthliving.core.command.EarthOsCommand;
import dk.earthliving.core.earthos.EarthOsListener;
import dk.earthliving.core.earthos.EarthOsService;
import dk.earthliving.core.event.EarthLivingEventService;
import dk.earthliving.core.guide.GuideService;
import dk.earthliving.core.jobs.JobsService;
import dk.earthliving.core.module.CoreModule;
import dk.earthliving.core.module.ModuleRegistry;
import dk.earthliving.core.notification.DiscordNotificationService;
import dk.earthliving.core.notification.NotificationService;
import dk.earthliving.core.passport.PassportService;
import dk.earthliving.core.placeholder.EarthLivingPlaceholderExpansion;
import dk.earthliving.core.preview.PlacementPreviewListener;
import dk.earthliving.core.preview.PlacementPreviewService;
import dk.earthliving.core.report.ReportService;
import dk.earthliving.core.verification.VerificationService;
import dk.earthliving.core.wallet.WalletService;
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
    private PlacementPreviewService placementPreviewService;
    private VerificationService verificationService;
    private WalletService walletService;
    private JobsService jobsService;
    private GuideService guideService;
    private EarthLivingPlaceholderExpansion placeholderExpansion;
    private BorderControlBuildGenerator borderControlBuildGenerator;

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
        verificationService = new VerificationService(this, notificationService);
        walletService = new WalletService(this, notificationService);
        jobsService = new JobsService(this, notificationService, walletService);
        guideService = new GuideService(this, notificationService);
        borderControlBuildGenerator = new BorderControlBuildGenerator(notificationService);
        placementPreviewService = new PlacementPreviewService(this, notificationService);
        webPortalService = new WebPortalService(this, notificationService, reportService);
        discordReportImportService = new DiscordReportImportService(this, notificationService, reportService);
        earthOsService = new EarthOsService(this, notificationService, reportService, webPortalService, passportService, verificationService, walletService, jobsService, guideService);

        registerModules();
        registerCommands();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new EarthOsListener(this, earthOsService, reportService, webPortalService, passportService, verificationService, walletService, jobsService, guideService), this);
        getServer().getPluginManager().registerEvents(jobsService, this);
        getServer().getPluginManager().registerEvents(new PlacementPreviewListener(placementPreviewService), this);
        registerPlaceholders();
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
        if (placementPreviewService != null) {
            placementPreviewService.stop();
        }
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
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

    public PlacementPreviewService placementPreviewService() {
        return placementPreviewService;
    }

    public VerificationService verificationService() {
        return verificationService;
    }

    public WalletService walletService() {
        return walletService;
    }

    public JobsService jobsService() {
        return jobsService;
    }

    public GuideService guideService() {
        return guideService;
    }

    private void registerModules() {
        List<CoreModule> modules = List.of(
                new CoreModule("earthos", "EarthOS menu/device"),
                new CoreModule("webportal", "Website profile bridge and read-only exports"),
                new CoreModule("notifications", "Player and admin notifications"),
                new CoreModule("events", "Server event feed foundation"),
                new CoreModule("reports", "Support/report workflow foundation"),
                new CoreModule("passports", "Country/passport integration foundation"),
                new CoreModule("verification", "Discord verification entrypoint"),
                new CoreModule("wallet", "Simple VS2 player balance"),
                new CoreModule("jobs", "Simple VS2 action rewards"),
                new CoreModule("guide", "Simple VS2 onboarding guide"),
                new CoreModule("preview", "Schematic placement preview foundation"),
                new CoreModule("tab-placeholders", "TAB and PlaceholderAPI identity placeholders"),
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

        PluginCommand earthLivingBuildCommand = Objects.requireNonNull(getCommand("elbuild"));
        EarthLivingBuildCommand buildExecutor = new EarthLivingBuildCommand(notificationService, borderControlBuildGenerator);
        earthLivingBuildCommand.setExecutor(buildExecutor);
        earthLivingBuildCommand.setTabCompleter(buildExecutor);
    }

    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            notificationService.console("PlaceholderAPI not found; EarthLiving TAB placeholders are disabled.");
            return;
        }
        placeholderExpansion = new EarthLivingPlaceholderExpansion(this, passportService);
        if (placeholderExpansion.register()) {
            notificationService.console("Registered PlaceholderAPI expansion: %earthliving_*%.");
        } else {
            notificationService.console("Could not register PlaceholderAPI expansion: %earthliving_*%.");
        }
    }
}
