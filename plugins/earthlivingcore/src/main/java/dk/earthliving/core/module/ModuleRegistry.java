package dk.earthliving.core.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ModuleRegistry {
    private final Map<String, CoreModule> modules = new LinkedHashMap<>();

    public void register(CoreModule module) {
        modules.put(module.id(), module);
    }

    public Optional<CoreModule> find(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public Collection<CoreModule> modules() {
        return List.copyOf(modules.values());
    }

    public List<CoreModule> enabledModules() {
        List<CoreModule> enabled = new ArrayList<>();
        for (CoreModule module : modules.values()) {
            if (module.enabled()) {
                enabled.add(module);
            }
        }
        return enabled;
    }

    public void clear() {
        modules.clear();
    }
}
