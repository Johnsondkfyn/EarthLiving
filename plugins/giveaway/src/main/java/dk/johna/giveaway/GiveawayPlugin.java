package dk.johna.giveaway;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class GiveawayPlugin extends JavaPlugin implements CommandExecutor, Listener {
    private int randomNumber = 1;
    private boolean inProgress;
    private boolean postGame;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        var command = getCommand("giveaway");
        if (command != null) {
            command.setExecutor(this);
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ga.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1 || !isInteger(args[0])) {
            sendHelp(sender);
            return true;
        }

        if (inProgress) {
            sender.sendMessage(ChatColor.RED + "There is already a giveaway in progress!");
            return true;
        }

        int max = Integer.parseInt(args[0]);
        if (max < 1) {
            sendHelp(sender);
            return true;
        }

        inProgress = true;
        postGame = false;
        randomNumber = ThreadLocalRandom.current().nextInt(max + 1);

        if (getConfig().getBoolean("tell_command_sender", true)) {
            sender.sendMessage(ChatColor.GREEN + "The number is: " + randomNumber);
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "A giveaway has begun!");
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Type a number between 0 and " + max + "!");
        return true;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (postGame) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This giveaway has ended stop guessing!");
            return;
        }

        if (!inProgress) {
            return;
        }

        String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
        if (!isInteger(message)) {
            return;
        }

        int guess = Integer.parseInt(message);
        if (guess != randomNumber) {
            return;
        }

        event.setCancelled(true);
        Player winner = event.getPlayer();
        Bukkit.getScheduler().runTask(this, () -> finishGiveaway(winner));
    }

    private void finishGiveaway(Player winner) {
        if (!inProgress) {
            return;
        }

        postGame = true;
        inProgress = false;
        winner.getInventory().addItem(new ItemStack(Material.DIAMOND, 10));
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + winner.getName() + " has won the game!");
        winner.sendMessage(ChatColor.GREEN + "You won 10 diamonds!");

        long delayTicks = getConfig().getLong("silence_after_giveaway", 5L) * 20L;
        Bukkit.getScheduler().runTaskLater(this, () -> postGame = false, Math.max(0L, delayTicks));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Proper use is /giveaway <number>");
        sender.sendMessage(ChatColor.RED + "The number is used for a random number between 0 and <number>.");
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
