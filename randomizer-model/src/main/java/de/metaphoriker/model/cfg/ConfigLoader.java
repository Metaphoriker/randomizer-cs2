package de.metaphoriker.model.cfg;

import de.metaphoriker.model.cfg.keybind.KeyBindRepository;
import java.io.File;
import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// TODO: merge this with KeyBindRepository
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
      log.debug("Successfully loaded {} key binds", keyBindRepository.getKeyBinds().size());
    } catch (FileNotFoundException e) {
      log.error("Error loading key binds", e);
      throw new RuntimeException(e);
    }
  }

  private static void loadDefaultKeyBinds(KeyBindRepository keyBindRepository)
      throws FileNotFoundException {
    String defaultConfigPath = findDefaultConfigFile();
    if (defaultConfigPath != null) {
      log.info("Loading default key binds from: {}", defaultConfigPath);
      keyBindRepository.initDefaults(defaultConfigPath);
    } else {
      throw new FileNotFoundException("Default config file not found.");
    }
  }

  private static void loadUserKeyBindings(KeyBindRepository keyBindRepository)
      throws FileNotFoundException {
    String userConfigPath = findUserConfigFile();
    if (userConfigPath != null) {
      log.info("Loading user key binds from: {}", userConfigPath);
      keyBindRepository.initModifiedKeyBinds(userConfigPath);
    } else {
      throw new FileNotFoundException("User config file not found.");
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
