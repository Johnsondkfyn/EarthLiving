package dk.earthliving.core.command;

import dk.earthliving.core.EarthLivingCorePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class EarthOsCommand implements CommandExecutor {
    private final EarthLivingCorePlugin plugin;

    public EarthOsCommand(EarthLivingCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.notificationService().send(sender, "&cOnly players can open EarthOS.");
            return true;
        }

        plugin.earthOsService().open(player);
        return true;
    }
}
