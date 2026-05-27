package dk.earthliving.architect.blueprint;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public record BlueprintStyle(
        String id,
        Material wall,
        Material trim,
        Material glass,
        Material floor,
        Material roof
) {
    public static BlueprintStyle fromConfig(String id, ConfigurationSection section) {
        return new BlueprintStyle(
                id,
                material(section, "wall", Material.WHITE_CONCRETE),
                material(section, "trim", Material.LIGHT_GRAY_CONCRETE),
                material(section, "glass", Material.GLASS),
                material(section, "floor", Material.SMOOTH_STONE),
                material(section, "roof", Material.QUARTZ_SLAB)
        );
    }

    private static Material material(ConfigurationSection section, String key, Material fallback) {
        if (section == null) {
            return fallback;
        }
        Material material = Material.matchMaterial(section.getString(key, fallback.name()));
        return material == null || material.isAir() ? fallback : material;
    }
}
