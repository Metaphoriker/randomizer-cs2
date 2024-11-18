package com.revortix.updater;

import com.revortix.model.exception.UncaughtExceptionLogger;
import com.revortix.model.updater.Updater;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {

        Thread.currentThread()
                .setUncaughtExceptionHandler(UncaughtExceptionLogger.DEFAULT_UNCAUGHT_EXCEPTION_LOGGER);

        Color backgroundColor = new Color(5, 14, 16); // #050E10
        Color foregroundColor = new Color(218, 240, 243); // #DAF0F3
        Color accentColor = new Color(82, 40, 126); // #52287E

        JFrame mainFrame = createMainFrame(backgroundColor);
        JLabel statusLabel = createStatusLabel(foregroundColor);
        JProgressBar progressBar = createProgressBar(accentColor, backgroundColor);
        JLabel versionComparisonLabel = createVersionComparisonLabel(foregroundColor);
        setupUI(mainFrame, statusLabel, progressBar, versionComparisonLabel, backgroundColor);

        if (args.length != 0) {
            for (String arg : args) {
                if (arg.startsWith("-randomizerLocation=")) {
                    File randomizer = extractFilePath(arg);
                    checkAndUpdate(randomizer, statusLabel, progressBar, versionComparisonLabel);
                    break;
                }
            }
        } else {
            statusLabel.setText("No randomizer location specified! DO NOT OPEN THIS JAR DIRECTLY!");
        }
    }

    private static JFrame createMainFrame(Color backgroundColor) {
        JFrame frame = new JFrame();
        frame.setSize(450, 200);
        frame.setTitle("Randomizer Updater");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(backgroundColor);
        return frame;
    }

    private static JLabel createStatusLabel(Color foregroundColor) {
        JLabel label = new JLabel("Checking for updates...", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(foregroundColor);
        return label;
    }

    private static JProgressBar createProgressBar(Color accentColor, Color backgroundColor) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(accentColor);
        progressBar.setBackground(backgroundColor);
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return progressBar;
    }

    private static JLabel createVersionComparisonLabel(Color foregroundColor) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(foregroundColor);
        return label;
    }

    private static JLabel createUpdatingLabel() {
        JLabel label = new JLabel("", SwingConstants.RIGHT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private static void setupUI(
            JFrame frame,
            JLabel statusLabel,
            JProgressBar progressBar,
            JLabel versionComparisonLabel,
            Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        JPanel versionPanel = new JPanel(new BorderLayout());
        versionPanel.setBackground(backgroundColor);
        versionPanel.add(versionComparisonLabel, BorderLayout.CENTER);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(backgroundColor);
        progressPanel.add(progressBar, BorderLayout.CENTER);

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
            JLabel versionComparisonLabel) {
        CompletableFuture.supplyAsync(
                        () -> Updater.isUpdateAvailable(randomizer, Updater.RANDOMIZER_VERSION_URL))
                .thenAccept(
                        isUpdateAvailable -> {
                            CompletableFuture.runAsync(
                                    () -> {
                                        String currentVersion = Updater.getCurrentVersion(); // TODO: randomizer url
                                        String latestVersion = Updater.getLatestVersion(Updater.RANDOMIZER_VERSION_URL);

                                        SwingUtilities.invokeLater(
                                                () -> {
                                                    versionComparisonLabel.setText(
                                                            String.format(
                                                                    "<html><span style='color:red;'>%s</span> -> <span style='color:green;'>%s</span></html>",
                                                                    currentVersion, latestVersion));
                                                });
                                    });

                            if (isUpdateAvailable) {
                                statusLabel.setText("Updating...");
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
