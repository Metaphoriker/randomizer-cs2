package dev.luzifer.updater;

import dev.luzifer.model.updater.Updater;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.File;
import java.io.IOException;

public class Main {
    
    private static final String LAUNCHER_VERSION_URL = "https://raw.githubusercontent.com/Luziferium/randomizer-csgo/stage/randomizer-frontend/randomizer-launcher/src/main/resources/version.txt";
    private static final String LAUNCHER_DOWNLOAD_URL = "https://github.com/Luziferium/randomizer-csgo/releases/download/latest/randomizer-launcher.jar";
    
    private static final String RANDOMIZER_VERSION_URL = "https://raw.githubusercontent.com/Luziferium/randomizer-csgo/master/randomizer-backend/randomizer-model/src/main/resources/version.txt";
    private static final String RANDOMIZER_DOWNLOAD_URL = "https://github.com/Luziferium/randomizer-csgo/releases/download/latest/randomizer.jar";
    private static final File RANDOMIZER_JAR = new File("randomizer.jar");
    
    public static void main(String[] args) throws IOException {
    
        if(args.length != 0) {
            for(int i = 0; i < args.length; i++) {
                if(args[i].startsWith("-launcherLocation=")) {
                    
                    File path = new File(args[i].substring(args[i].indexOf("=") + 1));
                    String pathName = path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf("\\"));
                    String fileName = path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("\\") + 1);
                    
                    File launcher = new File(pathName, fileName);
                    
                    Updater.update(launcher, LAUNCHER_VERSION_URL, LAUNCHER_DOWNLOAD_URL);
                    Runtime.getRuntime().exec("java -jar " + launcher.getAbsolutePath() + " -noupdate");
                    System.exit(0);
                }
                // TODO: parameter for randomizer stuff like -randomizerModule=module + refactor
            }
        }
        
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(200, 200);
        jFrame.setVisible(true);
    
        JLabel jLabel = new JLabel();
        jFrame.add(jLabel);
        
        jLabel.setText("Checking for updates...");
    
        Updater.update(RANDOMIZER_JAR, RANDOMIZER_VERSION_URL, RANDOMIZER_DOWNLOAD_URL);
        
        Runtime.getRuntime().exec("java -jar randomizer.jar");
        System.exit(0);
    }
    
}
