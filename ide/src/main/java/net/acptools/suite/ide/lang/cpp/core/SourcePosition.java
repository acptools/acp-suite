package net.acptools.suite.ide.lang.cpp.core;

public class SourcePosition {
    private long left;
    private long right;

    public SourcePosition(long left, long right) {
        this.left = left;
        this.right = right;
    }

    public long getLeft() {
        return left;
    }

    public long getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "SourcePosition{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
