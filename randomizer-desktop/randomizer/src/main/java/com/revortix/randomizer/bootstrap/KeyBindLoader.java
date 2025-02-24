package com.revortix.randomizer.bootstrap;

import com.revortix.model.config.ConfigLoader;
import com.revortix.model.config.keybind.KeyBindRepository;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class KeyBindLoader {

  private final KeyBindRepository keyBindRepository;

  public KeyBindLoader(KeyBindRepository keyBindRepository) {
    this.keyBindRepository = keyBindRepository;
  }

  private void loadKeyBinds(String configType) {
    String logMessagePrefix = configType + " KeyBinds";
    String configLoaderMethodName = "load" + configType + "KeyBindings";
    String configFinderMethodName = "find" + configType + "ConfigFile";

    log.info("Lade {}...", logMessagePrefix);
    try {
      String configPath =
          ConfigLoader.class.getDeclaredMethod(configFinderMethodName).invoke(null).toString();
      if (configPath != null) {
        ConfigLoader.class
            .getDeclaredMethod(configLoaderMethodName, String.class, KeyBindRepository.class)
            .invoke(null, configPath, keyBindRepository);
        log.info("{} erfolgreich geladen!", logMessagePrefix);
        return;
      }
    } catch (Exception e) {
      // Ignoriere Exception und fahre mit der Benutzereingabe fort
    }
  }

  public void loadDefaultKeyBinds() {
    loadKeyBinds("Default");
  }

  public void loadUserKeyBinds() {
    loadKeyBinds("User");
  }
}
