package net.acptools.suite.ide.project;

import net.acptools.suite.generator.models.components.ConfigurationException;
import net.acptools.suite.generator.models.modules.ComponentType;
import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.generator.models.project.Component;
import net.acptools.suite.generator.models.project.EepromItem;
import net.acptools.suite.generator.models.project.Project;
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
import java.net.URL;
import java.util.*;

public class ProjectProxy implements ComponentInterface {

    private final Group undefinedGroup = new Group("undefined group");

    public static class Group {
        public String name;
        public boolean projectComponentGroup;
        public boolean expanded;

        public Group() {
        }

        public Group(String name) {
            this(name, false);
        }

        public Group(String name, boolean projectComponentGroup) {
            this.name = name;
            this.projectComponentGroup = projectComponentGroup;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    protected Project parentProject;

    protected Map<String, String> properties;

    protected Map<ProjectProxy.Group, List<ComponentInterface>> components = new LinkedHashMap<>();

    public ProjectProxy(Project parentProject) {
        this.parentProject = parentProject;
    }

    // --------------------------------------------------------------------------------------
    // GETTERS / SETTERS
    // --------------------------------------------------------------------------------------

    public Map<Group, List<ComponentInterface>> getComponents() {
        return components;
    }

    public Map<String, ComponentProxy> getComponentsMap() {
        Map<String, ComponentProxy> map = new HashMap<>();

        List<ComponentProxy> components = new ArrayList<>();
        for (Component component : parentProject.getComponents()) {
            components.add(new ComponentProxy(component));
        }
        for (ComponentProxy component : components) {
            map.put(component.getName(), component);
        }

        return map;
    }

    public List<ComponentInterface> createAndGetGroup(ProjectProxy.Group group) {
        if (!components.containsKey(group)) {
            components.put(group, new ArrayList<>());
        }
        return components.get(group);
    }

    public void removeGroup(ProjectProxy.Group group) {
        removeGroup(group, undefinedGroup);
    }

    public void removeGroup(ProjectProxy.Group group, ProjectProxy.Group newGroup) {
        if (components.containsKey(group)) {
            ComponentInterface[] data = new ComponentInterface[components.get(group).size()];
            components.get(group).toArray(data);
            for (ComponentInterface component : data) {
                moveComponent((ComponentProxy) component, newGroup);
            }
            if (components.get(group).size() == 0) {
                components.remove(group);
            }
        }
    }

    public void addComponent(ComponentProxy component, ProjectProxy.Group group) {
        if (group == null) {
            group = undefinedGroup;
        }
        createAndGetGroup(group).add(component);
        parentProject.getComponents().add(component.getParentComponent());
    }

    public void moveComponent(ComponentProxy component, Group newGroup) {
        removeComponent(component);
        addComponent(component, newGroup);
    }

    public void removeComponent(ComponentProxy component) {
        for (Map.Entry<Group, List<ComponentInterface>> entry : components.entrySet()) {
            entry.getValue().remove(component);
        }
        parentProject.getComponents().remove(component.getParentComponent());
    }

    public String getPlatformName() {
        return parentProject.getPlatformName();
    }

    public void setPlatformName(String platformName) {
        properties.replace("PlatformName", platformName);
    }

    public List<String> getLibraryImports() {
        return parentProject.getLibraryImports();
    }

    public List<EepromItem> getEepromItems() {
        return parentProject.getEepromItems();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public Map<String, String> getEvents() {
        return parentProject.getProgramEvents();
    }

    @Override
    public String getName() {
        return "Project component";
    }

    @Override
    public void setName(String name) {
        throw new RuntimeException("You cannot change project component name.");
    }

    @Override
    public String getType() {
        return "project";
    }

    @Override
    public void setType(String type) {
        throw new RuntimeException("Project component type cannot be changed.");
    }

    @Override
    public String getDescription() {
        return "Runtime configurations for your project.";
    }

    @Override
    public void setDescription(String description) {
        throw new RuntimeException("Project component description cannot be changed.");
    }

    @Override
    public Module getModuleInstance() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL filePath = Objects.requireNonNull(classLoader.getResource("project-component.xml"));
        File file = new File(filePath.getFile());
        return ComponentType.loadFromFile(file);
    }


    // --------------------------------------------------------------------------------------
    // IDE CONFIGURATION READER
    // --------------------------------------------------------------------------------------

    private void readConfiguration(Element xmlRoot) {
        createDefaultConfiguration();

        // Initialize project properties
        properties.put("EepromLayoutVersion", parentProject.getEepromLayoutVersion());
        properties.put("PlatformName", parentProject.getPlatformName());
        properties.put("WatchdogLevel", Integer.toString(parentProject.getWatchdogLevel()));

        // Add all other components
        Map<String, ComponentProxy> componentsMap = getComponentsMap();


        Element groupsWrapper = XmlUtils.getChildElement(xmlRoot, "groups");
        for (Element xmlGroup : XmlUtils.getChildElements(groupsWrapper, "group")) {
            Group newGroup = new Group();
            newGroup.name = XmlUtils.getSimpleAttributeValue(xmlGroup, "name", null);
            newGroup.expanded = "true".equals(XmlUtils.getSimpleAttributeValue(xmlGroup, "expanded", "true"));
            List<ComponentInterface> newGroupComponentsList = new ArrayList<>();
            for (Element groupComponent : XmlUtils.getChildElements(xmlGroup, "component")) {
                String componentName = XmlUtils.getElementValue(groupComponent, null);
                ComponentInterface component = componentsMap.getOrDefault(componentName, null);
                if (component != null) {
                    newGroupComponentsList.add(component);
                }
            }
            components.put(newGroup, newGroupComponentsList);
        }

        assignRestComponents();
    }

    private void createDefaultConfiguration() {
        properties = new HashMap<>();
        components = new LinkedHashMap<>();

        // Insert project component group
        List<ComponentInterface> projectComponentGroupList = new ArrayList<ComponentInterface>(1);
        projectComponentGroupList.add(this);
        components.put(new Group("Project component group", true), projectComponentGroupList);
    }

    private void assignRestComponents() {
        // Read all currenty assigned components
        Map<String, Boolean> selectedComponents = new LinkedHashMap<>();
        for (Map.Entry<Group, List<ComponentInterface>> entry : components.entrySet()) {
            for (ComponentInterface componentInterface : entry.getValue()) {
                selectedComponents.put(componentInterface.getName(), true);
            }
        }

        // Load all components
        Map<String, ComponentProxy> componentsMap = getComponentsMap();

        // Add to undefined group all rest components
        int size = componentsMap.size() - selectedComponents.size();

        if (size > 0) {
            // find and add rest component
            List<ComponentInterface> newGroupComponentsList = new ArrayList<>(size);
            for (Map.Entry<String, ComponentProxy> entry : componentsMap.entrySet()) {
                if (!selectedComponents.containsKey(entry.getKey())) {
                    newGroupComponentsList.add(entry.getValue());
                }
            }

            // add to components model
            components.put(undefinedGroup, newGroupComponentsList);
        }
    }

    public static ProjectProxy loadFromFile(File xmlFile) {
        Project parentProject = Project.loadFromFile(xmlFile);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setCoalescing(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);
            ProjectProxy result = new ProjectProxy(parentProject);
            Element xmlRoot = doc.getDocumentElement();
            Element ideXmlRoot = XmlUtils.getChildElement(xmlRoot, "ide");

            if (ideXmlRoot != null) {
                result.readConfiguration(ideXmlRoot);
            } else {
                result.createDefaultConfiguration();
                result.assignRestComponents();
            }
            return result;
        } catch (Exception var6) {
            throw new ConfigurationException("Loading of project configuration failed.", var6);
        }
    }

