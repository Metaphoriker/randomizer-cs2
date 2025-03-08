package com.revortix.randomizer.bootstrap;

import com.google.inject.Inject;
import com.revortix.model.config.ConfigLoader;
import com.revortix.model.config.keybind.KeyBindRepository;
import com.revortix.randomizer.config.RandomizerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerConfigLoader {

  private final RandomizerConfig randomizerConfig;
  private final KeyBindRepository keyBindRepository;

  @Inject
  public RandomizerConfigLoader(
      RandomizerConfig randomizerConfig, KeyBindRepository keyBindRepository) {
    this.randomizerConfig = randomizerConfig;
    this.keyBindRepository = keyBindRepository;
  }

  public void ladeDefaultKeyBinds() {
    log.info("Lade KeyBinds...");
    try {
      String configPath = ConfigLoader.findDefaultConfigFile();
      if (configPath != null) {
        ConfigLoader.loadDefaultKeyBinds(configPath, keyBindRepository);
        log.info("Default KeyBinds erfolgreich geladen!");
      }
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Laden der Default KeyBinds", e);
    }
  }

  public void ladeUserKeyBinds() {
    log.info("Lade User KeyBinds...");
    try {
      ConfigLoader.loadUserKeyBindings(getUserConfigPath(), keyBindRepository);
      log.info("User KeyBinds erfolgreich geladen!");
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Laden der User KeyBinds", e);
    }
  }

  public String getUserConfigPath() {
    return randomizerConfig.getConfigPath();
  }

  public String ladeUserConfigPath() {
    return ConfigLoader.findUserConfigFile();
  }
}
