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

    JFrame mainFrame = createMainFrame();
    JLabel statusLabel = createStatusLabel();
    JProgressBar progressBar = createProgressBar();
    setupUI(mainFrame, statusLabel, progressBar);

    if (args.length != 0) {
      for (String arg : args) {
        if (arg.startsWith("-randomizerLocation=")) {
          File randomizer = extractFilePath(arg);
          checkAndUpdate(randomizer, statusLabel, progressBar);
          break;
        }
      }
    } else {
      statusLabel.setText("No randomizer location specified! DONT OPEN THIS JAR DIRECTLY!");
    }
  }

  private static JFrame createMainFrame() {
    JFrame frame = new JFrame();
    frame.setSize(500, 200);
    frame.setUndecorated(true);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setBackground(new Color(34, 45, 50));
    return frame;
  }

  private static JLabel createStatusLabel() {
    JLabel label = new JLabel("Checking for updates...", SwingConstants.CENTER);
    label.setFont(new Font("Arial", Font.PLAIN, 18));
    label.setForeground(Color.WHITE);
    return label;
  }

  private static JProgressBar createProgressBar() {
    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setStringPainted(true);
    progressBar.setForeground(new Color(77, 182, 172));
    progressBar.setBackground(new Color(44, 62, 80));
    progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    return progressBar;
  }

  private static void setupUI(JFrame frame, JLabel label, JProgressBar progressBar) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(34, 45, 50));
    panel.add(label, BorderLayout.NORTH);
    panel.add(progressBar, BorderLayout.CENTER);
    frame.add(panel);
    frame.setVisible(true);
  }

  private static File extractFilePath(String arg) {
    File path = new File(arg.substring(arg.indexOf("=") + 1));
    String pathName = path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf("\\"));
    String fileName =
        path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("\\") + 1);
    return new File(pathName, fileName);
  }

  private static void checkAndUpdate(File randomizer, JLabel label, JProgressBar progressBar) {
    CompletableFuture.supplyAsync(
            () -> Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL))
        .thenAccept(
            isUpdateAvailable -> {
              if (isUpdateAvailable) {
                label.setText("Update available. Updating...");
                CompletableFuture.runAsync(
                        () ->
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
                                }))
                    .thenRun(
                        () -> {
                          SwingUtilities.invokeLater(
                              () -> {
                                label.setText("Update successful!");
                                progressBar.setValue(100);
                                progressBar.setString("Completed!");
                                label.setIcon(
                                    new ImageIcon(
                                        Main.class.getResource("images/checkmark_icon.png")));
                              });
                          try {
                            Runtime.getRuntime().exec("java -jar " + randomizer.getAbsolutePath());
                            Thread.sleep(2000);
                            System.exit(0);
                          } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                          }
                        });
              } else {
                SwingUtilities.invokeLater(() -> label.setText("No update available!"));
                progressBar.setValue(100);
                progressBar.setString("Completed!");
                label.setIcon(new ImageIcon(Main.class.getResource("images/checkmark_icon.png")));
              }
            });
  }
}
