package net.acptools.suite.ide.lang.cpp;


import java_cup.runtime.ComplexSymbolFactory;
import net.acptools.suite.ide.lang.cpp.generated.Lexer;
import net.acptools.suite.ide.lang.cpp.generated.Parser;
import net.acptools.suite.ide.lang.cpp.util.SemanticAnalysis;
import net.acptools.suite.ide.lang.cpp.generated.Parser;
import net.acptools.suite.ide.lang.cpp.util.SemanticAnalysis;
import org.fife.io.DocumentReader;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;

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

    /**
     * Adds all notices from the Java parser to the results object.
     */
    private void addNotices(RSyntaxDocument doc) {

        /*
         * result.clearNotices(); int count = cu==null ? 0 : cu.getParserNoticeCount();
         *
         * if (count==0) { return; }
         *
         * for (int i=0; i<count; i++) { ParserNotice notice = cu.getParserNotice(i);
         * int offs = getOffset(doc, notice); if (offs>-1) { int len =
         * notice.getLength(); result.addNotice(new DefaultParserNotice(this,
         * notice.getMessage(), notice.getLine(), offs, len)); } }
         */

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
            ComplexSymbolFactory csf = new ComplexSymbolFactory();
            DocumentReader r = new DocumentReader(doc);
            Lexer scanner = new Lexer(new BufferedReader(r), csf);

            Parser parser = new Parser(scanner, csf);
            ComplexSymbolFactory.ComplexSymbol s = (ComplexSymbolFactory.ComplexSymbol) parser.parse();
        } catch (Exception e) {
            System.err.println("Failed to compile: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        support.removePropertyChangeListener(prop, l);
    }
}
