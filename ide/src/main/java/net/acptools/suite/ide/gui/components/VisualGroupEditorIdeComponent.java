package net.acptools.suite.ide.gui.components;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import net.acptools.suite.ide.models.IdeProject;
import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.gui.components.visualgroupview.GroupView;
import net.acptools.suite.ide.models.ComponentInterface;
import net.acptools.suite.ide.models.ProjectProxy;
import net.acptools.suite.ide.utils.event.EventManager;
import net.acptools.suite.ide.utils.event.EventType;

import javax.swing.*;
import java.awt.*;

public class VisualGroupEditorIdeComponent implements IdeComponent {
    private static EventManager eventManager = EditorFrame.instance.getEventManager();
    private final EditorFrame editorFrame;

    protected JComponent renderView;

    public VisualGroupEditorIdeComponent(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;

        InitializeComponents();

        eventManager.registerObserver(EventType.GROUP_CREATE, ((eventType, o) -> {
            String newName = JOptionPane.showInputDialog(EditorFrame.instance, "Put name of group", "Create new group", JOptionPane.PLAIN_MESSAGE);
            if (newName != null) {
                IdeProject.getInstance().getProject().createAndGetGroup(new ProjectProxy.Group(newName));

                groupView.setModel(IdeProject.getInstance().getProject().getComponents());
                eventManager.callEvent(EventType.PROJECT_CHANGED);
            }
        }));

        eventManager.registerObserver(EventType.GROUP_EDIT, ((eventType, o) -> {
            ProjectProxy.Group group = (ProjectProxy.Group) o;
            String newName = JOptionPane.showInputDialog(EditorFrame.instance, "Put new name for group " + group.name, "Edit group", JOptionPane.PLAIN_MESSAGE);
            if (newName != null) {
                ProjectProxy.Group newGroup = new ProjectProxy.Group(newName);
                IdeProject.getInstance().getProject().createAndGetGroup(newGroup);
                IdeProject.getInstance().getProject().removeGroup(group, newGroup);

                System.out.println("to be edited");
                groupView.setModel(IdeProject.getInstance().getProject().getComponents());
                eventManager.callEvent(EventType.PROJECT_CHANGED);
            }
        }));

        eventManager.registerObserver(EventType.GROUP_REMOVE, ((eventType, o) -> {
            ProjectProxy.Group group = (ProjectProxy.Group) o;
            if (JOptionPane.showConfirmDialog(EditorFrame.instance,
                    "Are you sure?\nAll components assigned in this group will be moved to undefined group!",
                    "Delete group",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                IdeProject.getInstance().getProject().removeGroup(group);

                groupView.setModel(IdeProject.getInstance().getProject().getComponents());
                eventManager.callEvent(EventType.PROJECT_CHANGED);
            }
        }));
    }

    private GroupView<ProjectProxy.Group, ComponentInterface> groupView;

    private void InitializeComponents() {
        groupView = new GroupView<>();

        groupView.setModel(IdeProject.getInstance().getProject().getComponents());

        renderView = new JScrollPane(groupView);
        ((JScrollPane) renderView).getVerticalScrollBar().setUnitIncrement(100);
        renderView.setBackground(Color.WHITE);
    }

    public JComponent render() {
        return renderView;
    }

    @Override
    public SingleCDockable dockable() {
        return new DefaultSingleCDockable(getClass().toString(), "Project components editor", render());
    }

    public EditorFrame getEditorFrame() {
        return editorFrame;
    }


}
