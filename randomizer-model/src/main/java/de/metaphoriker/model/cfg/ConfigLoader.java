package de.metaphoriker.model.cfg;

import de.metaphoriker.model.cfg.keybind.KeyBindRepository;
import java.io.File;
import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigLoader {

  private static final String[] STEAM_PATHS = {
    "C:/Program Files (x86)/Steam",
    "C:/Program Files/Steam",
    "D:/SteamLibrary",
    "D:/Program Files (x86)/Steam"
  };

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
      throw new FileNotFoundException("Benutzerkonfigurationsdatei nicht gefunden.");
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
        File[] userDirs = userdataFolder.listFiles(File::isDirectory);
        if (userDirs != null) {
          for (File userDir : userDirs) {
            File gameFolder = new File(userDir, "730/remote/cs2_user_keys.vcfg");
            if (gameFolder.exists()) {
              return gameFolder.getAbsolutePath();
            }
          }
        }
      }
    }
    return null;
  }
}
