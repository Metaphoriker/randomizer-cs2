package com.revortix.randomizer.bootstrap;

import com.revortix.model.ApplicationContext;
import de.metaphoriker.updater.Updater;
import de.metaphoriker.updater.util.JarFileUtil;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerUpdater {

  private static void startProcess(ProcessBuilder processBuilder)
      throws IOException, InterruptedException {
    Process process = processBuilder.start();
    process.waitFor();
  }

  public void runUpdaterIfNeeded() {
    log.info("Richte Appdata Verzeichnis ein...");
    File updater = getUpdater();
    startUpdaterIfNecessary(updater.getAbsolutePath());
  }

  public boolean isRandomizerUpdateAvailable() {
    try {
      return Updater.isUpdateAvailable(
          Updater.getVersion(JarFileUtil.getJarFile(), Updater.FileType.RANDOMIZER),
          Updater.RANDOMIZER_VERSION_URL);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public File getUpdater() {
    File updaterJar = new File(ApplicationContext.getAppdataFolder(), "randomizer-updater.jar");
    if (!updaterJar.exists()) {
      updaterJar = installUpdater();
    }
    return updaterJar;
  }

  public File installUpdater() {
    log.info("Installiere Updater...");
    File updaterJar = new File(ApplicationContext.getAppdataFolder(), "randomizer-updater.jar");
    try {
      if (!updaterJar.exists()) updaterJar.createNewFile();
    } catch (IOException e) {
      log.error("Fehler beim Erstellen der Updater Datei", e);
      throw new RuntimeException(e);
    }

    if (Updater.isUpdateAvailable(
        updaterJar, Updater.UPDATER_VERSION_URL, Updater.FileType.UPDATER)) {
      Updater.update(updaterJar, Updater.UPDATER_DOWNLOAD_URL);
    }

    return updaterJar;
  }

  private void startUpdaterIfNecessary(String path) {
    log.info("Starte Updater falls notwendig...");
    try {
      File jarPath = JarFileUtil.getJarFile();
      if (Updater.isUpdateAvailable(
          Updater.getVersion(jarPath, Updater.FileType.RANDOMIZER),
          Updater.RANDOMIZER_VERSION_URL)) {
        log.info("Updater gestartet");
        ProcessBuilder processBuilder =
            new ProcessBuilder(
                "java", "-jar", path, "-randomizerLocation=" + jarPath.getAbsolutePath());
        processBuilder.inheritIO();
        startProcess(processBuilder);
        Platform.exit(); // we want to close the randomizer in order to update it
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
