package net.acptools.suite.ide.gui;

import net.acptools.suite.ide.App;
import net.acptools.suite.ide.IdeException;
import net.acptools.suite.ide.configurations.IdeSettingsProject;
import net.acptools.suite.ide.gui.generated.OpenFrameGenerated;
import net.acptools.suite.ide.gui.generated.OpenFrameGenerated;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class OpenFrame extends OpenFrameGenerated {

    public OpenFrame() {
        setLayout(new BorderLayout());
        setTitle("Open project");
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        InitializeComponets();

        newProjectLocationSelection.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.addActionListener(e1 -> {
                File selection = fc.getSelectedFile();
                if (selection != null && selection.isDirectory()) {
                    newProjectLocation.setText(selection.getAbsolutePath());
                }
            });
            fc.setDialogTitle("Select where project folder should be located");
            fc.showOpenDialog(OpenFrame.this);
        });
        newProjectButton.addActionListener(e -> {
            createProject(new File(newProjectLocation.getText()), newProjectName.getText());
        });

        openProjectLocationSelection.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.addActionListener(e1 -> {
                File selection = fc.getSelectedFile();
                if (selection != null && selection.isDirectory()) {
                    openProjectLocation.setText(selection.getAbsolutePath());
                }
            });
            fc.setDialogTitle("Select where your project folder is located");
            fc.showOpenDialog(OpenFrame.this);
        });
        openProjectButton.addActionListener(e -> {
            openProject(new File(openProjectLocation.getText()));
        });
    }

    private void createProject(File path, String name) {
        try {
            IdeSettingsProject project = IdeSettingsProject.createNewProject(path, name);
            OpenFrame.this.dispose();
            App.openProject(project);
        } catch (IdeException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acprog error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openProject(File projectPath) {
        try {
            IdeSettingsProject project = IdeSettingsProject.fromFile(projectPath);
            OpenFrame.this.dispose();
            App.openProject(project);
        } catch (IdeException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acprog error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