    // --------------------------------------------------------------------------------------
    // IDE CONFIGURATION WRITER
    // --------------------------------------------------------------------------------------

    public boolean saveToFile(File xmlFile) {
        parentProject.setPlatformName(properties.get("PlatformName"));
        try {
            parentProject.setWatchdogLevel(Integer.parseInt(properties.get("WatchdogLevel")));
        } catch (NumberFormatException e) {
            
        }
        parentProject.setEepromLayoutVersion(properties.get("EepromLayoutVersion"));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setCoalescing(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            // Write actual configuration to root node
            Element xmlRoot = parentProject.writeConfiguration(doc.createElement("project"));
            doc.appendChild(xmlRoot);

            Element xmlIdeRoot = writeIdeConfiguration(doc.createElement("ide"));
            if (xmlIdeRoot != null) {
                xmlRoot.appendChild(xmlIdeRoot);
            }

            // Save configuration to XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            return true;
        } catch (Exception e) {
            throw new ConfigurationException("Loading of project configuration failed.", e);
        }
    }

    private Element writeIdeConfiguration(Element xmlIdeElement) {
        Document doc = xmlIdeElement.getOwnerDocument();
        int output = 0;
        Element groupsElement = doc.createElement("groups");
        for (Map.Entry<Group, List<ComponentInterface>> entry : getComponents().entrySet()) {
            if (undefinedGroup.equals(entry.getKey()) || entry.getKey().projectComponentGroup) {
                continue;
            }
            Element groupElement = doc.createElement("group");
            groupElement.setAttribute("name", entry.getKey().name);
            groupElement.setAttribute("expanded", Boolean.toString(entry.getKey().expanded));
            for (ComponentInterface component : entry.getValue()) {
                Element componentElement = doc.createElement("component");
                componentElement.setTextContent(component.getName());
                groupElement.appendChild(componentElement);
            }
            groupsElement.appendChild(groupElement);
            output++;
        }
        xmlIdeElement.appendChild(groupsElement);
        return output == 0 ? null : xmlIdeElement;
    }

}
