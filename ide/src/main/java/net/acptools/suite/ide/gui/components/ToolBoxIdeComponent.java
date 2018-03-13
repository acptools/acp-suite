package net.acptools.suite.ide.gui.components;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import net.acptools.suite.generator.models.modules.Module;
import net.acptools.suite.ide.configurations.IdeSettings;
import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.utils.ACPModules;
import net.acptools.suite.ide.utils.event.EventType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ToolBoxIdeComponent implements IdeComponent, MouseListener, KeyListener {
    private final EditorFrame editorFrame;

    protected JTree tree;
    protected JScrollPane scrollPane;

    public ToolBoxIdeComponent(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
        InitializeComponents();
    }

    private void createNodes(DefaultMutableTreeNode top) {
        // Načítame všetky dostupné typy komponentov
        File acpModulesDirectory = new File(IdeSettings.getInstance().getAcprogModulesFolder());
        ACPModules acpModules = new ACPModules(acpModulesDirectory);
        Collection<Module> allModules = acpModules.scanDirectory();

        // Inicializácia premenných
        Map<String, DefaultMutableTreeNode> treeMap = new HashMap<>();
        DefaultMutableTreeNode parent;
        DefaultMutableTreeNode node;

        // Prechod cez všetky dostupné moduly
        for (Module module : allModules) {
            String name = module.getName();

            // Rozbijeme si názov modulu cez bodky a vytvoríme stromovú štruktúru
            String[] path = name.split("\\.");
            if (path.length == 0) {
                path = new String[]{name};
            }

            // Generovanie podpriečinkov
            String fullPath = "";
            parent = top;
            for (int i = 0; i < path.length - 1; i++) {
                fullPath = fullPath + "." + path[i];

                // Overenie existencie podstromu
                if (treeMap.containsKey(fullPath)) {
                    node = treeMap.get(fullPath);
                } else {
                    node = new DefaultMutableTreeNode(path[i]);
                    parent.add(node);
                }

                // Nastavenie nového rodiča
                treeMap.put(fullPath, node);
                parent = node;
            }

            // Vloženie typu komponentu pod rodica
            node = new DefaultMutableTreeNode(new ToolBoxComponent(this, module));
            parent.add(node);
        }
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void InitializeComponents() {
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode("Nástroje");
        createNodes(top);
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(this);
        tree.addKeyListener(this);
        expandAllNodes(tree, 0, tree.getRowCount());

        scrollPane = new JScrollPane(tree);
        scrollPane.getVerticalScrollBar().setUnitIncrement(100);
    }

    public JComponent render() {
        return scrollPane;
    }

    @Override
    public SingleCDockable dockable() {
        return new DefaultSingleCDockable(getClass().toString(), "Toolbox", render());
    }

    public EditorFrame getEditorFrame() {
        return editorFrame;
    }

    public void addSelectedComponent() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (node == null)
            return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            ToolBoxComponent toolBoxComponent = (ToolBoxComponent) nodeInfo;
            getEditorFrame().getEventManager().callEvent(EventType.COMPONENT_CREATE, toolBoxComponent.getModule());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            addSelectedComponent();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            addSelectedComponent();
        }
    }
}
