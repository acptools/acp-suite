package net.acptools.suite.ide.models;

import net.acptools.suite.generator.ACPCompiler;
import net.acptools.suite.generator.CompilationException;
import net.acptools.suite.generator.CompilationSettings;
import net.acptools.suite.generator.models.components.ConfigurationException;
import net.acptools.suite.generator.utils.FileUtils;
import net.acptools.suite.ide.IdeException;
import net.acptools.suite.ide.gui.components.console.ConsoleIde;
import net.acptools.suite.ide.gui.components.console.ConsoleInterface;

import java.io.*;

public class IdeProject {
    private static IdeProject ourInstance = null;

    public static IdeProject getInstance() {
        return ourInstance;
    }

    private File projectFolder = null;
    private ProjectProxy project = null;
    private File source = null;
    private String sourceString = null;
    private boolean opened = false;

    private IdeProject(File projectFolder) throws IdeException {
        try {
            this.projectFolder = projectFolder;
            openProjectDefinition();
            openProjectSource();
            opened = true;
        } catch (FileNotFoundException e) {
            throw new IdeException("Error when opening project. Did you selected acprog project folder?");
        }
    }

    private File getProjectXmlFile() {
        return new File(projectFolder.getPath() + "\\" + projectFolder.getName() + ".xml");
    }

    private File getProjectInoFile() {
        return new File(projectFolder.getPath() + "\\" + projectFolder.getName() + ".ino");
    }

    private String getLibraryName() {
        return projectFolder.getName();
    }

    private void openProjectDefinition() throws IdeException {
        try {
            project = ProjectProxy.loadFromFile(getProjectXmlFile());
        } catch (ConfigurationException e) {
            throw new IdeException(e.getMessage(), e);
        }
    }

    private void openProjectSource() throws FileNotFoundException {
        source = getProjectInoFile();
        sourceString = FileUtils.readFile(source);
    }

    public boolean save(ConsoleInterface console) {
        console.println("Saving ino file.........");
        try (Writer fw = new BufferedWriter(new FileWriter(getProjectInoFile()))) {
            fw.write(sourceString);
            fw.close();
        } catch (Exception e) {
            console.errln(e.getMessage());
            return false;
        }
        console.println("Saving xml file.........");
        project.saveToFile(getProjectXmlFile());
        return true;
    }

    public void close() {
        if (opened) {
            save(ConsoleIde.instance);
        }
    }

    public static void closeProject() {
        ourInstance.close();
        ourInstance = null;
    }

    public static IdeProject openProject(File projectFolder) throws IdeException {
        if (ourInstance != null) {
            closeProject();
        }
        ourInstance = new IdeProject(projectFolder);
        return ourInstance;
    }

    public File getSource() {
        return source;
    }

    public String getSourceString() {
        return sourceString;
    }

    public ProjectProxy getProject() {
        return project;
    }

    public void setSourceString(String sourceString) {
        this.sourceString = sourceString;
    }

    public boolean build(ConsoleInterface console, boolean clean) {
        File acpModulesDirectory = new File(IdeSettings.getInstance().getAcprogModulesFolder());
        File arduinoLibraryDirectory = new File(IdeSettings.getInstance().getArduinoLibraryFolder());

        File projectFile = getProjectXmlFile();

        // Clean (if required)
        if (clean) {
            File libraryDir = new File(arduinoLibraryDirectory, getLibraryName());
            FileUtils.removeDirectory(libraryDir);
        }

        // Build
        try {
            ACPCompiler compiler = new ACPCompiler(acpModulesDirectory);
            CompilationSettings settings = new CompilationSettings();
            settings.setProjectConfigurationFile(projectFile);
            settings.setLibraryName(getLibraryName());
            settings.setOutputLibraryPath(arduinoLibraryDirectory);
            settings.setDebugMode(IdeSettings.getInstance().getDebugMode());
            compiler.compile(settings);
            return true;
        } catch (CompilationException e) {
            console.errln(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verify(ConsoleInterface console) {
        String proccess = IdeSettings.getInstance().getArduinoCli();
        proccess += " --verify";
        proccess += " --board " + "arduino:avr:uno";
        proccess += " --pref build.path=" + getProjectInoFile().getParentFile().getPath() + "\\build";
        //proccess += " --verbose";
        proccess += " " + getProjectInoFile();

        int ret = console.runProccess(proccess);
        if (ret == 0) {
            return true;
        }

        console.errln("Chyba, skontrolujte konzolu pre viac informácií.");
        return false;
    }

    public boolean upload(ConsoleInterface console, String serialPort) {
        String proccess = IdeSettings.getInstance().getArduinoCli();
        proccess += " --upload";
        proccess += " --board " + "arduino:avr:uno";
        proccess += " --port " + serialPort;
        proccess += " --pref build.path=" + getProjectInoFile().getParentFile().getPath() + "\\build";
        //proccess += " --verbose";
        proccess += " " + getProjectInoFile();

        int ret = console.runProccess(proccess);
        if (ret == 0) {
            return true;
        }

        console.errln("Chyba, skontrolujte konzolu pre viac informácií.");
        return false;
    }
}
