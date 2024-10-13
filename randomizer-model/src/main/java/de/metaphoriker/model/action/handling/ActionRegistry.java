package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActionRegistry {

  private static final ActionRegistryStorage ACTION_REGISTRY_STORAGE = new ActionRegistryStorage();

  private final Map<Action, Boolean> actions = new LinkedHashMap<>();

  public void register(Action action) {
    actions.put(action, true);
    ACTION_REGISTRY_STORAGE.save(actions);
  }

  public void unregister(Action action) {
    actions.remove(action);
  }

  public void enable(Action action) {
    actions.put(action, true);
    ACTION_REGISTRY_STORAGE.save(actions);
  }

  public void loadStatesIfExist() {
    ACTION_REGISTRY_STORAGE.loadStates(actions);
  }

  public void disable(Action action) {
    actions.put(action, false);
    ACTION_REGISTRY_STORAGE.save(actions);
  }

  public Map<Action, Boolean> getActions() {
    return new LinkedHashMap<>(actions);
  }

  public Action getByName(String actionName) {
    return actions.keySet().stream()
        .filter(action -> action.name().equals(actionName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionName));
  }
}
