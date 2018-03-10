package net.acptools.suite.generator.compilation;

import java.util.*;

import net.acptools.suite.generator.compilation.ACPCompiler.CompilationContext;
import net.acptools.suite.generator.components.Instance;
import net.acptools.suite.generator.modules.ComponentType;
import net.acptools.suite.generator.modules.Library;
import net.acptools.suite.generator.modules.Module;
import net.acptools.suite.generator.platform.Platform;
import net.acptools.suite.generator.project.Component;
import net.acptools.suite.generator.project.Project;
import net.acptools.suite.generator.utils.FileUtils;

/**
 * Generator of file with acp project header.
 */
public class ACPProjectHeaderGenerator extends ACPContentGenerator {

    @Override
    protected void prepare(CompilationContext compilationContext, Map<String, String> output) {
        Project project = compilationContext.getProject();
        Platform platform = compilationContext.getPlatform();
        Map<String, Module> projectModules = compilationContext.getProjectModules();

        // Filter components with views
        List<Component> componentsWithView = new ArrayList<Component>();
        for (Component component : project.getComponents()) {
            ComponentType ctd = (ComponentType) projectModules.get(component.getType());
            if (ctd.getView() != null) {
                componentsWithView.add(component);
            }
        }

        // Collect header files to include (for views)
        Set<String> includes = new LinkedHashSet<String>();
        for (Component component : componentsWithView) {
            ComponentType ctd = (ComponentType) projectModules.get(component.getType());
            Instance view = ctd.getView();
            for (String include : view.getIncludes()) {
                include = ctd.getName().replace('.', '/') + "/" + include;
                include = "#include <" + include + ">";
                includes.add(FileUtils.mergeSlashes(include));
            }
        }

        // Collect header files to include (for libraries)
        for (String libraryImport : project.getLibraryImports()) {
            Library library = (Library) projectModules.get(libraryImport);
            for (String include : library.getIncludes()) {
                include = library.getName().replace('.', '/') + "/" + include;
                include = "#include <" + include + ">";
                includes.add(FileUtils.mergeSlashes(include));
            }
        }

        // Generate view externs
        List<String> viewExterns = new ArrayList<String>();
        for (Component component : componentsWithView) {
            ComponentType ctd = (ComponentType) projectModules.get(component.getType());
            Instance view = ctd.getView();
            try {
                String viewDeclaration = "extern " + view.generateClassType(component, platform) + " "
                        + component.getName() + ";";
                viewExterns.add(viewDeclaration);
            } catch (Exception e) {
                throw new CompilationException("Class type for view of component " + component.getName()
                        + " is invalid.", e);
            }
        }

        // Generate externs for eeprom variables
        @SuppressWarnings("unchecked")
        List<String> eepromVarExterns = (List<String>) compilationContext.getData().get("EepromExterns");
        if (!eepromVarExterns.isEmpty()) {
            includes.add("#include <" + ACPEepromDataGenerator.EEPROMVARS_HEADER_FILENAME + ">");
        }

        // Make includes unique
        Set<String> uniqueIncludes = new LinkedHashSet<String>(includes);
        includes.clear();
        includes.addAll(uniqueIncludes);

        // Prepare replacements for template
        output.put("includes", FileUtils.mergeLines(includes));
        output.put("views", FileUtils.mergeLines(viewExterns));
        output.put("eepromUsage", compilationContext.getData().get("EepromUsage").toString());
        output.put("eepromVars", FileUtils.mergeLines(eepromVarExterns));
    }

    @Override
    protected void generate(CompilationContext compilationContext, Map<String, String> output) {
        generateOutputFromResourceTemplate("acp_project.h", output, compilationContext.getSettings()
                .getProjectHeaderFile());

    }
}
