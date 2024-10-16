package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/** Haelt die Grundinstanzen der Aktionen mit den jeweils zugewiesenen Keybinds. */
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
    Map<Action, Boolean> actions = new LinkedHashMap<>();
    this.actions.forEach(
        (action, enabled) -> {
          try {
            actions.put(action.clone(), enabled);
          } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
          }
        });
    return actions;
  }

  public Action getByName(String actionName) {
    Action originalAction =
        actions.keySet().stream()
            .filter(action -> action.getName().equals(actionName))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Action nicht gefunden: " + actionName));

    try {
      return originalAction.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Cloning not supported for action: " + actionName, e);
    }
  }
}
