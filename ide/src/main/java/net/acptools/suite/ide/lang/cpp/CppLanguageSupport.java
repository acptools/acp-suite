package net.acptools.suite.ide.lang.cpp;

import net.acptools.suite.ide.lang.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

public class CppLanguageSupport extends AbstractLanguageSupport {

    /**
     * The completion provider, shared amongst all text areas editing C.
     */
    private CppCompletionProvider provider;


    /**
     * Constructor.
     */
    public CppLanguageSupport() {
        setParameterAssistanceEnabled(true);
        setShowDescWindow(true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected ListCellRenderer createDefaultCompletionCellRenderer() {
        return new CppCellRenderer();
    }


    private CppCompletionProvider getProvider() {
        if (provider == null) {
            provider = new CppCompletionProvider();
        }
        return provider;
    }

    public CppParser getParser(RSyntaxTextArea textArea) {
        // Could be a parser for another language.
        Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
        if (parser instanceof CppParser) {
            return (CppParser) parser;
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void install(RSyntaxTextArea textArea) {

        CppCompletionProvider provider = getProvider();
        AutoCompletion ac = createAutoCompletion(provider);
        ac.install(textArea);
        installImpl(textArea, ac);

        CppParser parser = new CppParser(textArea);
        textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
        textArea.addParser(parser);
        textArea.setToolTipSupplier(provider);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstall(RSyntaxTextArea textArea) {
        uninstallImpl(textArea);
        textArea.setToolTipSupplier(null);
    }


}