package com.revortix.model.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@UtilityClass
@Slf4j
public class Updater {

  public static final String UPDATER_VERSION_URL =
      "https://raw.githubusercontent.com/revortix/randomizer-cs2/stage/randomizer-desktop/randomizer-updater/src/main/resources/updater-version.txt";
  public static final String UPDATER_DOWNLOAD_URL =
      "https://github.com/revortix/randomizer-cs2/releases/download/latest/randomizer-updater.jar";
  public static final String RANDOMIZER_VERSION_URL =
      "https://raw.githubusercontent.com/revortix/randomizer-cs2/stage/randomizer-model/src/main/resources/model-version.txt";
  public static final String RANDOMIZER_DOWNLOAD_URL =
      "https://github.com/revortix/randomizer-cs2/releases/download/latest/randomizer.jar";

  /**
   * Updates the specified target file using the provided download URL.
   *
   * @param target The file to be updated.
   * @param downloadUrl The URL from which the file update should be downloaded.
   */
  public static void update(File target, String downloadUrl) {
    update(target, downloadUrl, null);
  }

  /**
   * Updates the target file with content from the specified download URL, providing progress
   * through the listener.
   *
   * @param target the file to update
   * @param downloadUrl the URL to download content from
   * @param listener the listener to track the progress of the download
   */
  public static void update(File target, String downloadUrl, ProgressListener listener) {
    log.info("Starte Aktualisierung von URL: {} zu Ziel-Datei: {}", downloadUrl, target);

    try {
      URL downloadFrom = new URL(downloadUrl);
      copyURLToFileWithProgress(downloadFrom, target, listener);
      log.info("Erfolgreich aktualisiert von URL: {} zu Ziel-Datei: {}", downloadUrl, target);
    } catch (IOException ignored) {
      log.error("Fehler bei der Aktualisierung von URL: {} zu Ziel-Datei: {}", downloadUrl, target);
      throw new RuntimeException(ignored);
    }
  }

  /**
   * Downloads a file from the specified URL and saves it to the specified destination file,
   * providing progress updates via a ProgressListener.
   *
   * @param source the URL to copy the file from.
   * @param destination the file to which the URL's content will be copied.
   * @param listener a ProgressListener to receive updates about the copy progress, can be null.
   * @throws IOException if an error occurs during the file copy process.
   */
  public static void copyURLToFileWithProgress(
      URL source, File destination, ProgressListener listener) throws IOException {
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
        }
        log.info("Daten von URL: {} erfolgreich zu Ziel-Datei: {} kopiert", source, destination);
      }
    }
  }

  @Getter
  public enum FileType {
    UPDATER("updater-"),
    RANDOMIZER("model-");

    private final String prefix;

    FileType(String prefix) {
      this.prefix = prefix;
    }
  }

  /**
   * Checks if an update is available for the given file by comparing its version with the version
   * retrieved from a specified URL.
   *
   * @param toUpdate the file to be checked for updates
   * @param versionUrl the URL from which the latest version information can be retrieved
   * @return true if an update is available or the file is not found/accessible, false otherwise
   */
  public static boolean isUpdateAvailable(File toUpdate, String versionUrl, FileType fileType) {
    log.info("Prüfen auf Update: Datei = {}, Versions-URL = {}", toUpdate, versionUrl);

    if (!toUpdate.exists()) {
      log.info("Update verfügbar: Ziel-Datei existiert nicht: {}", toUpdate);
      return true;
    }

    log.info("Update für Version={} von Datei={} wird geprüft", getVersion(toUpdate, fileType), toUpdate);

    String versionFile = fileType.getPrefix() + "version.txt";
    try (ZipFile zipFile = new ZipFile(toUpdate)) {
      ZipEntry versionEntry = zipFile.getEntry(versionFile);
      if (versionEntry == null) {
        log.info("Update verfügbar: model-version.txt Eintrag wurde nicht im Zip-Archiv gefunden");
        return true;
      }

      String version = readLineFromInputStream(zipFile.getInputStream(versionEntry));
      boolean updateAvailable = isUpdateAvailable(version, versionUrl);
      log.info(
          "Update-Verfügbarkeit geprüft: aktuelle Version = {}, Update verfügbar = {}",
          version,
          updateAvailable);
      return updateAvailable;
    } catch (IOException ignored) {
      log.error("Fehler bei der Prüfung auf Update von Datei: {}", toUpdate);
      throw new RuntimeException(ignored);
    }
  }

  public static String getVersion(File file, FileType fileType) {
    if (!file.exists()) {
      log.warn("Datei nicht gefunden: {}", file);
      return "NOT FOUND";
    }

    String versionFile = fileType.getPrefix() + "version.txt";
    try (ZipFile zipFile = new ZipFile(file)) {
      ZipEntry versionEntry = zipFile.getEntry(versionFile);
      if (versionEntry == null) {
        return "NOT FOUND";
      }

      return readLineFromInputStream(zipFile.getInputStream(versionEntry));
    } catch (IOException ignored) {
      log.error("Fehler bei der Prüfung auf Update von Datei: {}", file);
    }
    return "NOT FOUND";
  }

  /**
   * Retrieves the latest version from a specified version URL.
   *
   * @param versionUrl the URL from which the latest version information can be retrieved
   * @return a String representing the latest version.
   */
  public static String getLatestVersion(String versionUrl) {
    log.info("Abrufen der neuesten Version von URL: {}", versionUrl);
    try (BufferedReader in =
        new BufferedReader(new InputStreamReader(new URL(versionUrl).openStream()))) {
      String latestVersion = in.readLine();
      log.info("Erfolgreich abgerufen: neueste Version = {}", latestVersion);
      return latestVersion;
    } catch (IOException e) {
      log.error("Fehler beim Abrufen der neuesten Version von URL: {}", versionUrl, e);
      return "UNGÜLTIG";
    }
  }

  /**
   * Checks if an update is available for the given version using the specified version URL.
   *
   * @param version the current version to check against
   * @param versionUrl the URL used to check for the latest version
   * @return true if an update is available, false otherwise
   */
  public static boolean isUpdateAvailable(String version, String versionUrl) {
    log.info(
        "Prüfen auf Update mit aktueller Version: {} unter Verwendung der Versions-URL: {}",
        version,
        versionUrl);

    UpdateChecker updateChecker = new UpdateChecker(versionUrl);
    updateChecker.checkUpdate(version);

    boolean updateAvailable = updateChecker.isUpdateAvailable();
    log.info(
        "Update-Prüfung abgeschlossen: Versions-URL = {}, Update verfügbar = {}",
        versionUrl,
        updateAvailable);
    return updateAvailable;
  }

  private static String readLineFromInputStream(InputStream inputStream) {
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.readLine();
    } catch (IOException e) {
      log.error("Fehler beim Lesen der Version aus dem InputStream", e);
      return "UNGÜLTIG";
    }
  }
}
