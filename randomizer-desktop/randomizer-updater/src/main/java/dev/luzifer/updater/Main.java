package dev.luzifer.updater;

import dev.luzifer.model.updater.Updater;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
    
    private static final File RANDOMIZER_JAR = new File("randomizer.jar");
    
    public static void main(String[] args) throws IOException {
    
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(200, 200);
        jFrame.setVisible(true);
    
        JLabel jLabel = new JLabel();
        jFrame.add(jLabel);
    
        jLabel.setText("Checking for updates...");
    
        int result = JOptionPane.showConfirmDialog(null, "Do you want to update..?", "Update", JOptionPane.YES_NO_OPTION);
        if(result == 0) {
            
            if(Updater.isUpdateAvailable(RANDOMIZER_JAR, Updater.RANDOMIZER_VERSION_URL)) {
                jLabel.setText("Updating...");
                Updater.update(RANDOMIZER_JAR, Updater.RANDOMIZER_DOWNLOAD_URL);
                jLabel.setText("Update successful!");
            } else {
                jLabel.setText("No update available!");
            }
            Runtime.getRuntime().exec("java -jar randomizer.jar");
        }
    }
    
}
