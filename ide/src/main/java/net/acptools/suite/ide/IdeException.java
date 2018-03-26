package net.acptools.suite.ide;

public class IdeException extends Exception {
    public IdeException(String message) {
        super(message);
    }

    public IdeException(String message, Exception ex) {
        super(message, ex);
    }
}
