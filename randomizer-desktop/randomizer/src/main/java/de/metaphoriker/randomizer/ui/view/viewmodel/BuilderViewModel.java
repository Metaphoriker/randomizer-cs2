package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.repository.ActionRepository;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.config.keybind.KeyBindNameTypeMapper;
import de.metaphoriker.model.config.keybind.KeyBindType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public class BuilderViewModel {

  @Getter private final StringProperty currentActionSequenceProperty = new SimpleStringProperty();

  private final ActionRepository actionRepository;
  private final ActionSequenceRepository actionSequenceRepository;
  private final KeyBindNameTypeMapper keyBindNameTypeMapper;

  @Inject
  public BuilderViewModel(
      ActionRepository actionRepository,
      ActionSequenceRepository actionSequenceRepository,
      KeyBindNameTypeMapper keyBindNameTypeMapper) {
    this.actionRepository = actionRepository;
    this.actionSequenceRepository = actionSequenceRepository;
    this.keyBindNameTypeMapper = keyBindNameTypeMapper;
  }

  public Map<String, List<String>> getActionToTypeMap() {
    Map<String, List<String>> actionMap = new HashMap<>();

    actionRepository
        .getActions()
        .forEach(
            (action, _) -> {
              KeyBindType type = keyBindNameTypeMapper.getTypeByName(action.getName());
              if (type != null) {
                actionMap
                    .computeIfAbsent(type.name(), _ -> new ArrayList<>())
                    .add(action.getName());
              }
            });
    return actionMap;
  }

  public List<String> getActionSequences() {
    return actionSequenceRepository.getActionSequences().stream()
        .map(ActionSequence::getName)
        .toList();
  }

  public List<String> getActionsOfSequence(String sequenceName) {
    return actionSequenceRepository
        .getActionSequence(sequenceName)
        .orElseThrow(IllegalStateException::new)
        .getActions()
        .stream()
        .map(Action::getName)
        .toList();
  }
}
