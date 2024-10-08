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
    try {
      URL downloadFrom = new URL(downloadUrl);
      copyURLToFileWithProgress(downloadFrom, target, listener);
    } catch (IOException ignored) {
      log.error("Failed to update", ignored);
    }
  }

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
      }
    }
  }

  public static boolean isUpdateAvailable(File toUpdate, String versionUrl) {

    if (!toUpdate.exists()) return true;

    try (ZipFile zipFile = new ZipFile(toUpdate)) {
      ZipEntry versionEntry = zipFile.getEntry("version.txt");
      if (versionEntry == null) return true;

      String version = readLineFromInputStream(zipFile.getInputStream(versionEntry));
      return isUpdateAvailable(version, versionUrl);
    } catch (IOException ignored) {
      log.error("Failed to check for update", ignored);
    }
    return true;
  }

  public static boolean isUpdateAvailable(String version, String versionUrl) {

    UpdateChecker updateChecker = new UpdateChecker(versionUrl);
    updateChecker.checkUpdate(version);

    return updateChecker.isUpdateAvailable();
  }

  public static String getCurrentVersion() {
    return readLineFromInputStream(getInputStream("version.txt"));
  }

  private static InputStream getInputStream(String fileName) {

    InputStream resource = Updater.class.getResourceAsStream("/" + fileName);

    if (resource == null)
      throw new IllegalStateException("Probably corrupted JAR file, missing " + fileName);

    return resource;
  }

  private static String readLineFromInputStream(InputStream inputStream) {

    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.readLine();
    } catch (IOException e) {
      log.error("Failed to read version", e);
    }

    return "INVALID";
  }

  private static boolean isEmpty(File file) {
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(FileUtils.openInputStream(file), StandardCharsets.UTF_8))) {
      return bufferedReader.readLine() == null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
