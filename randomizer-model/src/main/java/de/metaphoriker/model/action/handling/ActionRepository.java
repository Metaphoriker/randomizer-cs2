package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ActionRepository {

  private static final ActionDao ACTION_REGISTRY_STORAGE = new ActionDao();

  private final Map<Action, Boolean> actions = new LinkedHashMap<>();

  public void register(Action action) {
    log.debug("Registriere Action: {}", action);
    actions.put(action, true);
    ACTION_REGISTRY_STORAGE.save(actions);
    log.info("Action erfolgreich registriert: {}", action);
  }

  public void unregister(Action action) {
    log.debug("Entferne Registrierung der Action: {}", action);
    actions.remove(action);
    log.info("Action erfolgreich abgemeldet: {}", action);
  }

  public void enable(Action action) {
    log.debug("Aktiviere Action: {}", action);
    actions.put(action, true);
    ACTION_REGISTRY_STORAGE.save(actions);
    log.info("Action erfolgreich aktiviert: {}", action);
  }

  public void loadStatesIfExist() {
    log.debug("Lade gespeicherte Zustände der Actions, falls vorhanden");
    ACTION_REGISTRY_STORAGE.loadStates(actions);
    log.info("Zustände der Actions erfolgreich geladen");
  }

  public void disable(Action action) {
    log.debug("Deaktiviere Action: {}", action);
    actions.put(action, false);
    ACTION_REGISTRY_STORAGE.save(actions);
    log.info("Action erfolgreich deaktiviert: {}", action);
  }

  public boolean isEnabled(Action action) {
    boolean enabled = actions.getOrDefault(action, true);
    log.debug("Prüfe ob Action aktiviert ist: {}, Ergebnis: {}", action, enabled);
    return enabled;
  }

  public Map<Action, Boolean> getActions() {
    log.debug("Gebe Kopie der aktuellen Actions-Liste zurück");
    return new LinkedHashMap<>(actions);
  }

  public Action getByName(String actionName) {
    log.debug("Suche Action nach Name: {}", actionName);
    return actions.keySet().stream()
        .filter(action -> action.name().equals(actionName))
        .findFirst()
        .orElseThrow(
            () -> {
              log.error("Action nicht gefunden: {}", actionName);
              return new IllegalArgumentException("Action nicht gefunden: " + actionName);
            });
  }
}
