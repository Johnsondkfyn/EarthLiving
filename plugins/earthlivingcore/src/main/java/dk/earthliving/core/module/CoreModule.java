package dk.earthliving.core.module;

public record CoreModule(String id, String description, boolean enabled) {
    public CoreModule(String id, String description) {
        this(id, description, true);
    }

    public CoreModule withEnabled(boolean enabled) {
        return new CoreModule(id, description, enabled);
    }
}
