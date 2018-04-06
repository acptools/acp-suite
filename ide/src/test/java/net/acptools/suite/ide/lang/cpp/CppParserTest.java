package net.acptools.suite.ide.lang.cpp;

import net.acptools.suite.ide.lang.LanguageSupport;
import net.acptools.suite.ide.lang.cpp.util.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Test;

import javax.swing.text.Document;
import java.io.*;

public class CppParserTest {


    private String readFile(String file) throws IOException {
        InputStream is = getClass().getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    @Test
    public void testSimpleDocument() throws IOException {
        RSyntaxTextArea textArea = new RSyntaxTextArea();

        CppParser parser = new CppParser(textArea);
        textArea.putClientProperty(LanguageSupport.PROPERTY_LANGUAGE_PARSER, parser);
        textArea.addParser(parser);


        Logger.enableLogger();
        textArea.setText(readFile("test01.ino"));
        textArea.forceReparsing(parser);


    }
}
