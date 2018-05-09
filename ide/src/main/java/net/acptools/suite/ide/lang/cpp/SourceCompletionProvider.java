package net.acptools.suite.ide.lang.cpp;
/**
 * Parses a Java AST for code completions.  It currently scans the following:
 *
 * <ul>
 * <li>Import statements
 * <li>Method names
 * <li>Field names
 * </ul>
 * <p>
 * Also, if the caret is inside a method, local variables up to the caret
 * position are also returned.
 */

import net.acptools.suite.ide.lang.cpp.core.ClassFile;
import net.acptools.suite.ide.lang.cpp.core.Method;
import net.acptools.suite.ide.lang.cpp.core.Variable;
import net.acptools.suite.ide.lang.cpp.util.SemanticAnalysis;
import net.acptools.suite.ide.models.ComponentProxy;
import net.acptools.suite.ide.models.IdeProject;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class SourceCompletionProvider extends DefaultCompletionProvider {


    /**
     * The parent completion provider.
     */
    private CppCompletionProvider cppCompletion;

    private static final String THIS = "this";

    /**
     * Constructor.
     */
    public SourceCompletionProvider() {
        setParameterizedCompletionParams('(', ", ", ')');
        setAutoActivationRules(false, "."); // Default - only activate after '.'
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
        getCompletionsImpl(tc); // Force loading of completions
        return super.getCompletionsAt(tc, p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Completion> getCompletionsImpl(JTextComponent comp) {

        comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {

            completions = new ArrayList<Completion>();//completions.clear();

            SemanticAnalysis sa = SemanticAnalysis.getInstance();
            if (sa == null) {
                return completions; // empty
            }

            Set<Completion> set = new TreeSet<Completion>();

            // Cut down the list to just those matching what we've typed.
            // Note: getAlreadyEnteredText() never returns null
            String text = getAlreadyEnteredText(comp);


            // Don't add shorthand completions if they're typing something
            // qualified
            if (text.indexOf('.') == -1) {
                addShorthandCompletions(set);
                addSemanticAnalysisData(set, sa);
            } else {
                loadMethodCompletions(set, text);
            }


            // Do a final sort of all of our completions and we're good to go!
            completions = new ArrayList<Completion>(set);
            Collections.sort(completions);


            if (text.indexOf('.') == -1) {
                // Append other cpp completions
                loadCodeCompletionsFromXml();
            }

            // Only match based on stuff after the final '.', since that's what is
            // displayed for all of our completions.
            text = text.substring(text.lastIndexOf('.') + 1);

            @SuppressWarnings("unchecked")
            int start = Collections.binarySearch(completions, text, comparator);
            if (start < 0) {
                start = -(start + 1);
            } else {
                // There might be multiple entries with the same input text.
                while (start > 0 &&
                        comparator.compare(completions.get(start - 1), text) == 0) {
                    start--;
                }
            }

            @SuppressWarnings("unchecked")
            int end = Collections.binarySearch(completions, text + '{', comparator);
            end = -(end + 1);

            return completions.subList(start, end);

        } finally {
            comp.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }

    }

    private void addSemanticAnalysisData(Set<Completion> set, SemanticAnalysis sa) {

        Map<String, ComponentProxy> componentsMap = IdeProject.getInstance().getProject().getComponentsMap();

        for (Map.Entry<String, ComponentProxy> entry : componentsMap.entrySet()) {
            set.add(new VariableCompletion(this, entry.getKey(), entry.getValue().getType()));
        }

        Map<String, ClassFile> classes = sa.getClasses();
        for (Map.Entry<String, ClassFile> entry : classes.entrySet()) {
            set.add(new ClassCompletion(this, entry.getValue()));
        }

    }

    private void loadCodeCompletionsFromXml() {
        // First try loading resource (running from demo jar), then try
        // accessing file (debugging in Eclipse).
        String res = "data/c.xml";
        if (res != null) { // Subclasses may specify a null value
            InputStream in = getClass().getResourceAsStream(res);
            try {
                if (in != null) {
                    loadFromXML(in);
                    in.close();
                } else {
                    loadFromXML(new File(res));
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void loadMethodCompletions(Set<Completion> set, String text) {
        text = text.substring(0, text.lastIndexOf('.'));

        ClassFile cf = SemanticAnalysis.getInstance().getClassForIdentifier(text);

        if (cf != null) {
            List<Method> methods = cf.getMethods();
            for (Method m : methods) {
                set.add(new MethodCompletion(this, m));
            }
            List<Variable> fields = cf.getFields();
            for (Variable v : fields) {
                set.add(new VariableCompletion(this, v.getName(), v.getType().toString()));
            }
        }
    }

    private void addShorthandCompletions(Set<Completion> set) {
        set.add(new ShorthandCompletion(this, "trace", "Serial.println(F(\"\"));"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValidChar(char ch) {
        return Character.isJavaIdentifierPart(ch) || ch == '.';
    }

}
