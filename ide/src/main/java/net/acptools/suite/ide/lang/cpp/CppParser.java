package net.acptools.suite.ide.lang.cpp;


import java_cup.runtime.ComplexSymbolFactory;
import net.acptools.suite.generator.Platform;
import net.acptools.suite.generator.models.components.Event;
import net.acptools.suite.generator.models.components.PropertyType;
import net.acptools.suite.generator.models.modules.ComponentType;
import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.generator.models.project.Component;
import net.acptools.suite.ide.lang.cpp.core.Function;
import net.acptools.suite.ide.lang.cpp.core.Type;
import net.acptools.suite.ide.lang.cpp.generated.Lexer;
import net.acptools.suite.ide.lang.cpp.generated.Parser;
import net.acptools.suite.ide.lang.cpp.util.SemanticAnalysis;
import net.acptools.suite.ide.models.ComponentProxy;
import net.acptools.suite.ide.models.IdeProject;
import net.acptools.suite.ide.models.ProjectProxy;
import org.fife.io.DocumentReader;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CppParser extends AbstractParser {

    /**
     * The property change event that's fired when the document is re-parsed.
     * Applications can listen for this property change and update themselves
     * accordingly.
     */
    public static final String PROPERTY_COMPILATION_UNIT = "CompilationUnit";

    // private CompilationUnit cu;
    private PropertyChangeSupport support;
    private DefaultParseResult result;

    /**
     * Constructor.
     */
    public CppParser(RSyntaxTextArea textArea) {
        support = new PropertyChangeSupport(this);
        result = new DefaultParseResult(this);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        support.addPropertyChangeListener(prop, l);
    }

    /**
     * Returns the compilation unit from the last time the text area was parsed.
     *
     * @return The compilation unit, or <code>null</code> if it hasn't yet been
     *         parsed or an unexpected error occurred while parsing.
     */
    /*
     * public CompilationUnit getCompilationUnit() { return cu; }
     */

    /*
     * public int getOffset(RSyntaxDocument doc, ParserNotice notice) { Element root
     * = doc.getDefaultRootElement(); Element elem =
     * root.getElement(notice.getLine()); int offs = elem.getStartOffset() +
     * notice.getColumn(); return offs>=elem.getEndOffset() ? -1 : offs; }
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        try {
            SemanticAnalysis.reset();
            this.result.clearNotices();
            SemanticAnalysis.setParser(this, doc);
            ComplexSymbolFactory csf = new ComplexSymbolFactory();
            DocumentReader r = new DocumentReader(doc);
            Lexer scanner = new Lexer(new BufferedReader(r), csf);

            Parser parser = new Parser(scanner, csf);
            ComplexSymbolFactory.ComplexSymbol s = (ComplexSymbolFactory.ComplexSymbol) parser.parse();
        } catch (Exception e) {
            System.err.println("Failed to compile source code, due to this error:");
            e.printStackTrace();
        }

        validateSources(doc);

        return this.result;
    }

    private void validateSources(RSyntaxDocument doc) {

        if (IdeProject.getInstance() == null) {
            return;
        }
        ProjectProxy projectProxy = IdeProject.getInstance().getProject();


        String projectInclude = IdeProject.getInstance().getName() + ".h";
        if (!SemanticAnalysis.getInstance().hasInclude(projectInclude)) {
            DefaultParserNotice pn = new DefaultParserNotice(this, "Missing required import #include \"" + projectInclude + "\"", 0);
            pn.setLevel(ParserNotice.Level.ERROR);
            addNotice(pn);
        }

        Platform platform = Platform.loadPlatform(projectProxy.getPlatformName());
        boolean[] digitalPins = new boolean[platform.getNumberOfDigitalPins()];
        boolean[] analogPins = new boolean[platform.getNumberOfAnalogInputPins()];


        Map<String, ComponentProxy> components = projectProxy.getComponentsMap();
        for (Map.Entry<String, ComponentProxy> entry : components.entrySet()) {
            ComponentProxy componentProxy = entry.getValue();
            Map<String, String> properties = componentProxy.getProperties();
            Map<String, String> events = componentProxy.getEvents();
            Module module = componentProxy.getModuleInstance();
            if (!(module instanceof ComponentType)) {
                return;
            }
            ComponentType componentType = (ComponentType) module;

            // Validate properties
            for (Map.Entry<String, PropertyType> entry1 : componentType.getProperties().entrySet()) {
                PropertyType pt = entry1.getValue();
                if (properties.getOrDefault(entry1.getKey(), null) != null) {
                    if ("pin".equals(pt.getType())) {
                        Integer pinNumber = new Integer(properties.get(entry1.getKey()));
                        if (pinNumber <= platform.getNumberOfDigitalPins() && !digitalPins[pinNumber]) {
                            digitalPins[pinNumber] = true;
                        } else {
                            DefaultParserNotice pn = new DefaultParserNotice(this, "Repeatly using pin number \"" + pinNumber + "\" on component \"" + componentProxy.getName() + "\"", 0);
                            pn.setLevel(ParserNotice.Level.ERROR);
                            addNotice(pn);
                        }
                    }
                    if ("analog-pin".equals(pt.getType())) {
                        Integer pinNumber = new Integer(properties.get(entry1.getKey()));
                        if (pinNumber <= platform.getNumberOfAnalogInputPins() && !analogPins[pinNumber]) {
                            analogPins[pinNumber] = true;
                        } else {
                            DefaultParserNotice pn = new DefaultParserNotice(this, "Repeatly using analog pin number \"" + pinNumber + "\" on component \"" + componentProxy.getName() + "\"", 0);
                            pn.setLevel(ParserNotice.Level.ERROR);
                            addNotice(pn);
                        }
                    }
                }
            }

            // Validate events
            Map<String, Function> allFunctionsMap = SemanticAnalysis.getInstance().getFunctions();
            for (Map.Entry<String, Event> entry1 : componentType.getEvents().entrySet()) {
                Event event = entry1.getValue();
                if (events.getOrDefault(entry1.getKey(), null) != null) {
                    String eventMethod = events.get(entry1.getKey());
                    if (!allFunctionsMap.containsKey(eventMethod)) {
                        DefaultParserNotice pn = new DefaultParserNotice(this, "Event method \"" + eventMethod + "\" does not exists in source code, error on component \"" + componentProxy.getName() + "\"", 0);
                        pn.setLevel(ParserNotice.Level.ERROR);
                        addNotice(pn);
                    } else {
                        Function function = allFunctionsMap.get(eventMethod);
                        Type[] functionParameterTypes = function.getParameterTypes();
                        List<Event.ParameterType> expectedParameters = event.getParameters();
                        boolean correct = true;
                        if (functionParameterTypes.length == expectedParameters.size()) {
                            for (int i = 0; i < functionParameterTypes.length; i++) {
                                if (!functionParameterTypes[i].getName().equals(expectedParameters.get(i).getType())) {
                                    correct = false;
                                    break;
                                }
                            }
                        } else {
                            correct = false;
                        }

                        if (!correct) {
                            String[] params = new String[expectedParameters.size()];
                            for (int i = 0; i < expectedParameters.size(); i++) {
                                params[i] = expectedParameters.get(i).getType() + " " + expectedParameters.get(i).getName();
                            }
                            String[] found = new String[functionParameterTypes.length];
                            for (int i = 0; i < functionParameterTypes.length; i++) {
                                found[i] = functionParameterTypes[i].getName();
                            }
                            DefaultParserNotice pn = new DefaultParserNotice(this, "Event method \"" + eventMethod + "\" must have these parameters (" + Arrays.toString(params) + "), found (" + Arrays.toString(found) + ")", 0);
                            pn.setLevel(ParserNotice.Level.ERROR);
                            addNotice(pn);
                        }
                    }
                }

            }
        }
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        support.removePropertyChangeListener(prop, l);
    }

    public void addNotice(DefaultParserNotice pn) {
        System.err.println(pn.getMessage() + " " + pn.getLine() + " " + pn.getOffset() + " " + pn.getLength());
        this.result.addNotice(pn);
    }
}
