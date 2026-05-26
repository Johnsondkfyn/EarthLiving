package dk.earthliving.core.earthos;

import dk.earthliving.core.passport.PassportService;
import dk.earthliving.core.report.ReportService;
import dk.earthliving.core.webportal.WebPortalService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public final class EarthOsListener implements Listener {
    private final Plugin plugin;
    private final EarthOsService earthOsService;
    private final ReportService reportService;
    private final WebPortalService webPortalService;
    private final PassportService passportService;

    public EarthOsListener(Plugin plugin, EarthOsService earthOsService, ReportService reportService, WebPortalService webPortalService, PassportService passportService) {
        this.plugin = plugin;
        this.earthOsService = earthOsService;
        this.reportService = reportService;
        this.webPortalService = webPortalService;
        this.passportService = passportService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        webPortalService.recordJoin(event.getPlayer());
        passportService.recordJoin(event.getPlayer());
        if (earthOsService.shouldGiveOnJoin()) {
            earthOsService.giveDevice(event.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!earthOsService.isDevice(event.getItem())) {
            return;
        }

        event.setCancelled(true);
        earthOsService.open(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!EarthOsService.MENU_TITLE.equals(title)
                && !ReportService.MENU_TITLE.equals(title)
                && !ReportService.CREATE_MENU_TITLE.equals(title)
                && !ReportService.MY_REPORTS_TITLE.equals(title)
                && !ReportService.ADMIN_REPORTS_TITLE.equals(title)
                && !PassportService.MENU_TITLE.equals(title)) {
            return;
        }

        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            if (EarthOsService.MENU_TITLE.equals(title)) {
                earthOsService.handleMenuClick(player, event.getRawSlot());
            } else if (ReportService.MENU_TITLE.equals(title)) {
                reportService.handleHubClick(player, event.getRawSlot());
            } else if (ReportService.CREATE_MENU_TITLE.equals(title)) {
                reportService.handleCreateClick(player, event.getRawSlot());
            } else if (ReportService.MY_REPORTS_TITLE.equals(title) || ReportService.ADMIN_REPORTS_TITLE.equals(title)) {
                reportService.handleReportListClick(player, event.getRawSlot());
            } else if (PassportService.MENU_TITLE.equals(title)) {
                passportService.handleClick(player, event.getRawSlot());
            }
            player.updateInventory();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (webPortalService.hasPendingLinkInput(player)) {
            event.setCancelled(true);
            String message = event.getMessage();
            player.getServer().getScheduler().runTask(
                    plugin,
                    () -> webPortalService.submitLinkInput(player, message)
            );
            return;
        }

        if (!reportService.hasPendingReport(player)) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        player.getServer().getScheduler().runTask(
                plugin,
                () -> reportService.submitPending(player, message)
        );
    }
}
