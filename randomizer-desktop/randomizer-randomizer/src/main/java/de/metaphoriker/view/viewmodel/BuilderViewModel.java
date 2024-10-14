package de.metaphoriker.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.handling.ActionRegistry;
import java.util.List;

public class BuilderViewModel {

  @Inject private ActionRegistry actionRegistry;

  @Inject
  public BuilderViewModel() {}

  public List<Action> getActions() {
    return actionRegistry.getActions().keySet().stream()
        .filter(action -> actionRegistry.isEnabled(action))
        .toList();
  }
}
