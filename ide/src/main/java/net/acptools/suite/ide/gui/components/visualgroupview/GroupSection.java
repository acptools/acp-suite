package net.acptools.suite.ide.gui.components.visualgroupview;

import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.project.ComponentInterface;
import net.acptools.suite.ide.project.ComponentProxy;
import net.acptools.suite.ide.project.ProjectProxy;
import net.acptools.suite.ide.utils.event.EventType;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

/**
 * Panel that contains both the title/header part and the content part.
 *
 * @author oliver
 */

public class GroupSection<G, I> extends JPanel {

    private final int minComponentHeight = 30;
    private final int minComponentWidth = 300;
    private final int minItemWidth = 130;
    private final int minItemHeight = 30;

    private JComponent titlePanel;

    private ArrowPanel arrowIconPanel;

    private JList<I> contentList;

    private boolean expanded;

    private GroupView parent;

    private G group;

    private List<I> model;

    public GroupSection(GroupView parent, G group, List<I> model) {
        this.parent = parent;
        this.group = group;
        this.model = model;

        InitializeComponents();
        populateModelData();

        EditorFrame.instance.getEventManager().registerObserver(EventType.PROJECT_CHANGED, (eventType, o) -> setExpanded(isExpanded()));
    }

    private void populateModelData() {
        contentList.setModel(new ListModel<I>() {
            @Override
            public int getSize() {
                return model.size();
            }

            @Override
            public I getElementAt(int index) {
                try {
                    return model.get(index);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }

            @Override
            public void addListDataListener(ListDataListener l) {
                //System.out.println("add " + l.toString());
            }

            @Override
            public void removeListDataListener(ListDataListener l) {
                //System.out.println("remove " + l.toString());
            }
        });
    }

    private JLabel titleComponent;


    public void InitializeComponents() {
        // absolute layout
        setLayout(new BorderLayout());

        // region Hlavička sekcie
        titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titlePanel.setPreferredSize(new Dimension(this.getPreferredSize().width, minComponentHeight));
        titlePanel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setExpanded(!isExpanded());
            }
        });

        arrowIconPanel = new ArrowPanel(BasicArrowButton.EAST);
        arrowIconPanel.setPreferredSize(new Dimension(40, 40));
        titlePanel.add(arrowIconPanel, BorderLayout.WEST);

        // region Ikony
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JButton addButtonPanel = IconButton.createPlusButton();
        addButtonPanel.setPreferredSize(new Dimension(40, 40));
        iconsPanel.add(addButtonPanel);
        addButtonPanel.addActionListener(e -> {
            EditorFrame.instance.getEventManager().callEvent(EventType.GROUP_CREATE);
        });

        JButton editButtonPanel = IconButton.createEditButton();
        editButtonPanel.setPreferredSize(new Dimension(40, 40));
        iconsPanel.add(editButtonPanel);
        editButtonPanel.addActionListener(e -> {
            EditorFrame.instance.getEventManager().callEvent(EventType.GROUP_EDIT, (Object) getGroup());
        });

        JButton deleteButtonPanel = IconButton.createTrashButton();
        deleteButtonPanel.setPreferredSize(new Dimension(40, 40));
        iconsPanel.add(deleteButtonPanel);
        deleteButtonPanel.addActionListener(e -> {
            EditorFrame.instance.getEventManager().callEvent(EventType.GROUP_REMOVE, (Object) getGroup());
        });

        // endregion


        titlePanel.add(iconsPanel, BorderLayout.EAST);

        titleComponent = new JLabel(group.toString());
        titleComponent.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), titleComponent.getBorder()));
        titlePanel.add(titleComponent);

        titlePanel.setTransferHandler(new ListTransferHandler());

        add(titlePanel, BorderLayout.NORTH);
        // endregion

        // region Komponent sekcie
        contentList = new JList<I>();
        contentList.setDragEnabled(true);
        contentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentList.addListSelectionListener(e -> {
            if (contentList.getSelectedValue() != null) {
                EditorFrame.instance.getEventManager().callEvent(EventType.COMPONENT_SELECTED, contentList.getSelectedValue());
                parent.clearSelection(GroupSection.this);
            }
        });
        contentList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    EditorFrame.instance.getEventManager().callEvent(EventType.COMPONENT_DELETE, contentList.getSelectedValue());
                    EditorFrame.instance.getEventManager().callEvent(EventType.COMPONENT_SELECTED, null);
                }
            }
        });
        contentList.setTransferHandler(new ListTransferHandler());
        contentList.setCellRenderer(new SectionComponentCellRenderer());
        add(contentList, BorderLayout.CENTER);
        // endregion

        styleSection();
    }

    public void styleSection() {
        titlePanel.setBackground(Color.LIGHT_GRAY);
        contentList.setBackground(Color.WHITE);
        arrowIconPanel.setTheme(Color.LIGHT_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        contentList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentList.setFixedCellWidth(minItemWidth);
        contentList.setFixedCellHeight(minItemHeight);
        contentList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        contentList.setVisibleRowCount(-1);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(minComponentWidth, minComponentHeight);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;

        arrowIconPanel.changeDirection(expanded ? BasicArrowButton.SOUTH : BasicArrowButton.EAST);
        arrowIconPanel.updateUI();

        contentList.setVisible(expanded);
        contentList.revalidate();
        contentList.updateUI();

        if (expanded) {
            setMaximumSize(new Dimension(Integer.MAX_VALUE, titlePanel.getPreferredSize().height + contentList.getPreferredSize().height));
        } else {
            setMaximumSize(new Dimension(Integer.MAX_VALUE, titlePanel.getPreferredSize().height));
        }

        revalidate();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void clearSelection() {
        contentList.clearSelection();
    }

    public G getGroup() {
        return group;
    }

    public void touch() {
        titleComponent.setText(group.toString() + " (" + model.size() + ")");
    }

    protected class SectionComponentCellRenderer extends DefaultListCellRenderer {

        public java.awt.Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            ComponentInterface component = (ComponentInterface) value;

            setText(component.getName());
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            return this;
        }
    }

    public class ListTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            return (support.getComponent() instanceof JPanel) && support.isDataFlavorSupported(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
        }

        @Override
        public boolean importData(TransferSupport support) {
            boolean accept = false;
            if (canImport(support)) {
                try {
                    Transferable t = support.getTransferable();
                    Object value = t.getTransferData(ListItemTransferable.LIST_ITEM_DATA_FLAVOR);
                    if (value instanceof ComponentProxy) {
                        java.awt.Component component = support.getComponent();
                        if (component instanceof JPanel) {
                            EditorFrame.instance.getIdeProject().getProject().moveComponent((ComponentProxy) value, (ProjectProxy.Group) group);
                            EditorFrame.instance.getEventManager().callEvent(EventType.PROJECT_CHANGED);
                            accept = true;
                        }
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            return accept;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return DnDConstants.ACTION_COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            Transferable t = null;
            if (c instanceof JList) {
                @SuppressWarnings("unchecked")
                JList<ComponentProxy> list = (JList<ComponentProxy>) c;
                Object value = list.getSelectedValue();
                if (value instanceof ComponentProxy) {
                    ComponentProxy li = (ComponentProxy) value;
                    t = new ListItemTransferable(li);
                }
            }
            return t;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            //System.out.println("ExportDone");
            // Here you need to decide how to handle the completion of the transfer,
            // should you remove the item from the list or not...
        }
    }

    public static class ListItemTransferable implements Transferable {

        public static final DataFlavor LIST_ITEM_DATA_FLAVOR = new DataFlavor(ComponentProxy.class, "java/ListItem");
        private ComponentProxy listItem;

        public ListItemTransferable(ComponentProxy listItem) {
            this.listItem = listItem;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{LIST_ITEM_DATA_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(LIST_ITEM_DATA_FLAVOR);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

            return listItem;

        }
    }
}