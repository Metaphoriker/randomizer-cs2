package de.metaphoriker.model.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateChecker {

  private final String versionUrl;

  @Getter private boolean updateAvailable;

  public UpdateChecker(String versionUrl) {
    this.versionUrl = versionUrl;
  }

  public void checkUpdate(String version) {

    HttpURLConnection connection = null;
    try {

      connection = (HttpURLConnection) new URL(versionUrl).openConnection();
      connection.connect();

      String latestVersion = readLineFromInputStream(connection.getInputStream());
      updateAvailable = !latestVersion.equals(version);

    } catch (Exception ignored) {
      log.error("Failed to check for update", ignored);
    } finally {
      if (connection != null) connection.disconnect();
    }
  }

  private String readLineFromInputStream(InputStream inputStream) {

    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      return bufferedReader.readLine();
    } catch (IOException e) {
      log.error("Failed to read line from input stream", e);
    }

    return "INVALID";
  }
}
