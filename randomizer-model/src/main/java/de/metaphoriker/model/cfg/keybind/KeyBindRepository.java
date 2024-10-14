package de.metaphoriker.model.cfg.keybind;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyBindRepository {

  private static final String UNBOUND = "<unbound>";
  private final List<KeyBind> keyBinds = new ArrayList<>();
  private final KeyBindNameMapper keyBindNameMapper = new KeyBindNameMapper();
  private String defaultFilePath;
  private String userConfigFilePath;

  public void initDefaults(String filePath) {
    this.defaultFilePath = filePath;
    loadKeyBinds(filePath);
  }

  public void initModifiedKeyBinds(String filePath) {
    this.userConfigFilePath = filePath;
    loadKeyBinds(filePath);
  }

  private void loadKeyBinds(String filePath) {
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        processLine(scanner.nextLine().trim());
      }
    } catch (FileNotFoundException e) {
      log.error("Config file not found: {}", filePath);
    }
  }

  private void processLine(String line) {
    if (line.isEmpty() || line.startsWith("//")) {
      return;
    }
    if (line.contains("\"")) {
      String[] parts = line.split("\"");
      if (parts.length >= 4) {
        handleKeyBind(parts[1], parts[3]);
      }
    }
  }

  private void handleKeyBind(String key, String descriptor) {
    if (UNBOUND.equals(descriptor)) {
      keyBinds.removeIf(bind -> bind.getKey().equals(key));
    } else if (keyBindNameMapper.hasKey(descriptor)) {
      addOrUpdateKeyBind(key, keyBindNameMapper.getKeyName(descriptor));
    } else {
      log.debug("Key descriptor not found: {} - ignoring", descriptor);
    }
  }

  private void addOrUpdateKeyBind(String key, String actionName) {
    KeyBind newKeyBind = new KeyBind(key, actionName);
    for (int i = 0; i < keyBinds.size(); i++) {
      if (keyBinds.get(i).getKey().equals(key)) {
        keyBinds.set(i, newKeyBind);
        return;
      }
    }
    keyBinds.add(newKeyBind);
  }

  public void reloadBinds() {
    keyBinds.clear();
    if (defaultFilePath == null) {
      throw new IllegalStateException("KeyBindRepository not initialized");
    }
    initDefaults(defaultFilePath);
    if (userConfigFilePath != null) {
      initModifiedKeyBinds(userConfigFilePath);
    }
  }

  public List<KeyBind> getKeyBinds() {
    return new ArrayList<>(keyBinds);
  }
}
