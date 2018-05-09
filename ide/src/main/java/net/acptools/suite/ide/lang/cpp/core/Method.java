package net.acptools.suite.ide.lang.cpp.core;

import java.util.ArrayList;

public class Method extends Function {
    public Method(String name, ArrayList<Parameter> parameters) {
        super(name, parameters);
    }

    public Method(String name) {
        super(name, new ArrayList<>());
    }

    private String enclosingClassName;

    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    public void setEnclosingClassName(String enclosingClassName) {
        this.enclosingClassName = enclosingClassName;
    }

    public void addParameter(Parameter p) {
        functionParameter.add(p);
    }
}
