package net.acptools.suite.ide.gui.components.visualgroupview;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconButton extends JButton {

    public static IconButton createButton(String iconFile) {
        IconButton ib = new IconButton();
        ClassLoader classLoader = ib.getClass().getClassLoader();
        URL url = classLoader.getResource(iconFile);
        if (url != null) {
            ib.setIcon(new ImageIcon(url));
        }
        ib.setMargin(new Insets(2, 2, 2, 2));
        return ib;
    }


    public static IconButton createEditButton() {
        return createButton("icons/edit.png");
    }

    public static IconButton createPlusButton() {
        return createButton("icons/plus.png");
    }

    public static IconButton createTrashButton() {
        return createButton("icons/trash.png");
    }
}
