package net.acptools.suite.ide.gui.utils;

import net.acptools.suite.ide.gui.EditorFrame;

public class ConsoleIde implements ConsoleInterface {

    public static final ConsoleIde instance = new ConsoleIde();

    private ConsoleIde() {
    }

    @Override
    public void print(String s) {
        EditorFrame.instance.console.print(s);
    }

    @Override
    public void println(String s) {
        EditorFrame.instance.console.println(s);
    }

    @Override
    public void err(String s) {
        EditorFrame.instance.console.err(s);
    }

    @Override
    public void errln(String s) {
        EditorFrame.instance.console.errln(s);
    }

    @Override
    public int runProccess(String proccess) {
        return EditorFrame.instance.console.runProccess(proccess);
    }
}
