package dk.earthliving.core.guide;

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

public final class GuideService {
    public static final String MENU_TITLE = "EarthOS Guide";

    private final JavaPlugin plugin;
    private final NotificationService notifications;

    public GuideService(JavaPlugin plugin, NotificationService notifications) {
        this.plugin = plugin;
        this.notifications = notifications;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        List<String> steps = plugin.getConfig().getStringList("guide.steps");
        if (steps.isEmpty()) {
            steps = List.of(
                    "&71. Verify Discord",
                    "&72. Check Passport",
                    "&73. Select Main server",
                    "&74. Earn first coins"
            );
        }
        inventory.setItem(13, item(Material.BOOK, "&6EarthLiving Guide", steps));
        inventory.setItem(22, item(Material.BARRIER, "&cClose", List.of("&7This is a simple VS2 guide.")));
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot) {
        if (slot == 22) {
            player.closeInventory();
        }
    }

    public void send(Player player) {
        List<String> steps = plugin.getConfig().getStringList("guide.steps");
        for (String step : steps) {
            notifications.send(player, step);
        }
    }

    private ItemStack item(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(notifications.color(name));
        List<String> colored = new ArrayList<>();
        for (String line : lore) {
            colored.add(notifications.color(line));
        }
        meta.setLore(colored);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
