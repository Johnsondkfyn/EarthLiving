package dk.earthliving.core.earthos;

import dk.earthliving.core.report.ReportService;
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

    public EarthOsListener(Plugin plugin, EarthOsService earthOsService, ReportService reportService) {
        this.plugin = plugin;
        this.earthOsService = earthOsService;
        this.reportService = reportService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
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
        if (!EarthOsService.MENU_TITLE.equals(title) && !ReportService.MENU_TITLE.equals(title)) {
            return;
        }

        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            if (EarthOsService.MENU_TITLE.equals(title)) {
                earthOsService.handleMenuClick(player, event.getRawSlot());
            } else if (ReportService.MENU_TITLE.equals(title)) {
                reportService.handleClick(player, event.getRawSlot());
            }
            player.updateInventory();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
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
