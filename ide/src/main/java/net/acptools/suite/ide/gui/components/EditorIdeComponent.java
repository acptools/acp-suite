package net.acptools.suite.ide.gui.components;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import net.acptools.suite.generator.models.components.Event;
import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.lang.LanguageSupport;
import net.acptools.suite.ide.lang.cpp.CppLanguageSupport;
import net.acptools.suite.ide.lang.cpp.core.Function;
import net.acptools.suite.ide.lang.cpp.util.SemanticAnalysis;
import net.acptools.suite.ide.utils.event.EventType;
import net.acptools.suite.ide.utils.event.Observer;
import net.acptools.suite.ide.lang.LanguageSupport;
import net.acptools.suite.ide.lang.cpp.CppLanguageSupport;
import net.acptools.suite.ide.lang.cpp.core.Function;
import net.acptools.suite.ide.lang.cpp.util.SemanticAnalysis;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.*;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.text.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorIdeComponent implements IdeComponent {
    private final EditorFrame editorFrame;

    protected RSyntaxTextArea textArea = null;
    protected RTextScrollPane sp = null;
    protected Parser parser = null;
    protected CppLanguageSupport ls = new CppLanguageSupport();

    public EditorIdeComponent(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
        InitializeComponents();

        textArea.setText(editorFrame.getIdeProject().getSourceString());

        this.editorFrame.getEventManager().registerObserver(EventType.COMPONENT_UPDATED, new Observer() {
            @Override
            public void onEvent(EventType eventType, Object o) {
                if (textArea != null && parser != null) {
                    textArea.forceReparsing(parser);
                }
            }
        });
        this.editorFrame.getEventManager().registerObserver(EventType.PROJECT_PRE_SAVE, new Observer() {
            @Override
            public void onEvent(EventType eventType, Object o) {
                editorFrame.getIdeProject().setSourceString(textArea.getText());
            }
        });
    }


    private void InitializeComponents() {
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        textArea.setCodeFoldingEnabled(true);
        ls.install(textArea);
        parser = ls.getParser(textArea);

        sp = new RTextScrollPane(textArea);
    }

    public JComponent render() {
        return sp;
    }

    @Override
    public SingleCDockable dockable() {
        return new DefaultSingleCDockable(getClass().toString(), "Editor", render());
    }

    public void createOrFindMethod(String functionName, List<Event.ParameterType> parameters) {
        if (SemanticAnalysis.getInstance().getFunctions().containsKey(functionName)) {
            Function function = SemanticAnalysis.getInstance().getFunctions().get(functionName);
            System.out.println("Funkcia existuje, oznacime ju v zdrojovom kode. " + function.toString());
        } else {
            List<String> params = new ArrayList<>(parameters.size());
            parameters.forEach(parameterType -> {
                params.add(parameterType.getType() + " " + parameterType.getName());
            });
            String newFunction = "\nvoid " + functionName + "(" + String.join(", ", params) + ") {\n\t// TODO: implement your action here\n}\n";
            textArea.append(newFunction);
            // TODO: oznacit pridany komentar v zdrojovom kode!
        }
    }

}
