package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionRepository {

  private static final ActionDao ACTION_REGISTRY_STORAGE = new ActionDao();

  private final Map<Action, Boolean> actions = new LinkedHashMap<>();

  public void register(Action action) {
    actions.put(action, true);
    ACTION_REGISTRY_STORAGE.save(actions);
    log.info("Action erfolgreich registriert: {}", action);
  }

  public void unregister(Action action) {
    actions.remove(action);
    log.info("Action erfolgreich abgemeldet: {}", action);
  }

  public void enable(Action action) {
    actions.put(action, true);
    ACTION_REGISTRY_STORAGE.save(actions);
    log.info("Action erfolgreich aktiviert: {}", action);
  }

  public void loadStatesIfExist() {
    ACTION_REGISTRY_STORAGE.loadStates(actions);
    log.info("Zustände der Actions erfolgreich geladen");
  }

  public void disable(Action action) {
    actions.put(action, false);
    ACTION_REGISTRY_STORAGE.save(actions);
    log.info("Action erfolgreich deaktiviert: {}", action);
  }

  public boolean isEnabled(Action action) {
    return actions.getOrDefault(action, true);
  }

  public Map<Action, Boolean> getActions() {
    return new LinkedHashMap<>(actions);
  }

  public Action getByName(String actionName) {
    return actions.keySet().stream()
        .filter(action -> action.name().equals(actionName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Action nicht gefunden: " + actionName));
  }
}
