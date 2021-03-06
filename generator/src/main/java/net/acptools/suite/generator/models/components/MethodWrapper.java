package net.acptools.suite.generator.models.components;

import java.util.*;

import org.w3c.dom.Element;

import net.acptools.suite.generator.utils.XmlUtils;

/**
 * Description a method wrapper.
 */
public class MethodWrapper {

    // ---------------------------------------------------------------------------
    // Parameter type
    // ---------------------------------------------------------------------------

    /**
     * Description of a parameter of a method and also a function that wraps the
     * method.
     */
    public static class ParameterType {
        /**
         * Type of parameter (int, long, etc.)
         */
        private final String type;

        /**
         * Suggested name for the parameter.
         */
        private final String name;

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        /**
         * Constructs new parameter description of a method wrapper.
         *
         * @param type the type of parameter
         * @param name the suggester name of parameter
         */
        public ParameterType(String type, String name) {
            if ((type == null) || (type.trim().isEmpty())) {
                throw new ConfigurationException("Parameter type cannot by empty.");
            }

            this.type = type;
            this.name = name;
        }
    }

    // ---------------------------------------------------------------------------
    // Instance variables
    // ---------------------------------------------------------------------------

    /**
     * Method invoked by the wrapping function.
     */
    private String wrappedMethod;

    /**
     * Name of autogenerated property name that will hold the name of generated
     * function.
     */
    private String autogeneratedPropertyName;

    /**
     * Binding type.
     */
    private Binding binding;

    /**
     * Type of result returned by method and also the wrapping function.
     */
    private String resultType;

    /**
     * Ordered list of parameters of the wrapping function.
     */
    private final List<ParameterType> parameters = new ArrayList<ParameterType>();

    // ---------------------------------------------------------------------------
    // Setters and getters
    // ---------------------------------------------------------------------------

    public Binding getBinding() {
        return binding;
    }

    public String getResultType() {
        return resultType;
    }

    public List<ParameterType> getParameters() {
        return parameters;
    }

    public String getWrappedMethod() {
        return wrappedMethod;
    }

    public String getAutogeneratedPropertyName() {
        return autogeneratedPropertyName;
    }

    // ---------------------------------------------------------------------------
    // XML parsing
    // ---------------------------------------------------------------------------

    /**
     * Reads method wrapper description from an xml element.
     *
     * @param xmlElement the xml element.
     */
    public void readFromXml(Element xmlElement) {
        wrappedMethod = XmlUtils.getSimplePropertyValue(xmlElement, "method", "").trim();
        autogeneratedPropertyName = xmlElement.getAttribute("autogenerated-property").trim();

        binding = null;
        Element xmlBinding = XmlUtils.getChildElement(xmlElement, "binding");
        if (xmlBinding != null) {
            binding = new Binding();
            try {
                binding.readFromXml(xmlBinding);
            } catch (ConfigurationException e) {
                throw new ConfigurationException("Binding of method wrapper for method " + wrappedMethod + " contains errors.", e);
            }
        }

        resultType = null;
        Element xmlResultType = XmlUtils.getChildElement(xmlElement, "result");
        if (xmlResultType != null) {
            resultType = xmlResultType.getTextContent().trim();
        }

        for (Element xmlParameter : XmlUtils.getChildElements(xmlElement, "parameter")) {
            String parameterType = xmlParameter.getTextContent().trim();
            if (parameterType.isEmpty()) {
                throw new ConfigurationException("Empty parameter type in method wrapper for method " + wrappedMethod
                        + ".");
            }

            parameters.add(new ParameterType(parameterType, xmlParameter.getAttribute("name").trim()));
        }
    }

    // ---------------------------------------------------------------------------
    // Generators
    // ---------------------------------------------------------------------------

    /**
     * Generates header of the wrapping function.
     *
     * @param functionName           the name of function.
     * @param generateParameterNames true, if the names of parameters are generated, false
     *                               otherwise.
     * @return the function header.
     */
    public String generateWrappingFunctionHeader(String functionName, boolean generateParameterNames) {
        StringBuilder sb = new StringBuilder();
        if ((resultType == null) || (resultType.isEmpty())) {
            sb.append("void");
        } else {
            sb.append(resultType.trim());
        }

        sb.append(" ");
        sb.append(functionName);
        sb.append("(");
        boolean first = true;
        for (ParameterType parameter : parameters) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }

            sb.append(parameter.getType().trim());
            if (generateParameterNames) {
                sb.append(" " + parameter.getName());
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Generates invocation commands with given argument names.
     *
     * @param invocated method or function to be invoked.
     * @return the command for invocation.
     */
    public String generateInvocation(String invocated) {
        StringBuilder sb = new StringBuilder();
        sb.append(invocated);
        sb.append("(");
        boolean first = true;
        for (ParameterType parameter : parameters) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }

            sb.append(parameter.getName().trim());
        }
        sb.append(")");
        return sb.toString();
    }
}
