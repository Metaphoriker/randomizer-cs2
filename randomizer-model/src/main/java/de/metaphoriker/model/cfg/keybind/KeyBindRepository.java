package de.metaphoriker.model.cfg.keybind;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyBindRepository {

  private final List<KeyBind> keyBinds = new ArrayList<>();
  private final KeyBindNameMapper keyBindNameMapper = new KeyBindNameMapper();

  private String defaultFilePath;
  private String userConfigFilePath;

  public void initDefaults(String filePath) {
    this.defaultFilePath = filePath;
    loadKeyBindsFromFile(filePath);
  }

  public void initModifiedKeyBinds(String filePath) {
    this.userConfigFilePath = filePath;
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();

        if (line.isEmpty() || line.startsWith("//")) {
          continue;
        }

        if (line.contains("\"")) {
          String[] parts = line.split("\"");
          if (parts.length >= 4) {
            String key = parts[1];
            String descriptor = parts[3];

            if (descriptor.equals("<unbound>")) {
              keyBinds.removeIf(bind -> bind.getKey().equals(key));
            } else {
              if (!keyBindNameMapper.hasKey(descriptor)) {
                log.info("Key descriptor not found: {} - ignoring", descriptor);
                continue;
              }

              String actionName = keyBindNameMapper.getKeyName(descriptor);
              KeyBind newKeyBind = new KeyBind(key, actionName);

              boolean updated = false;
              for (int i = 0; i < keyBinds.size(); i++) {
                if (keyBinds.get(i).getKey().equals(key)) {
                  keyBinds.set(i, newKeyBind);
                  updated = true;
                  break;
                }
              }

              if (!updated) {
                keyBinds.add(newKeyBind);
              }
            }
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("Config file not found: " + filePath);
    }
  }

  public void reloadBinds() {
    keyBinds.clear();

    if (defaultFilePath == null)
      throw new IllegalStateException("KeyBindRepository not initialized");

    initDefaults(defaultFilePath);

    if (userConfigFilePath != null) {
      initModifiedKeyBinds(userConfigFilePath);
    }
  }

  private void loadKeyBindsFromFile(String filePath) {
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();

        if (line.isEmpty() || line.startsWith("//")) {
          continue;
        }

        if (line.contains("\"")) {
          String[] parts = line.split("\"");
          if (parts.length >= 4) {
            String key = parts[1];
            String descriptor = parts[3];

            if (!keyBindNameMapper.hasKey(descriptor)) {
              log.info("Key descriptor not found: {} - ignoring", descriptor);
              continue;
            }

            String actionName = keyBindNameMapper.getKeyName(descriptor);
            keyBinds.add(new KeyBind(key, actionName));
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("Config file not found: " + filePath);
    }
  }

  public List<KeyBind> getKeyBinds() {
    return new ArrayList<>(keyBinds);
  }
}
