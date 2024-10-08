package de.metaphoriker.updater;

import de.metaphoriker.model.exception.UncaughtExceptionLogger;
import de.metaphoriker.model.updater.Updater;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class Main {

  public static void main(String[] args) throws IOException {

    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

    JFrame jFrame = new JFrame("Updater");
    jFrame.setSize(500, 100);
    jFrame.setLocationRelativeTo(null);
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jFrame.setVisible(true);

    JLabel jLabel = new JLabel();
    jLabel.setIcon(new ImageIcon(Main.class.getResource("images/loading_gif.gif")));
    jFrame.add(jLabel);

    jLabel.setText("Checking for updates...");

    if (args.length != 0) {
      for (String arg : args) {
        if (arg.startsWith("-randomizerLocation=")) {

          File path = new File(arg.substring(arg.indexOf("=") + 1));
          String pathName =
              path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf("\\"));
          String fileName =
              path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("\\") + 1);

          File randomizer = new File(pathName, fileName);

          if (Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL)) {
            jLabel.setText("Updating...");
            Updater.update(randomizer, Updater.RANDOMIZER_DOWNLOAD_URL);
            jLabel.setText("Update successful!");
          } else {
            jLabel.setText("No update available!");
          }
          jLabel.setIcon(new ImageIcon(Main.class.getResource("images/checkmark_icon.png")));
          Runtime.getRuntime().exec("java -jar " + randomizer.getAbsolutePath());
          break;
        }
      }
    } else {
      jLabel.setText("No randomizer location specified! DONT OPEN THIS JAR DIRECTLY!");
    }
    System.exit(0);
  }
}
