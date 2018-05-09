package net.acptools.suite.ide.lang.cpp.core;
/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a <code>ClassFile</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ClassFile {

    private String name;

    /**
     * Structures giving complete descriptions of the fields in this class
     * or interface.
     */
    private List<Variable> fields = new ArrayList<>();

    /**
     * Structures giving complete descriptions of the methods in this class or
     * interface.
     */
    private List<Method> methods = new ArrayList<>();

    public ClassFile(String name) {
        this.name = name;
    }

    public ClassFile(String name, List<Variable> fiels, List<Method> methods) {
        this.name = name;
        this.fields = fiels;
        this.methods = methods;
    }

    public List<Variable> getFields() {
        return fields;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setFields(List<Variable> fields) {
        this.fields = fields;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMethod(Method m) {
        methods.add(m);
    }

    public void addField(Variable m) {
        fields.add(m);
    }
}