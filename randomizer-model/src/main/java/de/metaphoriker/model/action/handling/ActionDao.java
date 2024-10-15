package de.metaphoriker.model.action.handling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Eine Klasse, die den Zustand von Aktionen speichert.
 *
 * <p>Der Zustand besteht aus der Aktion selbst und einer booleschen Flagge, die angibt, ob die
 * Aktion aktiviert ist oder nicht.
 */
@Slf4j
public class ActionDao {

  private static final File STORAGE_FILE =
      new File(ApplicationContext.getAppdataFolder() + File.separator + "actions.json");
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public Map<String, Boolean> load() {
    if (!STORAGE_FILE.exists()) {
      log.warn("Speicherdatei existiert nicht: {}", STORAGE_FILE.getAbsolutePath());
      return new HashMap<>();
    }

    try {
      return OBJECT_MAPPER.readValue(STORAGE_FILE, new TypeReference<>() {});
    } catch (IOException e) {
      log.error(
          "Fehler beim Laden der Zustände der Aktionen aus Datei: {}",
          STORAGE_FILE.getAbsolutePath(),
          e);
      throw new RuntimeException("Fehler beim Laden der Aktionen aus Datei", e);
    }
  }

  public void save(Map<Action, Boolean> actions) {
    Map<String, Boolean> actionStates = new HashMap<>();
    actions.forEach((action, enabled) -> actionStates.put(action.name(), enabled));

    try {
      OBJECT_MAPPER.writeValue(STORAGE_FILE, actionStates);
      log.info(
          "Zustände der Aktionen erfolgreich gespeichert in Datei: {}",
          STORAGE_FILE.getAbsolutePath());
    } catch (IOException e) {
      log.error(
          "Fehler beim Speichern der Zustände der Aktionen in Datei: {}",
          STORAGE_FILE.getAbsolutePath(),
          e);
      throw new RuntimeException("Fehler beim Speichern der Aktionen in Datei", e);
    }
  }

  public void loadStates(Map<Action, Boolean> actions) {
    Map<String, Boolean> loadedActions = load();
    actions
        .keySet()
        .forEach(
            action -> {
              Boolean enabled = loadedActions.get(action.name());
              if (enabled != null) {
                actions.put(action, enabled);
              }
            });
    log.info("Zustände der Aktionen erfolgreich geladen und angewendet");
  }
}
