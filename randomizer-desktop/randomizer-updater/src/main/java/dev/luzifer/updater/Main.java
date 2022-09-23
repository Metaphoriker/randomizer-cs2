package dev.luzifer.updater;

import dev.luzifer.model.updater.Updater;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
    
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(500, 200);
        jFrame.setVisible(true);
    
        JLabel jLabel = new JLabel();
        jFrame.add(jLabel);
    
        jLabel.setText("Checking for updates...");
    
        if(args.length != 0) {
            for (String arg : args) {
                if (arg.startsWith("-randomizerLocation=")) {
                
                    File path = new File(arg.substring(arg.indexOf("=") + 1));
                    String pathName = path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf("\\"));
                    String fileName = path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("\\") + 1);
                
                    File randomizer = new File(pathName, fileName);
                
                    if(Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL)) {
                        jLabel.setText("Updating...");
                        Updater.update(randomizer, Updater.RANDOMIZER_DOWNLOAD_URL);
                        jLabel.setText("Update successful!");
                    } else {
                        jLabel.setText("No update available!");
                    }
                    Runtime.getRuntime().exec("java -jar " + randomizer.getAbsolutePath());
                    System.exit(0);
                    return;
                }
            }
        } else {
            jLabel.setText("No randomizer location specified! DONT OPEN THIS JAR DIRECTLY!");
        }
    }
    
}
