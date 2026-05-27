package dk.earthliving.core.preview;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PlacementPreviewListener implements Listener {
    private final PlacementPreviewService placementPreviewService;

    public PlacementPreviewListener(PlacementPreviewService placementPreviewService) {
        this.placementPreviewService = placementPreviewService;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (placementPreviewService.lock(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
