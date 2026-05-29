package dk.earthliving.core.verification;

import dk.earthliving.core.earthos.EarthOsService;
import dk.earthliving.core.notification.NotificationService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class VerificationService {
    public static final String MENU_TITLE = "EarthOS Verification";
    private static final int SLOT_INFO = 11;
    private static final int SLOT_START = 13;
    private static final int SLOT_BACK = 22;

    private final JavaPlugin plugin;
    private final NotificationService notifications;

    public VerificationService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        inventory.setItem(SLOT_INFO, item(Material.BOOK, configText("earthos.verification.info-name", "&bHow verification works"),
                configLore("earthos.verification.info-lore", List.of(
                        "&7Start the Discord link flow.",
                        "&7Follow the code instructions in chat.",
                        "&7Rejoin after your account is linked."
                ))));
        inventory.setItem(SLOT_START, item(Material.LIME_DYE, configText("earthos.verification.start-name", "&aStart Discord verification"),
                configLore("earthos.verification.start-lore", List.of(
                        "&7Runs the configured DiscordSRV link command.",
                        "&7No passwords are handled by EarthLivingCore."
                ))));
        inventory.setItem(SLOT_BACK, item(Material.ARROW, configText("earthos.verification.back-name", "&fBack to EarthOS"),
                List.of(notifications.color("&7Return to the main EarthOS menu."))));
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot, EarthOsService earthOsService) {
        if (slot == SLOT_BACK) {
            earthOsService.open(player);
            return;
        }
        if (slot != SLOT_START && slot != SLOT_INFO) {
            return;
        }
        if (!player.hasPermission("earthliving.verify")) {
            notifications.send(player, configText("messages.no-verify-permission", "&cYou do not have permission to start verification."));
            return;
        }
        if (slot == SLOT_INFO) {
            sendLines(player, "earthos.verification.instructions");
            return;
        }
        sendLines(player, "earthos.verification.before-start");
        String command = plugin.getConfig().getString("earthos.verification.start-command", "discord link");
        if (command != null && !command.isBlank()) {
            player.performCommand(command.replaceFirst("^/", ""));
        }
        sendLines(player, "earthos.verification.after-start");
    }

    private void sendLines(Player player, String path) {
        List<String> lines = plugin.getConfig().getStringList(path);
        if (lines.isEmpty()) {
            notifications.send(player, "&7Verification is configured through DiscordSRV.");
            return;
        }
        for (String line : lines) {
            notifications.send(player, line);
        }
    }

    private String configText(String path, String fallback) {
        return plugin.getConfig().getString(path, fallback);
    }

    private List<String> configLore(String path, List<String> fallback) {
        List<String> configured = plugin.getConfig().getStringList(path);
        List<String> source = configured.isEmpty() ? fallback : configured;
        List<String> colored = new ArrayList<>();
        for (String line : source) {
            colored.add(notifications.color(line));
        }
        return colored;
    }

    private ItemStack item(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(name));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
