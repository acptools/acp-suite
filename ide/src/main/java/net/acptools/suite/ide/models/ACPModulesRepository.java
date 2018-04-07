package net.acptools.suite.ide.models;

import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.ide.utils.ACPModules;

import java.util.Map;
import java.util.WeakHashMap;

public enum ACPModulesRepository {

    INSTANCE;

    private Map<String, Module> repository = new WeakHashMap<>();

    public Module getModule(ComponentProxy component) {
        String moduleName = component.getType();
        if (repository.containsKey(moduleName) && repository.get(moduleName) != null) {
            return repository.get(moduleName);
        }
        Module module = ACPModules.getModule(component.getParentComponent());
        repository.put(moduleName, module);
        return module;
    }

}
