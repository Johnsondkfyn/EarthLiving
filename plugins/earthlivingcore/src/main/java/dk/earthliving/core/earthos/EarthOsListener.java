package dk.earthliving.core.earthos;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class EarthOsListener implements Listener {
    private final EarthOsService earthOsService;

    public EarthOsListener(EarthOsService earthOsService) {
        this.earthOsService = earthOsService;
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
        if (!EarthOsService.MENU_TITLE.equals(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            player.updateInventory();
        }
    }
}
