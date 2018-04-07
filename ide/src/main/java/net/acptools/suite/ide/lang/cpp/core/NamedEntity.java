package net.acptools.suite.ide.lang.cpp.core;


import java_cup.runtime.Symbol;

/**
 * Entity that has a name
 */
public class NamedEntity extends SyntaxTreeNode {

    private String name;

    public NamedEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
