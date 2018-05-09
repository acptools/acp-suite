package net.acptools.suite.ide.models;

import net.acptools.suite.generator.models.components.ConfigurationException;
import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.generator.utils.XmlUtils;
import net.acptools.suite.ide.lang.cpp.core.ClassFile;
import net.acptools.suite.ide.lang.cpp.core.Method;
import net.acptools.suite.ide.lang.cpp.core.Type;
import net.acptools.suite.ide.lang.cpp.core.Variable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class ModuleProxy {

    private Module parentModule = null;

    public Module getModule() {
        return parentModule;
    }

    public String getName() {
        return getModule().getName();
    }

    public void setName(String name) {
        getModule().setName(name);
    }

    // ---------------------------------------------------------------------------
    // XML parsing and validation
    // ---------------------------------------------------------------------------

    /**
     * Loads a module configuration from an xml file.
     *
     * @param xmlFile the xml file with description of a module.
     * @return the constructed module description.
     * @throws ConfigurationException if loading of module description failed.
     */
    public static ModuleProxy loadFromFile(File xmlFile) throws ConfigurationException {

        ModuleProxy result = new ModuleProxy();
        result.parentModule = Module.loadFromFile(xmlFile);
        if (result.parentModule == null) {
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setCoalescing(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            Element xmlRoot = doc.getDocumentElement();

            Element autocompleteRoot = XmlUtils.getChildElement(xmlRoot, "autocomplete");
            if (autocompleteRoot != null) {
                result.readAutocompleteConfiguration(autocompleteRoot);
            }

            return result;
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Loading of description of a module from file " + xmlFile.getAbsolutePath() + " failed.", e);
        }
    }

    private ClassFile classFile;

    /**
     * Reads module description common for all module types from an xml element.
     *
     * @param xmlModule the xml element with description of a module.
     * @throws ConfigurationException if a module misconfiguration is detected.
     */
    private void readAutocompleteConfiguration(Element xmlModule) throws ConfigurationException {

        classFile = new ClassFile(getModule().getName());

        List<Element> methods = XmlUtils.getChildElements(xmlModule, "method");
        for (Element methodElement : methods) {
            Element nameElement = XmlUtils.getChildElement(methodElement, "name");
            if (nameElement == null) {
                throw new ConfigurationException("Method must have name element!");
            }
            Method m = new Method(XmlUtils.getElementValue(nameElement, null));
            Element propertiesElement = XmlUtils.getChildElement(methodElement, "properties");
            if (propertiesElement != null) {
                List<Element> propertyElements = XmlUtils.getChildElements(propertiesElement, "property");
                int i = 1;
                for (Element propertyElement : propertyElements) {
                    Type type = new Type(XmlUtils.getSimpleAttributeValue(propertyElement, "type", "void"));
                    String name = XmlUtils.getElementValue(propertyElement, "prop" + i);
                    m.addParameter(new Variable(name, type));
                    i++;
                }
            }

            classFile.addMethod(m);

        }
    }

    public ClassFile getClassFile() {
        return classFile;
    }
}
