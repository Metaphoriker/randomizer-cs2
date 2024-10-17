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
    JLabel versionComparisonLabel = createVersionComparisonLabel();
    JLabel updatingLabel = createUpdatingLabel();
    setupUI(mainFrame, statusLabel, progressBar, versionComparisonLabel, updatingLabel);

    if (args.length != 0) {
      for (String arg : args) {
        if (arg.startsWith("-randomizerLocation=")) {
          File randomizer = extractFilePath(arg);
          checkAndUpdate(
              randomizer, statusLabel, progressBar, versionComparisonLabel, updatingLabel);
          break;
        }
      }
    } else {
      statusLabel.setText("No randomizer location specified! DO NOT OPEN THIS JAR DIRECTLY!");
    }
  }

  private static JFrame createMainFrame() {
    JFrame frame = new JFrame();
    frame.setSize(450, 200);
    frame.setTitle("Randomizer Updater");
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setBackground(new Color(34, 45, 50));
    return frame;
  }

  private static JLabel createStatusLabel() {
    JLabel label = new JLabel("Checking for updates...", SwingConstants.CENTER);
    label.setFont(new Font("Arial", Font.PLAIN, 14));
    label.setForeground(Color.WHITE);
    return label;
  }

  private static JProgressBar createProgressBar() {
    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setStringPainted(true);
    progressBar.setForeground(new Color(77, 182, 172));
    progressBar.setBackground(new Color(44, 62, 80));
    progressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    return progressBar;
  }

  private static JLabel createVersionComparisonLabel() {
    JLabel label = new JLabel("", SwingConstants.CENTER);
    label.setFont(new Font("Arial", Font.PLAIN, 14));
    label.setForeground(Color.WHITE);
    return label;
  }

  private static JLabel createUpdatingLabel() {
    JLabel label = new JLabel("", SwingConstants.RIGHT);
    label.setFont(new Font("Arial", Font.PLAIN, 12));
    label.setForeground(Color.LIGHT_GRAY);
    return label;
  }

  private static void setupUI(
      JFrame frame,
      JLabel statusLabel,
      JProgressBar progressBar,
      JLabel versionComparisonLabel,
      JLabel updatingLabel) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(34, 45, 50));

    JPanel versionPanel = new JPanel(new BorderLayout());
    versionPanel.setBackground(new Color(34, 45, 50));
    versionPanel.add(versionComparisonLabel, BorderLayout.CENTER);

    JPanel progressPanel = new JPanel(new BorderLayout());
    progressPanel.setBackground(new Color(34, 45, 50));
    progressPanel.add(progressBar, BorderLayout.CENTER);
    progressPanel.add(updatingLabel, BorderLayout.EAST);

    panel.add(versionPanel, BorderLayout.NORTH);
    panel.add(statusLabel, BorderLayout.CENTER);
    panel.add(progressPanel, BorderLayout.SOUTH);

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

  private static void checkAndUpdate(
      File randomizer,
      JLabel statusLabel,
      JProgressBar progressBar,
      JLabel versionComparisonLabel,
      JLabel updatingLabel) {
    CompletableFuture.supplyAsync(
            () -> Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL))
        .thenAccept(
            isUpdateAvailable -> {
              CompletableFuture.runAsync(
                  () -> {
                    String currentVersion = Updater.getCurrentVersion();
                    String latestVersion = Updater.getLatestVersion(Updater.RANDOMIZER_VERSION_URL);

                    SwingUtilities.invokeLater(
                        () -> {
                          versionComparisonLabel.setText(
                              String.format(
                                  "<html><span style='color:red;'>%s</span> ➜ <span style='color:green;'>%s</span></html>",
                                  currentVersion, latestVersion));
                        });
                  });

              if (isUpdateAvailable) {
                statusLabel.setText("Update available. Updating...");
                updatingLabel.setText(String.format("Updating %s", randomizer.getName()));
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
                                        progressBar.setString(String.format("%.0f%%", progress));
                                      });
                                }))
                    .thenRun(
                        () -> {
                          SwingUtilities.invokeLater(
                              () -> {
                                statusLabel.setText("Update successful!");
                                progressBar.setValue(100);
                                progressBar.setString("Completed!");
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
                SwingUtilities.invokeLater(
                    () -> {
                      statusLabel.setText("No update available!");
                      progressBar.setValue(100);
                      progressBar.setString("Completed!");
                    });
              }
            });
  }
}
