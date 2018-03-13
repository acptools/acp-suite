package net.acptools.suite.ide.lang.cpp.util;

public class SemanticException extends IllegalArgumentException {

    public SemanticException(String message) {
        super("Error at " + "\n" + message);
    }

}
