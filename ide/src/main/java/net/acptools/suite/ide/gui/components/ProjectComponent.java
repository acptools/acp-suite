package net.acptools.suite.ide.gui.components;

import net.acptools.suite.ide.models.ComponentProxy;
import net.acptools.suite.ide.utils.event.EventType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ProjectComponent extends JButton implements MouseMotionListener, ActionListener {

    protected ComponentProxy projectComponent;
    protected VisualEditorIdeComponent visualEditorIdeComponent;

    /**
     * If sets <b>TRUE</b> this component is draggable
     */
    private boolean draggable = true;

    /**
     * 2D Point representing the coordinate where mouse is, relative parent container
     */
    protected Point anchorPoint;

    /**
     * Default mouse cursor for dragging action
     */
    protected Cursor draggingCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    /**
     * If sets <b>TRUE</b> when dragging component,
     * it will be painted over each other (z-Buffer change)
     */
    protected boolean overbearing = false;

    public ProjectComponent(VisualEditorIdeComponent visualEditorIdeComponent, ComponentProxy projectComponent) {
        super();

        this.visualEditorIdeComponent = visualEditorIdeComponent;
        this.projectComponent = projectComponent;

        updateUI();

        addMouseMotionListener(this);

        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        visualEditorIdeComponent.getEditorFrame().getEventManager().callEvent(EventType.COMPONENT_SELECTED, (Object) projectComponent);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        anchorPoint = e.getPoint();
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int anchorX = anchorPoint.x;
        int anchorY = anchorPoint.y;
        java.awt.Component parent = getParent();

        Point parentOnScreen = getParent().getLocationOnScreen();
        Point mouseOnScreen = e.getLocationOnScreen();
        Point position = new Point(mouseOnScreen.x - parentOnScreen.x - anchorX, mouseOnScreen.y - parentOnScreen.y - anchorY);
        position.x = Math.max(position.x, 0);
        position.y = Math.max(position.y, 0);
        //projectComponent.setLeft(position.x);
        //projectComponent.setTop(position.y);
        setLocation(position);
        Dimension minSize = parent.getMinimumSize();
        int width = Math.max(minSize.width,
                position.x + getBounds().width);
        int height = Math.max(minSize.height,
                position.y + getBounds().height);
        parent.setPreferredSize(new Dimension(width, height));
        parent.revalidate();
    }

    @Override
    public void updateUI() {
        if (projectComponent != null) {
            setText(projectComponent.getName());
            //setBounds(projectComponent.getLeft(), projectComponent.getTop(), projectComponent.getWidth(), projectComponent.getHeight());
        }
        super.updateUI();
    }
}
