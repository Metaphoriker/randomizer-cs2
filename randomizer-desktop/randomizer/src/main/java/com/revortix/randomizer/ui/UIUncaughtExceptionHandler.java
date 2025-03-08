package com.revortix.randomizer.ui;

import com.revortix.model.ApplicationContext;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UIUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

  private static final String GITHUB_ISSUES_URL =
      "https://github.com/Metaphoriker/randomizer-cs2/issues";

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    log.error("Unexpected error", e);

    Alert alert = createStyledAlert();
    Throwable rootCause = getRootCause(e);
    VBox content = createContent(rootCause);

    alert.getDialogPane().setContent(content);
    alert.showAndWait();
  }

  private Alert createStyledAlert() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert
        .getDialogPane()
        .getStylesheets()
        .add(getClass().getResource("alert-style.css").toExternalForm());
    alert.setTitle("An unexpected error occured");
    alert.setHeaderText("Please open an Issue on GitHub and post your logs.");
    return alert;
  }

  private Throwable getRootCause(Throwable e) {
    Throwable cause = e.getCause();
    while (cause != null) {
      e = cause;
      cause = e.getCause();
    }
    return e;
  }

  private VBox createContent(Throwable rootCause) {
    Hyperlink githubLink =
        createHyperlink(
            "Click here to get to the Issue section on GitHub.",
            GITHUB_ISSUES_URL,
            () -> openUrl(GITHUB_ISSUES_URL));
    Hyperlink logsLink =
        createHyperlink("Click here to direct to your logs folder.", null, this::openLogsFolder);

    VBox content = new VBox(10);
    content
        .getChildren()
        .addAll(
            new Label("An unexpected error occurred:"),
            new Label(rootCause.toString()),
            githubLink,
            logsLink);
    return content;
  }

  private Hyperlink createHyperlink(String text, String url, Runnable action) {
    Hyperlink hyperlink = new Hyperlink(text);
    hyperlink.setOnAction(_ -> action.run());
    return hyperlink;
  }

  private void openUrl(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException e) {
      log.error("Could not open URL: " + url, e);
      throw new IllegalStateException(e);
    }
  }

  private void openLogsFolder() {
    try {
      File logsFolder = ApplicationContext.getAppdataLogsFolder();
      if (logsFolder.exists() && logsFolder.isDirectory()) {
        Desktop.getDesktop().open(logsFolder);
      } else {
        log.error("Log folder does not exist: {}", logsFolder.getAbsolutePath());
        throw new IllegalStateException("Log folder does not exist: " + logsFolder.getAbsolutePath());
      }
    } catch (IOException e) {
      log.error("Could not open logs folder", e);
      throw new IllegalStateException(e);
    }
  }
}
