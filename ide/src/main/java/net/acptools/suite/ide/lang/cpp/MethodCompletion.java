package net.acptools.suite.ide.lang.cpp;


import net.acptools.suite.ide.lang.cpp.core.Method;
import net.acptools.suite.ide.lang.cpp.core.Type;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;


/**
 * A completion for a Java method.  This completion gets its information from
 * one of two sources:
 *
 * <ul>
 * <li>A {@link Method} instance, which is created when parsing a Java
 * source file.  This is used when the completion represents a method
 * found in uncompiled source, such as the source in an
 * <tt>RSyntaxTextArea</tt>, or in a loose file on disk.</li>
 * </ul>
 */
class MethodCompletion extends FunctionCompletion implements MemberCompletion {


    private Method method;

    /**
     * Used to compare this method completion with another.
     */
    private String compareString;

    /**
     * The relevance of methods.  This allows methods to be "higher" in
     * the completion list than other types.
     */
    private static final int NON_CONSTRUCTOR_RELEVANCE = 2;


    /**
     * Creates a completion for a method discovered when parsing a Java
     * source file.
     *
     * @param provider
     * @param m        Meta data about the method.
     */
    public MethodCompletion(CompletionProvider provider, Method m) {

        // NOTE: "void" might not be right - I think this might be constructors
        super(provider, m.getName(), m.getReturnType() == null ? "void" : m.getReturnType().toString());

        this.method = m;
        setRelevanceAppropriately();

        int count = m.getParameterTypes().length;
        List<Parameter> params = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Type param = m.getParameterTypes()[i];
            params.add(new Parameter(param.getType().toString(), param.getName()));
        }
        setParams(params);

    }

    /**
     * Overridden to compare methods by their comparison strings.
     *
     * @param c2 A <code>Completion</code> to compare to.
     * @return The sort order.
     */
    @Override
    public int compareTo(Completion c2) {

        int rc = -1;

        if (c2 == this) {
            rc = 0;
        } else if (c2 instanceof MethodCompletion) {
            rc = getCompareString().compareTo(
                    ((MethodCompletion) c2).getCompareString());
        } else if (c2 != null) {
            rc = toString().compareToIgnoreCase(c2.toString());
            if (rc == 0) { // Same text value
                String clazz1 = getClass().getName();
                clazz1 = clazz1.substring(clazz1.lastIndexOf('.'));
                String clazz2 = c2.getClass().getName();
                clazz2 = clazz2.substring(clazz2.lastIndexOf('.'));
                rc = clazz1.compareTo(clazz2);
            }
        }

        return rc;

    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MethodCompletion) &&
                //((MethodCompletion)obj).getSignature().equals(getSignature());
                ((MethodCompletion) obj).getCompareString().equals(getCompareString());
    }


    @Override
    public String getAlreadyEntered(JTextComponent comp) {
        String temp = getProvider().getAlreadyEnteredText(comp);
        int lastDot = temp.lastIndexOf('.');
        if (lastDot > -1) {
            temp = temp.substring(lastDot + 1);
        }
        return temp;
    }


    /**
     * Returns a string used to compare this method completion to another.
     *
     * @return The comparison string.
     */
    private String getCompareString() {

        /*
         * This string compares the following parts of methods in this order,
         * to optimize sort order in completion lists.
         *
         * 1. First, by name
         * 2. Next, by number of parameters.
         * 3. Finally, by parameter type.
         */

        if (compareString == null) {
            StringBuilder sb = new StringBuilder(getName());
            // NOTE: This will fail if a method has > 99 parameters (!)
            int paramCount = getParamCount();
            if (paramCount < 10) {
                sb.append('0');
            }
            sb.append(paramCount);
            for (int i = 0; i < paramCount; i++) {
                String type = getParam(i).getType();
                sb.append(type);
                if (i < paramCount - 1) {
                    sb.append(',');
                }
            }
            compareString = sb.toString();
        }

        return compareString;

    }


    @Override
    public String getEnclosingClassName(boolean fullyQualified) {
        return method.getEnclosingClassName();
    }


    @Override
    public String getSignature() {
        return method.toString();
    }


    @Override
    public int hashCode() {
        return getCompareString().hashCode();
    }


    @Override
    public boolean isDeprecated() {
        return false;
    }


    /**
     * Sets the relevance of this constructor based on its properties.
     */
    private void setRelevanceAppropriately() {
        setRelevance(NON_CONSTRUCTOR_RELEVANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getSignature();
    }


}
