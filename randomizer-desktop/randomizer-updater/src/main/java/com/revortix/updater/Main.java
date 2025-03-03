package com.revortix.updater;

import com.revortix.model.exception.UncaughtExceptionLogger;
import com.revortix.model.updater.Updater;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main {

  private static final Color LIGHT_BACKGROUND = new Color(248, 249, 250); // Light Gray Background
  private static final Color LIGHT_TEXT = new Color(33, 37, 41); // Dark Gray Text
  private static final Color ACCENT_COLOR = new Color(0, 123, 255); // Bootstrap Primary Blue
  private static final Color ACCENT_COLOR_DARKER =
      new Color(0, 86, 179); // Darker Blue for progress bar
  private static final Color SUCCESS_COLOR = new Color(40, 167, 69); // Green for success
  private static final Color WARNING_COLOR = new Color(255, 193, 7); // Yellow for warnings
  private static final Color ERROR_COLOR = new Color(220, 53, 69); // Red for errors

  private JFrame mainFrame;
  private JLabel statusLabel;
  private JProgressBar progressBar;
  private JLabel versionComparisonLabel;

  public static void main(String[] args) {
    Thread.currentThread()
        .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

    SwingUtilities.invokeLater(() -> new Main().initialize(args));
  }

  private void initialize(String[] args) {
    setupUI();

    if (args.length != 0) {
      for (String arg : args) {
        if (arg.startsWith("-randomizerLocation=")) {
          File randomizer = extractFilePath(arg);
          if (!randomizer.exists()) {
            showErrorDialog("Randomizer file does not exist!");
            return; // Exit the checkAndUpdate process
          }
          checkAndUpdate(randomizer);
          break;
        }
      }
    } else {
      showErrorDialog(
          "No randomizer file specified! Please specify the randomizer file location with -randomizerLocation=<randomizerFilePath>");
    }
  }

  private void setupUI() {
    mainFrame = createMainFrame();
    statusLabel = createStatusLabel();
    progressBar = createProgressBar();
    versionComparisonLabel = createVersionComparisonLabel();

    JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
    contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
    contentPanel.setBackground(LIGHT_BACKGROUND); // Set background color

    JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
    versionPanel.setBackground(LIGHT_BACKGROUND);
    versionPanel.add(versionComparisonLabel);

    JPanel progressPanel = new JPanel(new BorderLayout());
    progressPanel.setBackground(LIGHT_BACKGROUND);
    progressPanel.add(progressBar, BorderLayout.CENTER);

    contentPanel.add(versionPanel, BorderLayout.NORTH);
    contentPanel.add(statusLabel, BorderLayout.CENTER);
    contentPanel.add(progressPanel, BorderLayout.SOUTH);

    mainFrame.setContentPane(contentPanel); // Use setContentPane
    mainFrame.setVisible(true);
  }

  private JFrame createMainFrame() {
    JFrame frame = new JFrame();
    frame.setSize(550, 250);
    frame.setTitle("Randomizer Updater");
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().setBackground(LIGHT_BACKGROUND); // Set background
    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            frame.dispose();
            System.exit(0);
          }
        });
    return frame;
  }

  private JLabel createStatusLabel() {
    JLabel label = new JLabel("Checking for updates...", SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    label.setForeground(LIGHT_TEXT);
    return label;
  }

  private JProgressBar createProgressBar() {
    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setStringPainted(true);
    progressBar.setForeground(ACCENT_COLOR_DARKER);
    progressBar.setBackground(LIGHT_BACKGROUND);
    progressBar.setBorder(BorderFactory.createLineBorder(LIGHT_BACKGROUND, 2));
    progressBar.setUI(
        new javax.swing.plaf.basic.BasicProgressBarUI() {
          @Override
          protected Color getSelectionBackground() {
            return LIGHT_TEXT;
          }

          @Override
          protected Color getSelectionForeground() {
            return LIGHT_BACKGROUND;
          }
        });
    return progressBar;
  }

  private JLabel createVersionComparisonLabel() {
    JLabel label = new JLabel("", SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
    label.setForeground(LIGHT_TEXT);
    return label;
  }

  private File extractFilePath(String arg) {
    File path = new File(arg.substring(arg.indexOf("=") + 1));
    String pathName =
        path.getAbsolutePath().substring(0, path.getAbsolutePath().lastIndexOf(File.separator));
    String fileName =
        path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf(File.separator) + 1);
    return new File(pathName, fileName);
  }

  private void checkAndUpdate(File randomizer) {
    CompletableFuture.supplyAsync(
            () -> Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL))
        .thenAcceptAsync(
            isUpdateAvailable -> { // No need for nested invokeLater
              String currentVersion = Updater.getVersion(randomizer);
              String latestVersion = Updater.getLatestVersion(Updater.RANDOMIZER_VERSION_URL);

              SwingUtilities.invokeLater(
                  () ->
                      versionComparisonLabel.setText(
                          String.format(
                              "<html><span style='color:#%02x%02x%02x;'>%s</span> &rarr; <span style='color:#%02x%02x%02x;'>%s</span></html>",
                              currentVersion.equals(latestVersion)
                                  ? WARNING_COLOR.getRed()
                                  : ERROR_COLOR
                                      .getRed(), // Use warning color if versions are same, else
                              // error
                              currentVersion.equals(latestVersion)
                                  ? WARNING_COLOR.getGreen()
                                  : ERROR_COLOR.getGreen(),
                              currentVersion.equals(latestVersion)
                                  ? WARNING_COLOR.getBlue()
                                  : ERROR_COLOR.getBlue(),
                              currentVersion,
                              SUCCESS_COLOR.getRed(),
                              SUCCESS_COLOR.getGreen(),
                              SUCCESS_COLOR.getBlue(),
                              latestVersion)));

              if (isUpdateAvailable) {
                SwingUtilities.invokeLater(
                    () -> statusLabel.setText(formatColorText("Updating...", ACCENT_COLOR)));
                updateRandomizer(randomizer);
              } else {
                SwingUtilities.invokeLater(
                    () -> {
                      statusLabel.setText(formatColorText("No update available!", SUCCESS_COLOR));
                      progressBar.setValue(100);
                      progressBar.setString("Up to date!");
                    });
              }
            },
            SwingUtilities::invokeLater)
        .exceptionally(
            e -> {
              SwingUtilities.invokeLater(
                  () -> showErrorDialog("Error during update check: " + e.getMessage()));
              return null;
            });
  }

  private void showErrorDialog(String errorMessage) {
    JLabel label = new JLabel(errorMessage, SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    label.setForeground(ERROR_COLOR);
    label.setBackground(LIGHT_BACKGROUND);
    label.setOpaque(true);

    JOptionPane optionPane =
        new JOptionPane(
            label,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] {"OK"},
            "OK");

    JDialog dialog = optionPane.createDialog(mainFrame, "Fehler");
    dialog.setModal(true);
    dialog.setVisible(true);

    System.exit(0);
  }

  private void updateRandomizer(File randomizer) {
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
                            progressBar.setString(String.format("%.1f%%", progress));
                          });
                    }))
        .thenRunAsync(
            () -> { // thenRunAsync is correct here
              SwingUtilities.invokeLater(
                  () -> {
                    statusLabel.setText(formatColorText("Update successful!", SUCCESS_COLOR));
                    progressBar.setValue(100);
                    progressBar.setString("Completed!");
                    launchRandomizer(randomizer);
                  });
            },
            SwingUtilities::invokeLater)
        .exceptionally(
            e -> {
              SwingUtilities.invokeLater(() -> showErrorDialog("Update failed: " + e.getMessage()));
              return null; // Must return null
            });
  }

  private void launchRandomizer(File randomizer) {
    try {
      Runtime.getRuntime().exec("java -jar " + randomizer.getAbsolutePath());
      Thread.sleep(2000);
      System.exit(0);
    } catch (IOException | InterruptedException ex) {
      SwingUtilities.invokeLater(
          () -> showErrorDialog("Failed to launch updated randomizer: " + ex.getMessage()));
    }
  }

  private String formatColorText(String text, Color color) {
    return String.format(
        "<html><font color='#%02x%02x%02x'>%s</font></html>",
        color.getRed(), color.getGreen(), color.getBlue(), text);
  }
}
