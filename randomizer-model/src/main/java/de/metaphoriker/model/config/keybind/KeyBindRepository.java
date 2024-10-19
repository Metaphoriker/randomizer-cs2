package de.metaphoriker.model.config.keybind;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyBindRepository {

  private static final String UNBOUND = "<unbound>";
  private final List<KeyBind> keyBinds = new ArrayList<>();
  private final KeyBindNameTypeMapper keyBindNameTypeMapper = new KeyBindNameTypeMapper();

  private String defaultFilePath;
  private String userConfigFilePath;

  /**
   * Retrieves a KeyBind associated with the given key.
   *
   * @param key The key to search for in the key bindings.
   * @return An Optional containing the KeyBind if found, otherwise an empty Optional.
   */
  public Optional<KeyBind> getKeyBind(String key) {
    return keyBinds.stream().filter(bind -> bind.getKey().equals(key)).findFirst();
  }

  /**
   * Initializes the default key bindings from a specified configuration file.
   *
   * @param filePath the path to the configuration file containing the default key bindings
   */
  public void initDefaults(String filePath) {
    this.defaultFilePath = filePath;
    log.info("Initialisiere Standard-KeyBinds von Datei: {}", filePath);
    loadKeyBinds(filePath);
  }

  /**
   * Initializes modified key bindings from a specified configuration file.
   *
   * @param filePath the path to the configuration file containing the modified key bindings
   */
  public void initModifiedKeyBinds(String filePath) {
    this.userConfigFilePath = filePath;
    log.info("Initialisiere modifizierte KeyBinds von Datei: {}", filePath);
    loadKeyBinds(filePath);
  }

  private void loadKeyBinds(String filePath) {
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        processLine(line);
      }
    } catch (FileNotFoundException e) {
      log.error("Konfigurationsdatei nicht gefunden: {}", filePath, e);
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
    } else if (keyBindNameTypeMapper.hasKey(descriptor)) {
      addOrUpdateKeyBind(key, keyBindNameTypeMapper.getKeyName(descriptor));
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
      throw new IllegalStateException("KeyBindRepository wurde nicht initialisiert");
    }
    initDefaults(defaultFilePath);
    if (userConfigFilePath != null) {
      initModifiedKeyBinds(userConfigFilePath);
    }
    log.info("KeyBinds wurden erfolgreich neu geladen");
  }

  public List<KeyBind> getKeyBinds() {
    return new ArrayList<>(keyBinds);
  }
}
