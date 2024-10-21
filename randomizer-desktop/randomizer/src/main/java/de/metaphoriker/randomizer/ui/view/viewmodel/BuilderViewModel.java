package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.repository.ActionRepository;
import de.metaphoriker.model.action.repository.ActionSequenceRepository;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.action.value.Interval;
import de.metaphoriker.model.config.keybind.KeyBindNameTypeMapper;
import de.metaphoriker.model.config.keybind.KeyBindType;
import de.metaphoriker.model.persistence.dao.ActionSequenceDao;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;

public class BuilderViewModel {

  @Getter
  private final ObjectProperty<ActionSequence> currentActionSequenceProperty =
      new SimpleObjectProperty<>();

  @Getter private final ObjectProperty<Action> actionInFocusProperty = new SimpleObjectProperty<>();

  @Getter
  private final ListProperty<Action> currentActionsProperty =
      new SimpleListProperty<>(FXCollections.observableArrayList());

  @Getter private final StringProperty sequenceNameProperty = new SimpleStringProperty();
  @Getter private final StringProperty sequenceDescriptionProperty = new SimpleStringProperty();

  private final ObservableList<Action> actions = FXCollections.observableArrayList();

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

    bindActionSequenceChange();
    registerListsListener();
  }

  private final ListChangeListener<Action> LIST_CHANGE_LISTENER =
      change -> {
        while (change.next()) {
          if (change.wasAdded()) {
            actions.addAll(change.getAddedSubList());
          }
          if (change.wasRemoved()) {
            actions.removeAll(change.getRemoved());
          }
        }
      };

  private void bindActionSequenceChange() {
    currentActionSequenceProperty.addListener(
        (_, _, newSequence) -> {
          actions.clear();
          currentActionsProperty.clear();
          if (newSequence != null) {
            setActions(newSequence.getActions());
            sequenceNameProperty.set(newSequence.getName());
            sequenceDescriptionProperty.set(newSequence.getDescription());
          }
        });
  }

  public void openSequenceFolder() throws IOException {
    Desktop.getDesktop().open(ActionSequenceDao.ACTION_SEQUENCE_FOLDER);
  }

  private ActionSequence craftActionSequence() {
    ActionSequence actionSequence =
        new ActionSequence(currentActionSequenceProperty.get().getName());
    actionSequence.setActions(new ArrayList<>(actions));
    return actionSequence;
  }

  public void createNewActionSequence() {
    int sequenceCount = 1;
    String name = "Unnamed " + sequenceCount;
    while (actionSequenceRepository.getActionSequence(name).isPresent()) {
      sequenceCount++;
      name = "Unnamed " + sequenceCount;
    }

    ActionSequence actionSequence = new ActionSequence(name);
    actionSequenceRepository.saveActionSequence(actionSequence);
    currentActionSequenceProperty.set(actionSequence);
  }

  public void saveActionSequence() {
    ActionSequence actionSequence = craftActionSequence();
    actionSequenceRepository.saveActionSequence(actionSequence);
  }

  public void addRandomActions(int count) {
    List<Action> allActions = new ArrayList<>(actionRepository.getActions().keySet());
    for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, count); i++) {
      int randomIndex = (int) (Math.random() * allActions.size());
      Action randomAction = allActions.get(randomIndex);
      currentActionsProperty.add(randomAction);
    }
  }

  public void deleteActionSequence(ActionSequence sequence) {
    actionSequenceRepository
        .getActionSequence(sequence.getName())
        .ifPresent(actionSequenceRepository::deleteActionSequence);
  }

  private void registerListsListener() {
    currentActionsProperty.addListener(LIST_CHANGE_LISTENER);
  }

  public void addAction(Action action) {
    currentActionsProperty.add(action);
  }

  public void addActionAt(Action action, int index) {
    currentActionsProperty.add(index, action);
  }

  public void setActionInterval(Action action, int min, int max) throws IllegalArgumentException {
    if (min >= max) {
      throw new IllegalArgumentException("Min must be smaller than max");
    }

    actions.stream()
        .filter(a -> a.equals(action))
        .findFirst()
        .ifPresent(a -> a.setInterval(Interval.of(min, max)));
  }

  public void removeActionAt(int index) {
    currentActionsProperty.remove(index);
  }

  public void removeAction(Action action) {
    currentActionsProperty.remove(action);
  }

  public void setActions(List<Action> actions) {
    currentActionsProperty.setAll(actions);
  }

  public Map<KeyBindType, List<Action>> getActionToTypeMap() {
    Map<KeyBindType, List<Action>> actionMap = new HashMap<>();

    actionRepository
        .getActions()
        .forEach(
            (action, _) -> {
              KeyBindType type = keyBindNameTypeMapper.getTypeByName(action.getName());
              if (type != null) {
                actionMap.computeIfAbsent(type, _ -> new ArrayList<>()).add(action);
              }
            });
    return actionMap;
  }

  public List<ActionSequence> getActionSequences() {
    return actionSequenceRepository.getActionSequences();
  }

  public List<Action> getActionsOfSequence(ActionSequence sequence) {
    return actionSequenceRepository
        .getActionSequence(sequence.getName())
        .orElseThrow(IllegalStateException::new)
        .getActions();
  }
}
