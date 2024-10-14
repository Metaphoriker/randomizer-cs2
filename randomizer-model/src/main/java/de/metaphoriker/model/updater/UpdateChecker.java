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
    log.debug("Starte Update-Check: aktuelle Version = {}, Versions-URL = {}", version, versionUrl);

    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) new URL(versionUrl).openConnection();
      connection.connect();

      String latestVersion = readLineFromInputStream(connection.getInputStream());
      updateAvailable = !latestVersion.equals(version);

      log.debug(
          "Update-Check abgeschlossen: neueste Version = {}, Update verfügbar = {}",
          latestVersion,
          updateAvailable);
    } catch (Exception ignored) {
      log.error("Fehler beim Prüfen auf Update", ignored);
    } finally {
      if (connection != null) {
        connection.disconnect();
        log.debug("Verbindung geschlossen");
      }
    }
  }

  private String readLineFromInputStream(InputStream inputStream) {
    log.debug("Lese Zeile aus dem InputStream");

    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line = bufferedReader.readLine();
      log.debug("Zeile gelesen: {}", line);
      return line;
    } catch (IOException e) {
      log.error("Fehler beim Lesen der Zeile aus dem InputStream", e);
      return "UNGÜLTIG";
    }
  }
}
