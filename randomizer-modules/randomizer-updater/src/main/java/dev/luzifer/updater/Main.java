package dev.luzifer.updater;

import dev.luzifer.model.updater.Updater;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.File;
import java.io.IOException;

public class Main {
    
    private static final String RANDOMIZER_VERSION_URL = "https://raw.githubusercontent.com/Luziferium/randomizer-csgo/stage/randomizer-model/src/main/resources/version.txt";
    private static final String DOWNLOAD_URL = "https://github.com/Luziferium/randomizer-csgo/releases/download/latest/randomizer.jar";
    private static final File RANDOMIZER_JAR = new File("randomizer.jar");
    
    public static void main(String[] args) throws IOException {
    
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(200, 200);
        jFrame.setVisible(true);
    
        JLabel jLabel = new JLabel();
        jFrame.add(jLabel);
        
        jLabel.setText("Checking for updates...");
    
        Updater.update(RANDOMIZER_JAR, RANDOMIZER_VERSION_URL, DOWNLOAD_URL);
        Runtime.getRuntime().exec("java -jar randomizer.jar");
        System.exit(0);
    }
    
}
