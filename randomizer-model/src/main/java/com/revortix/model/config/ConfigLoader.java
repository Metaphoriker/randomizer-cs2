package com.revortix.model.config;

import com.revortix.model.config.keybind.KeyBindRepository;
import java.io.File;
import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigLoader {

  private static final String[] STEAM_PATHS = {
    "C:/Program Files (x86)/Steam",
    "C:/Program Files/Steam",
    "C:/Steam",
    "D:/SteamLibrary",
    "D:/Program Files (x86)/Steam",
    "D:/Steam",
    "D:/Games/Steam"
  };

  /**
   * Loads key bindings from default and user configuration files into the provided
   * KeyBindRepository.
   *
   * @param keyBindRepository the repository where key bindings will be loaded
   * @throws RuntimeException if the key bindings could not be loaded due to a FileNotFoundException
   */
  public static void loadKeyBinds(KeyBindRepository keyBindRepository) {
    try {
      loadDefaultKeyBinds(keyBindRepository);
      loadUserKeyBindings(keyBindRepository);
      log.info("Erfolgreich {} Key-Bindings geladen", keyBindRepository.getKeyBinds().size());
    } catch (FileNotFoundException e) {
      log.error("Fehler beim Laden der Key-Bindings", e);
      throw new RuntimeException(e);
    }
  }

  private static void loadDefaultKeyBinds(KeyBindRepository keyBindRepository)
      throws FileNotFoundException {
    String defaultConfigPath = findDefaultConfigFile();
    if (defaultConfigPath != null) {
      log.info("Lade Standard-Key-Bindings von: {}", defaultConfigPath);
      keyBindRepository.initDefaults(defaultConfigPath);
    } else {
      throw new FileNotFoundException("Standardkonfigurationsdatei nicht gefunden.");
    }
  }

  private static void loadUserKeyBindings(KeyBindRepository keyBindRepository)
      throws FileNotFoundException {
    String userConfigPath = findUserConfigFile();
    if (userConfigPath != null) {
      log.info("Lade Benutzer-Key-Bindings von: {}", userConfigPath);
      keyBindRepository.initModifiedKeyBinds(userConfigPath);
    } else {
      log.info("Keine Benutzer-Key-Bindings gefunden.");
    }
  }

  private static String findDefaultConfigFile() {
    for (String steamPath : STEAM_PATHS) {
      File defaultConfig =
          new File(
              steamPath
                  + "/steamapps/common/Counter-Strike Global Offensive/game/csgo/cfg/user_keys_default.vcfg");
      if (defaultConfig.exists()) {
        return defaultConfig.getAbsolutePath();
      }
    }
    return null;
  }

  private static String findUserConfigFile() {
    for (String steamPath : STEAM_PATHS) {
      File userdataFolder = new File(steamPath + "/userdata");
      if (userdataFolder.exists()) {
        File[] userDirs = listUserDirectories(userdataFolder);
        if (userDirs != null) {
          String configFilePath = searchInUserDirectories(userDirs);
          if (configFilePath != null) {
            return configFilePath;
          }
        }
      }
    }
    return null;
  }

  private static File[] listUserDirectories(File userdataFolder) {
    return userdataFolder.listFiles(File::isDirectory);
  }

  private static String searchInUserDirectories(File[] userDirs) {
    for (File userDir : userDirs) {
      File gameFolder = new File(userDir, "730/remote/cs2_user_keys.vcfg");
      if (gameFolder.exists()) {
        return gameFolder.getAbsolutePath();
      }
    }
    return null;
  }
}
