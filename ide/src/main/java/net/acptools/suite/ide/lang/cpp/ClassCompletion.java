package net.acptools.suite.ide.lang.cpp;


import net.acptools.suite.ide.lang.cpp.core.ClassFile;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


class ClassCompletion extends AbstractCppSourceCompletion {

    private ClassFile cf;


    public ClassCompletion(CompletionProvider provider, ClassFile cf) {
        super(provider, cf.getName());
        this.cf = cf;
    }


    /*
     * Fixed error when comparing classes of the same name, which did not allow
     * classes with same name but different packages.
     * Thanks to Guilherme Joao Frantz and Jonatas Schuler for the patch!
     */
    @Override
    public int compareTo(Completion c2) {

        if (c2 == this) {
            return 0;
        }
        // Check for classes with same name, but in different packages
        else if (c2.toString().equalsIgnoreCase(toString())) {
            if (c2 instanceof ClassCompletion) {
                ClassCompletion cc2 = (ClassCompletion) c2;
                return getClassName(true).compareTo(cc2.getClassName(true));
            }
        }
        return super.compareTo(c2);
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ClassCompletion) &&
                ((ClassCompletion) obj).getReplacementText().equals(getReplacementText());
    }


    /**
     * Returns the name of the class represented by this completion.
     *
     * @param fullyQualified Whether the returned name should be fully
     *                       qualified.
     * @return The class name.
     */
    public String getClassName(boolean fullyQualified) {
        return cf.getName();
    }


    @Override
    public String getSummary() {

        // Default to the fully-qualified class name.
        return "Class auto completion: " + cf.getName();

    }


    @Override
    public String getToolTipText() {
        return "class " + getReplacementText();
    }


    @Override
    public int hashCode() {
        return getReplacementText().hashCode();
    }

}
