package net.acptools.suite.ide.gui.components;

import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.ide.utils.event.EventType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBoxComponent implements ActionListener {
    private final ToolBoxIdeComponent toolBoxIdeComponent;
    private final Module module;

    public ToolBoxComponent(ToolBoxIdeComponent toolBoxIdeComponent, Module module) {
        this.toolBoxIdeComponent = toolBoxIdeComponent;
        this.module = module;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ToolBoxComponent source = (ToolBoxComponent) e.getSource();
        Module module = source.getModule();
        toolBoxIdeComponent.getEditorFrame().getEventManager().callEvent(EventType.COMPONENT_CREATE, module);
    }

    public Module getModule() {
        return module;
    }

    @Override
    public String toString() {
        return module.getShortName();
    }
}
