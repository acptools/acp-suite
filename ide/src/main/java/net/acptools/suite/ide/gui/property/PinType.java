package net.acptools.suite.ide.gui.property;

import sk.gbox.swing.propertiespanel.types.IntegerType;

public class PinType extends IntegerType {
    public PinType() {
        super(1, 15, false);
    }
}
