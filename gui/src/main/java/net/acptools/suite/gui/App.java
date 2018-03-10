package net.acptools.suite.gui;

import java.awt.EventQueue;
import javax.swing.UIManager;

public class App {

    public static void main(String[] args) {
        // Start gui
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MainFrame frame = new MainFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
