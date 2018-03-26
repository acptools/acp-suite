package net.acptools.suite.ide.gui.components;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.gui.components.console.ConsoleInterface;
import net.acptools.suite.ide.utils.ProcessUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleIdeComponent implements IdeComponent, ConsoleInterface {
    private final EditorFrame editorFrame;
    private final int consoleMaxLength = 10000;

    private JScrollPane panel;
    private JTextPane textPane;


    public ConsoleIdeComponent(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
        InitializeComponents();
    }

    private void InitializeComponents() {

        DefaultStyledDocument document = new DefaultStyledDocument();
        textPane = new JTextPane(document);
        //textPane.setEditable(false);

        panel = new JScrollPane(textPane);
    }

    public JComponent render() {
        return panel;
    }

    @Override
    public SingleCDockable dockable() {
        return new DefaultSingleCDockable(getClass().toString(), "Console", render());
    }

    private synchronized void appendToPane(JTextPane tp, String msg, Color c) {
        tp.setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset;

        aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        if (len > consoleMaxLength) {
            try {
                tp.getDocument().remove(0, len - consoleMaxLength);
            } catch (BadLocationException e) {

            }
            len = tp.getDocument().getLength();
        }
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
        tp.setEditable(false);
    }

    @Override
    public void print(String line) {
        appendToPane(textPane, line, Color.BLACK);
    }

    @Override
    public void println(String line) {
        appendToPane(textPane, line + "\n", Color.BLACK);
    }

    @Override
    public void err(String line) {
        appendToPane(textPane, line, Color.RED);
    }

    @Override
    public void errln(String line) {
        appendToPane(textPane, line + "\n", Color.RED);
    }

    @Override
    public void exception(Throwable e) {
        errln(e.getMessage());
        e.printStackTrace();
    }

    @Override
    public int runProccess(String proccess) {
        appendToPane(textPane, "> " + proccess + "\n", Color.GRAY);
        return ProcessUtils.run(proccess, (process) -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader inputErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            new Thread(() -> {
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                String line;
                try {
                    while ((line = inputErr.readLine()) != null) {
                        errln(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}
