package net.acptools.suite.ide.gui.components.console;

public interface ConsoleInterface {
    public void print(String s);

    public void println(String s);

    public void err(String message);

    public void errln(String message);

    int runProccess(String proccess);
}
