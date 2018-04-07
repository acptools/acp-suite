package net.acptools.suite.ide.lang.cpp.core;

import java_cup.runtime.ComplexSymbolFactory.Location;

abstract public class SyntaxTreeNode {
    private Location left;
    private Location right;

    public Location getLeft() {
        return left;
    }

    public void setLeft(Location left) {
        this.left = left;
    }

    public Location getRight() {
        return right;
    }

    public void setRight(Location right) {
        this.right = right;
    }
}
