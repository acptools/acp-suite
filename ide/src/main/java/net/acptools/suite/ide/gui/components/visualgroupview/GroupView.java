package net.acptools.suite.ide.gui.components.visualgroupview;

import net.acptools.suite.ide.gui.EditorFrame;
import net.acptools.suite.ide.utils.event.EventType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupView<G, I> extends JPanel {

    Map<G, List<I>> model;

    private Map<G, GroupSection<G, I>> sections = new LinkedHashMap<>();

    public GroupView() {

        setBackground(Color.WHITE);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setFocusable(false);

        revalidate();

        EditorFrame.instance.getEventManager().registerObserver(EventType.PROJECT_CHANGED, (eventType, o) -> {
            populateModelData();
        });
    }

    public void addSection(GroupSection<G, I> newSection, boolean expanded) {
        if (sections.containsKey(newSection.getGroup())) {
            throw new IllegalArgumentException("Sekcia už bola vložená skôr.");
        }
        sections.put(newSection.getGroup(), newSection);
        add(newSection);
        newSection.setExpanded(expanded);
    }

    public void touchSection(GroupSection<G, I> newSection) {
        newSection.touch();
    }

    public void setModel(Map<G, List<I>> model) {
        this.model = model;
        populateModelData();
    }

    public void populateModelData() {
        List<Map.Entry<G, GroupSection<G, I>>> toRemove = new ArrayList<>();
        // vymazanie sekcii
        for (Map.Entry<G, GroupSection<G, I>> entry : sections.entrySet()) {
            if (!model.containsKey(entry.getKey())) {
                toRemove.add(entry);
            }
        }
        for (Map.Entry<G, GroupSection<G, I>> entry : toRemove) {
            remove(entry.getValue());
            sections.remove(entry.getKey());
        }

        // vytvorenie sekcii
        for (Map.Entry<G, List<I>> entry : model.entrySet()) {
            if (!sections.containsKey(entry.getKey())) {
                GroupSection<G, I> section = new GroupSection<G, I>(this, entry.getKey(), entry.getValue());
                addSection(section, true);
                touchSection(section);
            } else {
                GroupSection<G, I> section = sections.get(entry.getKey());
                touchSection(section);
            }

        }

        // nastaenie velkosti
        revalidate();
    }

    public void clearSelection(Component exceptSection) {
        for (Component components : getComponents()) {
            GroupSection<G, I> groupSection = (GroupSection<G, I>) components;
            if (groupSection != exceptSection) {
                groupSection.clearSelection();
            }
        }
    }


}
