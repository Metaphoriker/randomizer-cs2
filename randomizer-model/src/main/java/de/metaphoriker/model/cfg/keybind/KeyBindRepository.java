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
    log.debug("Initialisiere Standard-KeyBinds von Datei: {}", filePath);
    loadKeyBinds(filePath);
  }

  public void initModifiedKeyBinds(String filePath) {
    this.userConfigFilePath = filePath;
    log.debug("Initialisiere modifizierte KeyBinds von Datei: {}", filePath);
    loadKeyBinds(filePath);
  }

  private void loadKeyBinds(String filePath) {
    log.debug("Lade KeyBinds von Datei: {}", filePath);
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        log.debug("Verarbeite Zeile: {}", line);
        processLine(line);
      }
    } catch (FileNotFoundException e) {
      log.error("Konfigurationsdatei nicht gefunden: {}", filePath, e);
    }
  }

  private void processLine(String line) {
    if (line.isEmpty() || line.startsWith("//")) {
      log.debug("Zeile übersprungen: {}", line);
      return;
    }
    if (line.contains("\"")) {
      String[] parts = line.split("\"");
      if (parts.length >= 4) {
        log.debug("Handle KeyBind: Schlüssel = {}, Beschreibung = {}", parts[1], parts[3]);
        handleKeyBind(parts[1], parts[3]);
      }
    }
  }

  private void handleKeyBind(String key, String descriptor) {
    if (UNBOUND.equals(descriptor)) {
      log.debug("KeyBind für Schlüssel {} als ungebunden markiert und entfernt", key);
      keyBinds.removeIf(bind -> bind.getKey().equals(key));
    } else if (keyBindNameMapper.hasKey(descriptor)) {
      log.debug(
          "Füge oder aktualisiere KeyBind: Schlüssel = {}, Aktion = {}",
          key,
          keyBindNameMapper.getKeyName(descriptor));
      addOrUpdateKeyBind(key, keyBindNameMapper.getKeyName(descriptor));
    } else {
      log.debug("Key-Beschreibung nicht gefunden: {} - wird ignoriert", descriptor);
    }
  }

  private void addOrUpdateKeyBind(String key, String actionName) {
    KeyBind newKeyBind = new KeyBind(key, actionName);
    for (int i = 0; i < keyBinds.size(); i++) {
      if (keyBinds.get(i).getKey().equals(key)) {
        keyBinds.set(i, newKeyBind);
        log.debug("KeyBind aktualisiert: {}", newKeyBind);
        return;
      }
    }
    keyBinds.add(newKeyBind);
    log.debug("KeyBind hinzugefügt: {}", newKeyBind);
  }

  public void reloadBinds() {
    keyBinds.clear();
    if (defaultFilePath == null) {
      throw new IllegalStateException("KeyBindRepository wurde nicht initialisiert");
    }
    log.debug("Lade KeyBinds neu");
    initDefaults(defaultFilePath);
    if (userConfigFilePath != null) {
      initModifiedKeyBinds(userConfigFilePath);
    }
  }

  public List<KeyBind> getKeyBinds() {
    log.debug("Gebe Kopie der aktuellen KeyBinds-Liste zurück");
    return new ArrayList<>(keyBinds);
  }
}
