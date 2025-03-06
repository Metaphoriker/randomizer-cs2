package de.metaphoriker.updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    log("Starte Aktualisierung von URL: " + downloadUrl + " zu Ziel-Datei: " + target);

    try {
      URL downloadFrom = new URL(downloadUrl);
      copyURLToFileWithProgress(downloadFrom, target, listener);
      log("Erfolgreich aktualisiert von URL: " + downloadUrl + " zu Ziel-Datei: " + target);
    } catch (IOException e) {
      log("Fehler bei der Aktualisierung von URL: " + downloadUrl + " zu Ziel-Datei: " + target);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Downloads a file from the specified URL and saves it to the specified destination file,
   * providing progress updates via a ProgressListener. Uses HttpURLConnection for more control over
   * the connection.
   *
   * @param source the URL to copy the file from.
   * @param destination the file to which the URL's content will be copied.
   * @param listener a ProgressListener to receive updates about the copy progress, can be null.
   * @throws IOException if an error occurs during the file copy process.
   */
  public static void copyURLToFileWithProgress(
      URL source, File destination, ProgressListener listener) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) source.openConnection();
    connection.setRequestMethod("GET"); // Ensure GET request

    try (InputStream inputStream = connection.getInputStream()) {
      long totalBytes = connection.getContentLengthLong(); // Get content length from the connection

      // Create parent directories if they don't exist
      File parentDir = destination.getParentFile();
      if (parentDir != null && !parentDir.exists()) {
        if (!parentDir.mkdirs()) {
          throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
        }
      }

      try (FileOutputStream outputStream =
          new FileOutputStream(destination)) { // Use FileOutputStream
        byte[] buffer = new byte[1024];
        long bytesRead = 0;
        int n;
        while ((n = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, n);
          bytesRead += n;
          if (listener != null) {
            listener.onProgress(bytesRead, totalBytes);
          }
        }
        log("Daten von URL: " + source + " erfolgreich zu Ziel-Datei: " + destination + " kopiert");
      }
    } finally {
      connection.disconnect(); // Always disconnect
    }
  }

  public enum FileType {
    UPDATER("updater-"),
    RANDOMIZER("model-");

    private final String prefix;

    FileType(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
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
    log("Prüfen auf Update: Datei = " + toUpdate + ", Versions-URL = " + versionUrl);

    if (!toUpdate.exists()) {
      log("Update verfügbar: Ziel-Datei existiert nicht: " + toUpdate);
      return true;
    }
    String version = getVersion(toUpdate, fileType);
    log("Update für Version=" + version + " von Datei=" + toUpdate + " wird geprüft");

    String versionFile = fileType.getPrefix() + "version.txt";
    try (ZipFile zipFile = new ZipFile(toUpdate)) {
      ZipEntry versionEntry = zipFile.getEntry(versionFile);
      if (versionEntry == null) {
        log(
            "Update verfügbar: "
                + fileType.getPrefix()
                + "version.txt Eintrag wurde nicht im Zip-Archiv gefunden");
        return true;
      }

      version = readLineFromInputStream(zipFile.getInputStream(versionEntry));
      boolean updateAvailable = isUpdateAvailable(version, versionUrl);
      log(
          "Update-Verfügbarkeit geprüft: aktuelle Version = "
              + version
              + ", Update verfügbar = "
              + updateAvailable);
      return updateAvailable;
    } catch (IOException e) {
      log("Fehler bei der Prüfung auf Update von Datei: " + toUpdate);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static String getVersion(File file, FileType fileType) {
    if (!file.exists()) {
      log("Datei nicht gefunden: " + file);
      return "NOT FOUND";
    }

    String versionFile = fileType.getPrefix() + "version.txt";
    try (ZipFile zipFile = new ZipFile(file)) {
      ZipEntry versionEntry = zipFile.getEntry(versionFile);
      if (versionEntry == null) {
        return "NOT FOUND";
      }

      return readLineFromInputStream(zipFile.getInputStream(versionEntry));
    } catch (IOException e) {
      log("Fehler bei der Prüfung auf Update von Datei: " + file);
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the latest version from a specified version URL.
   *
   * @param versionUrl the URL from which the latest version information can be retrieved
   * @return a String representing the latest version.
   */
  public static String getLatestVersion(String versionUrl) {
    log("Abrufen der neuesten Version von URL: " + versionUrl);
    try {
      URL url = new URL(versionUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET"); // Set request method explicitly

      try (BufferedReader in =
          new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String latestVersion = in.readLine();
        log("Erfolgreich abgerufen: neueste Version = " + latestVersion);
        return latestVersion;
      } finally {
        connection.disconnect(); // Ensure disconnection
      }
    } catch (IOException e) {
      log("Fehler beim Abrufen der neuesten Version von URL: " + versionUrl);
      e.printStackTrace(); // Log the exception details
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
    log(
        "Prüfen auf Update mit aktueller Version: "
            + version
            + " unter Verwendung der Versions-URL: "
            + versionUrl);

    UpdateChecker updateChecker = new UpdateChecker(versionUrl);
    updateChecker.checkUpdate(version);

    boolean updateAvailable = updateChecker.isUpdateAvailable();
    log(
        "Update-Prüfung abgeschlossen: Versions-URL = "
            + versionUrl
            + ", Update verfügbar = "
            + updateAvailable);
    return updateAvailable;
  }

  private static String readLineFromInputStream(InputStream inputStream) {
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.readLine();
    } catch (IOException e) {
      log("Fehler beim Lesen der Version aus dem InputStream");
      e.printStackTrace();
      return "UNGÜLTIG";
    }
  }

  private static void log(String message) {
    System.out.println(message); // TODO: add proper logging
  }
}
