package net.acptools.suite.ide.gui.components;

import bibliothek.gui.dock.common.SingleCDockable;

import javax.swing.*;

public interface IdeComponent {
    public JComponent render();

    public SingleCDockable dockable();
}
