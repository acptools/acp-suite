package net.acptools.suite.ide.lang.cpp;

import org.fife.ui.autocomplete.Completion;


/**
 * Interface for Java source code completions.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface CppSourceCompletion extends Completion {


    /**
     * Force subclasses to override equals().
     * TODO: Remove me
     */
    @Override
    public boolean equals(Object obj);


}
