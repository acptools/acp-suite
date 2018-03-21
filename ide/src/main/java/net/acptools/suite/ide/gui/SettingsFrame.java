package net.acptools.suite.ide.gui;

import net.acptools.suite.ide.configurations.IdeSettings;
import net.acptools.suite.ide.gui.generated.SettingsFrameGenerated;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SettingsFrame extends SettingsFrameGenerated {

    public SettingsFrame() {
        setLayout(new BorderLayout());
        setTitle("Preferencies");
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        InitializeComponets();

        initFileSelector(
                arduinoCli,
                arduinoCliSelection,
                "Select Arduino CLI executable (ex. arduino_debug.exe)",
                JFileChooser.FILES_ONLY);
        initFileSelector(
                arduinoLibraryFolder,
                arduinoLibraryFolderSelection,
                "Select Arduino Library Folder executable (ex. arduino_debug.exe)",
                JFileChooser.DIRECTORIES_ONLY);
        initFileSelector(
                acprogModulesFolder,
                acprogModulesFolderSelection,
                "Select Acprog Modules Folder executable (ex. arduino_debug.exe)",
                JFileChooser.DIRECTORIES_ONLY);


        arduinoCli.setText(IdeSettings.getInstance().getArduinoCli());
        arduinoLibraryFolder.setText(IdeSettings.getInstance().getArduinoLibraryFolder());
        acprogModulesFolder.setText(IdeSettings.getInstance().getAcprogModulesFolder());

        cancelButton.addActionListener(e -> {
            SettingsFrame.this.dispose();
        });

        saveButton.addActionListener(e -> {
            IdeSettings.getInstance().setArduinoCli(arduinoCli.getText());
            IdeSettings.getInstance().setArduinoLibraryFolder(arduinoLibraryFolder.getText());
            IdeSettings.getInstance().setAcprogModulesFolder(acprogModulesFolder.getText());
            IdeSettings.getInstance().saveSettingsToFile();
            SettingsFrame.this.dispose();
        });
    }


    private void initFileSelector(JTextField jTextField, JButton jButton, String fileChooserTitle, int fileChooserMode) {
        jButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(fileChooserMode);
            fc.setDialogTitle(fileChooserTitle);
            fc.setSelectedFile(new File(jTextField.getText()));
            fc.addActionListener(e1 -> {
                if (fc.getSelectedFile() != null) {
                    jTextField.setText(fc.getSelectedFile().getAbsolutePath());
                }
            });
            fc.showOpenDialog(this);
        });
    }

}
