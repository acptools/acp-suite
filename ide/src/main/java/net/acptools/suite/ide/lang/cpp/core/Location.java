package net.acptools.suite.ide.lang.cpp.core;


import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

public class Location {

    private ComplexSymbolFactory.Location xleft;

    private ComplexSymbolFactory.Location xright;

    public Location(Symbol symbol) {
        if (symbol instanceof ComplexSymbolFactory.ComplexSymbol) {
            this.xleft = ((ComplexSymbolFactory.ComplexSymbol) symbol).xleft;
            this.xright = ((ComplexSymbolFactory.ComplexSymbol) symbol).xright;
        }
    }

    public ComplexSymbolFactory.Location getXleft() {
        return xleft;
    }

    public ComplexSymbolFactory.Location getXright() {
        return xright;
    }

    @Override
    public String toString() {
        return "Source location " + xleft + " - " + xright;
    }
}
