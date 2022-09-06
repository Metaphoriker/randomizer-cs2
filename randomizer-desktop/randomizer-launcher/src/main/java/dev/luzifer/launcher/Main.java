package dev.luzifer.launcher;

import dev.luzifer.model.updater.Updater;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

public class Main {
    
    private static final String UPDATER_VERSION_URL = "https://raw.githubusercontent.com/Luziferium/randomizer-csgo/stage/randomizer-frontend/randomizer-updater/src/main/resources/version.txt";
    private static final String UPDATER_DOWNLOAD_URL = "https://github.com/Luziferium/randomizer-csgo/releases/download/latest/randomizer-updater.jar";
    
    public static void main(String[] args) throws IOException, URISyntaxException {
        
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(200, 200);
        jFrame.setVisible(true);
    
        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon(Main.class.getResource("/images/downloading.gif")));
        jFrame.add(jLabel);
    
        jLabel.setText("Launcher representation...");
        
        // TODO: since this is only initial, we have to check for updates manually in here, before restarting the launcher to avoid a restart loop
    
        int result = JOptionPane.showConfirmDialog(null, "Do you want to install the updater? (Recommended)", "Install", JOptionPane.YES_NO_OPTION);
        if(result == 0) {
            if(!Collections.unmodifiableList(Arrays.asList(args)).contains("-noupdate")) {
                
                File appdataFolder = setupAppdataFolder();
                installUpdater(appdataFolder);
                // TODO: check if update is needed first
                startUpdater(appdataFolder);
            }
        }
    }
    
    private static File setupAppdataFolder() {
        
        File appdataFolder = new File(System.getenv("APPDATA") + "\\randomizer");
        appdataFolder.mkdirs();
        
        return appdataFolder;
    }
    
    private static File installUpdater(File folderInto) {
        
        File updaterJar = new File(folderInto, "randomizer-updater.jar");
        try {
            updaterJar.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Updater.update(updaterJar, UPDATER_VERSION_URL, UPDATER_DOWNLOAD_URL);
        return updaterJar;
    }
    
    private static void startUpdater(File appdataFolder) throws IOException, URISyntaxException {
        Runtime.getRuntime().exec("java -jar " + appdataFolder.getAbsolutePath() + "/randomizer-updater.jar" +
                " -launcherLocation=" + Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        System.exit(0);
    }
}
