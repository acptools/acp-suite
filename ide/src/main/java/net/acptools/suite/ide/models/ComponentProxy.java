package net.acptools.suite.ide.models;

import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.generator.models.project.Component;
import net.acptools.suite.generator.utils.XmlUtils;
import net.acptools.suite.ide.utils.ACPModules;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public class ComponentProxy implements ComponentInterface {
    private Component parentComponent;

    public ComponentProxy(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.parentComponent.getProperties();
    }

    @Override
    public Map<String, String> getEvents() {
        return this.parentComponent.getEvents();
    }

    public Map<String, String> getAutogeneratedProperties() {
        return this.parentComponent.getAutogeneratedProperties();
    }

    @Override
    public String getName() {
        return this.parentComponent.getName();
    }

    @Override
    public void setName(String name) {
        this.parentComponent.setName(name);
    }

    @Override
    public String getType() {
        return this.parentComponent.getType();
    }

    @Override
    public void setType(String type) {
        this.parentComponent.setType(type);
    }

    @Override
    public String getDescription() {
        return parentComponent.getDescription();
    }

    @Override
    public void setDescription(String description) {
        parentComponent.setDescription(description);
    }

    @Override
    public Module getModuleInstance() {
        return ACPModulesRepository.INSTANCE.getModule(this);
    }

    public void readFromXml(Element xmlElement) {
        for (Element element : XmlUtils.getChildElements(xmlElement, "property")) {
            String name = XmlUtils.getSimpleAttributeValue(element, "key", null);
        }
    }

    public void saveToXml(Document doc, Element xmlComponent) {
        parentComponent.writeToXml(xmlComponent);
    }

    public Element writeIdeConfiguration(Element xmlGroup) {
        Element el;
        Document doc = xmlGroup.getOwnerDocument();
        xmlGroup.setAttribute("name", getName());
        xmlGroup.setAttribute("type", "component");
        return xmlGroup;
    }

    public Component getParentComponent() {
        return parentComponent;
    }
}
