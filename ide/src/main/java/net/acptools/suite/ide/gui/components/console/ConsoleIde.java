package net.acptools.suite.ide.gui.components.console;

import net.acptools.suite.ide.gui.EditorFrame;

public class ConsoleIde implements ConsoleInterface {

    public static final ConsoleIde instance = new ConsoleIde();

    private ConsoleIde() {
    }

    @Override
    public void print(String s) {
        if (EditorFrame.instance != null && EditorFrame.instance.console != null) {
            EditorFrame.instance.console.print(s);
        } else {
            System.out.print(s);
        }
    }

    @Override
    public void println(String s) {
        if (EditorFrame.instance != null && EditorFrame.instance.console != null) {
            EditorFrame.instance.console.println(s);
        } else {
            System.out.println(s);
        }
    }

    @Override
    public void err(String s) {
        if (EditorFrame.instance != null && EditorFrame.instance.console != null) {
            EditorFrame.instance.console.err(s);
        } else {
            System.err.print(s);
        }
    }

    @Override
    public void errln(String s) {
        if (EditorFrame.instance != null && EditorFrame.instance.console != null) {
            EditorFrame.instance.console.errln(s);
        } else {
            System.err.println(s);
        }
    }

    @Override
    public int runProccess(String proccess) {
        return EditorFrame.instance.console.runProccess(proccess);
    }
}
