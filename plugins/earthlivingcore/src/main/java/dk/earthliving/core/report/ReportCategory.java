package dk.earthliving.core.report;

import org.bukkit.Material;

public enum ReportCategory {
    BUG("bug", "Bug Report", Material.WRITABLE_BOOK),
    PLAYER("player", "Player Issue", Material.PLAYER_HEAD),
    REGION("region", "Region / Border", Material.FILLED_MAP),
    TRANSPORT("transport", "Transport", Material.MINECART),
    BUILD("build", "Build / World", Material.BRICKS),
    SUGGESTION("suggestion", "Suggestion", Material.AMETHYST_SHARD);

    private final String id;
    private final String title;
    private final Material icon;

    ReportCategory(String id, String title, Material icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Material icon() {
        return icon;
    }
}
