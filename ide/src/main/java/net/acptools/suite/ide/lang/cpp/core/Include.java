package net.acptools.suite.ide.lang.cpp.core;

import java_cup.runtime.Symbol;

public class Include extends SyntaxTreeNode {
    private String fileName;

    private Location location;

    public Include(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLocation(Symbol symbol) {
        this.location = new Location(symbol);
    }
}
