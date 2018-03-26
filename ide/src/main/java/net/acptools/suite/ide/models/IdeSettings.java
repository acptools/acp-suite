package net.acptools.suite.ide.models;

import net.acptools.suite.generator.models.components.ConfigurationException;
import net.acptools.suite.generator.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IdeSettings {
    private static final String SETTINGS_FILENAME = "acp_settings.xml";

    // region XML element names
    private static final String XML_EL_ROOT = "ide-settings";
    private static final String XML_EL_ACPROG_MODULES = "acprog-modules-folder";
    private static final String XML_EL_ARDUINO_CLI = "arduino-cli";
    private static final String XML_EL_ARDUINO_LIBRARY = "arduino-library-folder";
    private static final String XML_EL_SERIAL_PORT = "serial-port";
    private static final String XML_EL_DEBUG_MODE = "debug-mode";
    private static final String XML_EL_RECENT_PROJECTS = "recent-projects";
    private static final String XML_EL_PROJECT = "project";
    // endregion

    private static final int MAX_RECENT_PROJECTS = 10;

    private static IdeSettings ourInstance = new IdeSettings();
    private List<String> availableBoards = null;

    public static IdeSettings getInstance() {
        return ourInstance;
    }


    private boolean initializedEmpty = true;

    private boolean debugMode;
    private String serialPort = null;
    private String arduinoLibraryFolder;
    private String acprogModulesFolder;
    private String arduinoCli;
    private List<IdeSettingsProject> recentProjects;

    private IdeSettings() {
        loadSettingsFromFile();
    }

    private File getSettingsXmlFile() {
        File f = new File(SETTINGS_FILENAME);
        if (!f.exists()) {
            initializedEmpty = false;
            saveSettingsToFile(f);
        }
        return f;
    }

    private void loadSettingsFromFile() {
        File xmlFile = getSettingsXmlFile();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setCoalescing(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);
            Element xmlRoot = doc.getDocumentElement();
            Element ideXmlRoot = XmlUtils.getChildElement(xmlRoot, XML_EL_ROOT);
            acprogModulesFolder = XmlUtils.getChildElement(xmlRoot, XML_EL_ACPROG_MODULES).getTextContent();
            arduinoCli = XmlUtils.getChildElement(xmlRoot, XML_EL_ARDUINO_CLI).getTextContent();
            arduinoLibraryFolder = XmlUtils.getChildElement(xmlRoot, XML_EL_ARDUINO_LIBRARY).getTextContent();
            try {
                serialPort = XmlUtils.getChildElement(xmlRoot, XML_EL_SERIAL_PORT).getTextContent();
            } catch (Exception e) {
                serialPort = null;
            }
            debugMode = "true".equals(XmlUtils.getChildElement(xmlRoot, XML_EL_DEBUG_MODE).getTextContent().toLowerCase());
            recentProjects = new ArrayList<>();
            Element lastProjectElementRoot = XmlUtils.getChildElement(xmlRoot, XML_EL_RECENT_PROJECTS);
            if (lastProjectElementRoot != null) {
                List<Element> elements = XmlUtils.getChildElements(lastProjectElementRoot, XML_EL_PROJECT);
                if (elements != null) {
                    for (Element element : elements) {
                        recentProjects.add(IdeSettingsProject.loadSettingsFromXml(element));
                    }
                }
            }

        } catch (Exception e) {
            throw new ConfigurationException("Loading of project configuration failed.", e);
        }
    }

    public void saveSettingsToFile() {
        saveSettingsToFile(getSettingsXmlFile());
    }

    private void saveSettingsToFile(File xmlFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setCoalescing(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            // Write actual configuration to root node
            Element xmlRoot = doc.createElement(XML_EL_ROOT);
            doc.appendChild(xmlRoot);

            Element el;
            el = doc.createElement(XML_EL_ACPROG_MODULES);
            el.setTextContent(acprogModulesFolder);
            xmlRoot.appendChild(el);

            el = doc.createElement(XML_EL_ARDUINO_CLI);
            el.setTextContent(arduinoCli);
            xmlRoot.appendChild(el);

            el = doc.createElement(XML_EL_ARDUINO_LIBRARY);
            el.setTextContent(arduinoLibraryFolder);
            xmlRoot.appendChild(el);

            el = doc.createElement(XML_EL_SERIAL_PORT);
            el.setTextContent(serialPort);
            xmlRoot.appendChild(el);

            el = doc.createElement(XML_EL_DEBUG_MODE);
            el.setTextContent(Boolean.toString(debugMode));
            xmlRoot.appendChild(el);

            el = doc.createElement(XML_EL_RECENT_PROJECTS);
            for (IdeSettingsProject project : recentProjects) {
                el.appendChild(project.saveToXml(doc));
            }
            xmlRoot.appendChild(el);

            // Save configuration to XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);

        } catch (Exception e) {
            throw new ConfigurationException("Saving configuration failed.", e);
        }
    }

    public String getAcprogModulesFolder() {
        return acprogModulesFolder;
    }

    public void setAcprogModulesFolder(String acprogModulesFolder) {
        this.acprogModulesFolder = acprogModulesFolder;
    }

    public String getArduinoLibraryFolder() {
        return arduinoLibraryFolder;
    }

    public void setArduinoLibraryFolder(String arduinoLibraryFolder) {
        this.arduinoLibraryFolder = arduinoLibraryFolder;
    }

    public boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getArduinoCli() {
        return arduinoCli;
    }

    public void setArduinoCli(String arduinoCli) {
        this.arduinoCli = arduinoCli;
    }

    public boolean isInitializedEmpty() {
        return initializedEmpty;
    }

    public IdeSettingsProject getRecentProject() {
        if (recentProjects.size() >= 1) {
            return recentProjects.get(recentProjects.size() - 1);
        }
        return null;
    }

    public List<IdeSettingsProject> getRecentProjects() {
        return recentProjects;
    }

    public void addRecentProject(IdeSettingsProject ideSettingsProject) {
        recentProjects.remove(ideSettingsProject);
        recentProjects.add(ideSettingsProject);
        if (recentProjects.size() > MAX_RECENT_PROJECTS) {
            for (int i = 0; i < recentProjects.size() - MAX_RECENT_PROJECTS; i++) {
                recentProjects.remove(0);
            }
        }
        saveSettingsToFile();
    }

    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public List<String> getAvailableBoards() {
        if (availableBoards == null) {
            availableBoards = new ArrayList<>();
            availableBoards.add("Arduino");
            availableBoards.add("ArduinoMega");
            availableBoards.add("ArduinoNano");
            availableBoards.add("ArduinoUno");
        }
        return availableBoards;
    }
}
