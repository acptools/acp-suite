package net.acptools.suite.ide.gui.components;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.generator.models.project.Component;
import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.models.ComponentProxy;
import net.acptools.suite.ide.models.ProjectProxy;
import net.acptools.suite.ide.utils.event.EventType;

import javax.swing.*;
import java.awt.*;

public class VisualEditorIdeComponent implements IdeComponent {
    private final EditorFrame editorFrame;

    protected VisualEditorJPanel scrollPane;

    class VisualEditorJPanel extends JScrollPane {

        JPanel panel;

        public VisualEditorJPanel() {
            super();
            panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(Color.WHITE);
            setViewportView(panel);
        }

        void add(ComponentProxy component) {
            ProjectComponent pc = new ProjectComponent(VisualEditorIdeComponent.this, component);
            panel.add(pc);
        }

    }

    public VisualEditorIdeComponent(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;

        InitializeComponents();

        // vlozenie komponentov do plochy
        ProjectProxy project = editorFrame.getIdeProject().getProject();
        /*for (Component component : project.getComponents()) {
            scrollPane.add(component);
        }*/
        editorFrame.getEventManager().registerObserver(EventType.COMPONENT_CREATE, this::componentCreateEvent);
    }

    public void componentCreateEvent(EventType eventType, Object o) {
        Module module = (Module) o;

        // vytvorenie komponentu
        Component component = new Component();
        component.setType(module.getName());
        component.setName(module.getName() + " 1");
        ComponentProxy myComponent = new ComponentProxy(component);
        /*myComponent.setLeft(0);
        myComponent.setTop(0);
        myComponent.setWidth(100);
        myComponent.setHeight(25);*/

        // vlozenie komponentu do projektu
        ProjectProxy project = editorFrame.getIdeProject().getProject();
        project.addComponent(myComponent, null);
        scrollPane.add(myComponent);
        scrollPane.updateUI();

        getEditorFrame().getEventManager().registerObserver(EventType.VISUAL_EDITOR_UPDATEUI, (eventType1, o1) -> scrollPane.updateUI());
    }

    private void InitializeComponents() {
        scrollPane = new VisualEditorJPanel();
    }

    public JComponent render() {
        return scrollPane;
    }

    @Override
    public SingleCDockable dockable() {
        return new DefaultSingleCDockable(getClass().toString(), "Visual editor", render());
    }

    public EditorFrame getEditorFrame() {
        return editorFrame;
    }
}
