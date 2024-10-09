package de.metaphoriker.model.action.handling;

import de.metaphoriker.model.action.Action;

import java.util.HashSet;
import java.util.Set;

public class ActionRegistry {

  private final Set<Action> actions = new HashSet<>();

  public void register(Action action) {
    actions.add(action);
  }

  public void unregister(Action action) {
    actions.remove(action);
  }

  public Set<Action> getActions() {
    return new HashSet<>(actions);
  }

  public Action getByName(String actionName) {
    return actions.stream().filter(action -> actionName.equals(action.name())).findFirst().orElse(null);
  }
}
