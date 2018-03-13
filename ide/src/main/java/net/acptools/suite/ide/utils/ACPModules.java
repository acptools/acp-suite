package net.acptools.suite.ide.utils;

import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.generator.models.project.Component;
import net.acptools.suite.ide.configurations.IdeSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class ACPModules {
    protected File acpModulesDirectory;

    protected Collection<Module> allModules;

    public ACPModules(File acpModulesDirectory) {
        this.acpModulesDirectory = acpModulesDirectory;
    }

    private void addTree(File file, Collection<Module> all) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (Module.DESCRIPTION_FILE.equals(child.getName())) {
                    all.add(Module.loadFromFile(new File(file, Module.DESCRIPTION_FILE)));
                }
                addTree(child, all);
            }
        }
    }

    public Collection<Module> scanDirectory() {
        allModules = new ArrayList<>();
        addTree(acpModulesDirectory, allModules);
        return allModules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Module module : allModules) {
            sb.append(module.getName());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static Module getModule(Component component) {
        ACPModules m = new ACPModules(new File(IdeSettings.getInstance().getAcprogModulesFolder()));
        for (Module module : m.scanDirectory()) {
            if (module.getName().equals(component.getType())) {
                return module;
            }
        }
        return null;
    }
}
