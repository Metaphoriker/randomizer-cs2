package com.revortix.model.updater;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class UpdateChecker {

    private final String versionUrl;

    @Getter
    private boolean updateAvailable;

    public UpdateChecker(String versionUrl) {
        this.versionUrl = versionUrl;
    }

    /**
     * Checks for the availability of an update by comparing the current version with the latest version available
     * online. Logs the process and the result of the update check.
     *
     * @param version The current version of the software to be checked against the latest version available online.
     */
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
        } catch (Exception e) {
            log.error("Fehler beim Prüfen auf Update", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
                log.debug("Verbindung geschlossen");
            }
        }
    }

    private String readLineFromInputStream(InputStream inputStream) {
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            log.error("Fehler beim Lesen der Zeile aus dem InputStream", e);
            return "UNGÜLTIG";
        }
    }
}
