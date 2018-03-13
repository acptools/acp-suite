package net.acptools.suite.ide.configurations;

import net.acptools.suite.generator.utils.XmlUtils;
import net.acptools.suite.ide.IdeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class IdeSettingsProject {
    private String projectDirectory;
    private String projektName;

    public IdeSettingsProject() {

    }

    public IdeSettingsProject(String projectDirectory, String projektName) {
        super();
        this.projectDirectory = projectDirectory;
        this.projektName = projektName;
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public String getProjektName() {
        return projektName;
    }


    public IdeProject getIdeProject() throws IdeException {
        File fc = new File(projectDirectory);
        if (!fc.isDirectory()) {
            throw new IdeException("Open project by selecting folder containing project files.");
        }
        return IdeProject.openProject(fc);
    }

    private static final String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<project platform=\"ArduinoUno\">\n" +
            "</project>\n";

    private static final String inoContent = "//----------------------------------------------------------------------\n" +
            "// Includes required to build the sketch (including ext. dependencies)\n" +
            "#include <{projectName}.h>\n" +
            "//----------------------------------------------------------------------\n" +
            "\n" +
            "//----------------------------------------------------------------------\n" +
            "// Summary of available objects:\n" +
            "//----------------------------------------------------------------------\n";

    private static void createFile(File file, String content) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print(content);
        writer.close();
    }

    // region Open and Create project

    public static IdeSettingsProject fromFile(File fc) throws IdeException {
        IdeSettingsProject result = new IdeSettingsProject();
        result.projectDirectory = fc.getAbsolutePath();
        result.projektName = fc.getName();
        // overenie ci projekt je mozne otvorit
        result.getIdeProject();
        return result;
    }

    public static IdeSettingsProject createNewProject(File inThisDirectory, String newProjectName) throws Exception {
        if (newProjectName.isEmpty()) {
            throw new IdeException("Project name cannot be empty");
        }
        if (!inThisDirectory.isDirectory()) {
            throw new IdeException("inThisDirectory must be directory!");
        }
        File projectDirectory = new File(inThisDirectory.getAbsolutePath() + File.separator + newProjectName + File.separator);
        if (projectDirectory.exists()) {
            throw new IdeException(projectDirectory.toString() + " must not exists!");
        }
        if (!projectDirectory.mkdir()) {
            throw new IdeException("Cannot create project folder " + projectDirectory.toString() + "!");
        }

        createFile(new File(projectDirectory.getAbsoluteFile() + File.separator + newProjectName + ".xml"), xmlContent.replace("{projectName}", newProjectName));
        createFile(new File(projectDirectory.getAbsoluteFile() + File.separator + newProjectName + ".ino"), inoContent.replace("{projectName}", newProjectName));
        return new IdeSettingsProject(projectDirectory.getAbsolutePath(), newProjectName);
    }

    // endregion

    // region XML project manipulation

    public static IdeSettingsProject loadSettingsFromXml(Element element) {
        IdeSettingsProject result = new IdeSettingsProject();
        result.projektName = XmlUtils.getSimplePropertyValue(element, "name", null);
        result.projectDirectory = XmlUtils.getSimplePropertyValue(element, "directory", null);
        return result;
    }

    public Element saveToXml(Document doc) {
        Element projectElement = doc.createElement("project");

        Element nameEl = doc.createElement("name");
        nameEl.setTextContent(projektName);
        Element locationEl = doc.createElement("directory");
        locationEl.setTextContent(projectDirectory);

        projectElement.appendChild(nameEl);
        projectElement.appendChild(locationEl);

        return projectElement;
    }

    // endregion

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            IdeSettingsProject that = (IdeSettingsProject) obj;
            if (this.projectDirectory.equals(that.projectDirectory) &&
                    this.projektName.equals(that.projektName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return projectDirectory.hashCode();
    }

    @Override
    public String toString() {
        return projectDirectory + " " + projektName;
    }
}
