package de.metaphoriker.updater;

import de.metaphoriker.model.exception.UncaughtExceptionLogger;
import de.metaphoriker.model.updater.Updater;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;

public class Main {

  public static void main(String[] args) {

    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

    JFrame jFrame = new JFrame();
    jFrame.setSize(500, 200);
    jFrame.setUndecorated(true);
    jFrame.setLocationRelativeTo(null);
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jFrame.getContentPane().setBackground(new Color(34, 45, 50));

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(34, 45, 50));

    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setStringPainted(true);
    progressBar.setForeground(new Color(77, 182, 172));
    progressBar.setBackground(new Color(44, 62, 80));
    progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel jLabel = new JLabel("Checking for updates...", SwingConstants.CENTER);
    jLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    jLabel.setForeground(Color.WHITE);

    panel.add(jLabel, BorderLayout.NORTH);
    panel.add(progressBar, BorderLayout.CENTER);

    jFrame.add(panel);
    jFrame.setVisible(true);

    if (args.length != 0) {
      for (String arg : args) {
        if (arg.startsWith("-randomizerLocation=")) {
          File path = new File(arg.substring(arg.indexOf("=") + 1));
          String pathName =
              path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf("\\"));
          String fileName =
              path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("\\") + 1);

          File randomizer = new File(pathName, fileName);

          CompletableFuture.supplyAsync(
                  () -> Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL))
              .thenAccept(
                  isUpdateAvailable -> {
                    if (isUpdateAvailable) {
                      jLabel.setText("Update available. Updating...");
                      CompletableFuture.runAsync(
                              () -> {
                                Updater.update(
                                    randomizer,
                                    Updater.RANDOMIZER_DOWNLOAD_URL,
                                    (bytesRead, totalBytes) -> {
                                      double progress = (double) bytesRead / totalBytes * 100;
                                      SwingUtilities.invokeLater(
                                          () -> {
                                            progressBar.setValue((int) progress);
                                            progressBar.setString(
                                                "Downloading... "
                                                    + String.format("%.2f", progress)
                                                    + "%");
                                          });
                                    });
                              })
                          .thenRun(
                              () -> {
                                SwingUtilities.invokeLater(
                                    () -> {
                                      jLabel.setText("Update successful!");
                                      progressBar.setValue(100);
                                      progressBar.setString("Completed!");
                                      jLabel.setIcon(
                                          new ImageIcon(
                                              Main.class.getResource("images/checkmark_icon.png")));
                                    });
                                try {
                                  Runtime.getRuntime()
                                      .exec("java -jar " + randomizer.getAbsolutePath());
                                  Thread.sleep(2000);
                                  System.exit(0);
                                } catch (IOException | InterruptedException e) {
                                  e.printStackTrace();
                                }
                              });
                    } else {
                      SwingUtilities.invokeLater(() -> jLabel.setText("No update available!"));
                      progressBar.setValue(100);
                      progressBar.setString("Completed!");
                      jLabel.setIcon(
                          new ImageIcon(Main.class.getResource("images/checkmark_icon.png")));
                    }
                  });
          break;
        }
      }
    } else {
      jLabel.setText("No randomizer location specified! DONT OPEN THIS JAR DIRECTLY!");
    }
  }
}
