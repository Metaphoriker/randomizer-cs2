package de.metaphoriker.model.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@UtilityClass
@Slf4j
public class Updater {

  public static final String UPDATER_VERSION_URL =
      "https://raw.githubusercontent.com/Metaphoriker/randomizer-cs2/stage/randomizer-desktop/randomizer-updater/src/main/resources/version.txt";
  public static final String UPDATER_DOWNLOAD_URL =
      "https://github.com/Metaphoriker/randomizer-cs2/releases/download/latest/randomizer-updater.jar";
  public static final String RANDOMIZER_VERSION_URL =
      "https://raw.githubusercontent.com/Metaphoriker/randomizer-cs2/stage/randomizer-model/src/main/resources/version.txt";
  public static final String RANDOMIZER_DOWNLOAD_URL =
      "https://github.com/Metaphoriker/randomizer-cs2/releases/download/latest/randomizer.jar";

  public static void update(File target, String downloadUrl) {
    update(target, downloadUrl, null);
  }

  public static void update(File target, String downloadUrl, ProgressListener listener) {
    log.debug("Starte Aktualisierung von URL: {} zu Ziel-Datei: {}", downloadUrl, target);

    try {
      URL downloadFrom = new URL(downloadUrl);
      copyURLToFileWithProgress(downloadFrom, target, listener);
      log.debug("Erfolgreich aktualisiert von URL: {} zu Ziel-Datei: {}", downloadUrl, target);
    } catch (IOException ignored) {
      log.error(
          "Fehler bei der Aktualisierung von URL: {} zu Ziel-Datei: {}",
          downloadUrl,
          target,
          ignored);
    }
  }

  public static void copyURLToFileWithProgress(
      URL source, File destination, ProgressListener listener) throws IOException {
    log.debug("Kopiere Daten von URL: {} zu Ziel-Datei: {}", source, destination);
    try (InputStream inputStream = source.openStream()) {
      long totalBytes = source.openConnection().getContentLengthLong();
      try (OutputStream outputStream = FileUtils.openOutputStream(destination)) {
        byte[] buffer = new byte[1024];
        long bytesRead = 0;
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
          outputStream.write(buffer, 0, n);
          bytesRead += n;
          if (listener != null) {
            listener.onProgress(bytesRead, totalBytes);
          }
          log.debug("Kopierfortschritt: {}/{} Bytes", bytesRead, totalBytes);
        }
        log.debug("Daten von URL: {} erfolgreich zu Ziel-Datei: {} kopiert", source, destination);
      }
    }
  }

  public static boolean isUpdateAvailable(File toUpdate, String versionUrl) {
    log.debug("Prüfen auf Update: Datei = {}, Versions-URL = {}", toUpdate, versionUrl);

    if (!toUpdate.exists()) {
      log.debug("Update verfügbar: Ziel-Datei existiert nicht: {}", toUpdate);
      return true;
    }

    try (ZipFile zipFile = new ZipFile(toUpdate)) {
      ZipEntry versionEntry = zipFile.getEntry("version.txt");
      if (versionEntry == null) {
        log.debug("Update verfügbar: version.txt Eintrag wurde nicht im Zip-Archiv gefunden");
        return true;
      }

      String version = readLineFromInputStream(zipFile.getInputStream(versionEntry));
      boolean updateAvailable = isUpdateAvailable(version, versionUrl);
      log.debug(
          "Update-Verfügbarkeit geprüft: aktuelle Version = {}, Update verfügbar = {}",
          version,
          updateAvailable);
      return updateAvailable;
    } catch (IOException ignored) {
      log.error("Fehler bei der Prüfung auf Update von Datei: {}", toUpdate, ignored);
    }
    return true;
  }

  public static boolean isUpdateAvailable(String version, String versionUrl) {
    log.debug(
        "Prüfen auf Update mit aktueller Version: {} unter Verwendung der Versions-URL: {}",
        version,
        versionUrl);

    UpdateChecker updateChecker = new UpdateChecker(versionUrl);
    updateChecker.checkUpdate(version);

    boolean updateAvailable = updateChecker.isUpdateAvailable();
    log.debug(
        "Update-Prüfung abgeschlossen: Versions-URL = {}, Update verfügbar = {}",
        versionUrl,
        updateAvailable);
    return updateAvailable;
  }

  public static String getCurrentVersion() {
    log.debug("Hole aktuelle Version aus interner Ressource: version.txt");
    return readLineFromInputStream(getInputStream("version.txt"));
  }

  private static InputStream getInputStream(String fileName) {
    log.debug("Öffne InputStream für Ressourcendatei: {}", fileName);
    InputStream resource = Updater.class.getResourceAsStream("/" + fileName);

    if (resource == null) {
      log.error("Ressource nicht gefunden: {}", fileName);
      throw new IllegalStateException("Wahrscheinlich beschädigte JAR-Datei, fehlt " + fileName);
    }

    return resource;
  }

  private static String readLineFromInputStream(InputStream inputStream) {
    log.debug("Lese Zeile aus InputStream");

    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.readLine();
    } catch (IOException e) {
      log.error("Fehler beim Lesen der Version aus dem InputStream", e);
      return "UNGÜLTIG";
    }
  }

  private static boolean isEmpty(File file) {
    log.debug("Prüfe, ob Datei leer ist: {}", file);
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(FileUtils.openInputStream(file), StandardCharsets.UTF_8))) {
      return bufferedReader.readLine() == null;
    } catch (IOException e) {
      log.error("Fehler bei der Prüfung, ob die Datei leer ist: {}", file, e);
      throw new RuntimeException(e);
    }
  }
}
