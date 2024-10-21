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
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;

public class BuilderViewModel {

  @Getter private final StringProperty currentActionSequenceProperty = new SimpleStringProperty();

  @Getter
  private final ListProperty<String> currentActionsProperty =
      new SimpleListProperty<>(FXCollections.observableArrayList());

  @Getter
  private final StringProperty currentActionSequenceDescriptionProperty =
      new SimpleStringProperty();

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

  private void bindActionSequenceChange() {
    currentActionSequenceProperty.addListener(
        (_, _, newSequence) -> {
          actions.clear();
          currentActionsProperty.clear();
          currentActionSequenceDescriptionProperty.set("");

          ActionSequence actionSequence =
              actionSequenceRepository.getActionSequence(newSequence).orElse(null);
          if (actionSequence != null) {
            setActions(actionSequence.getActions().stream().map(Action::getName).toList());
            currentActionSequenceDescriptionProperty.set(actionSequence.getDescription());
          }
        });
  }

  private ActionSequence craftActionSequence() {
    ActionSequence actionSequence = new ActionSequence(currentActionSequenceProperty.get());
    actionSequence.setActions(actions);
    actionSequence.setDescription(currentActionSequenceDescriptionProperty.get());
    return actionSequence;
  }

  public void openSequenceFolder() throws IOException {
    Desktop.getDesktop().open(ActionSequenceDao.ACTION_SEQUENCE_FOLDER);
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
    currentActionSequenceProperty.set(name);
  }

  public void addRandomActions(int count) {
    Set<Action> allActions = actionRepository.getActions().keySet();
    for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, count); i++) {
      int randomIndex = (int) (Math.random() * allActions.size());
      String randomAction = allActions.toArray(new Action[0])[randomIndex].getName();
      currentActionsProperty.add(randomAction);
    }
  }

  public void saveActionSequence() {
    ActionSequence actionSequence = craftActionSequence();
    actionSequenceRepository.saveActionSequence(actionSequence);
  }

  public void deleteActionSequence(String name) {
    actionSequenceRepository
        .getActionSequence(name)
        .ifPresent(actionSequenceRepository::deleteActionSequence);
  }

  private void registerListsListener() {
    currentActionsProperty.addListener(
        (ListChangeListener<String>)
            change -> {
              while (change.next()) {
                if (change.wasAdded()) {
                  for (String addedItem : change.getAddedSubList()) {
                    actions.add(actionRepository.getByName(addedItem));
                  }
                }
                if (change.wasRemoved()) {
                  for (String removedItem : change.getRemoved()) {
                    actions.removeIf(action -> action.getName().equals(removedItem));
                  }
                }
              }
            });
  }

  public void addAction(String action) {
    currentActionsProperty.add(action);
  }

  public void addActionAt(String action, int index) {
    currentActionsProperty.add(index, action);
  }

  public void setActionInterval(String action, int min, int max) throws IllegalArgumentException {
    if (min >= max) {
      throw new IllegalArgumentException("Min must be smaller than max");
    }

    actions.stream()
        .filter(a -> a.getName().equals(action))
        .findFirst()
        .ifPresent(a -> a.setInterval(Interval.of(min, max)));
  }

  public void removeActionAt(int index) {
    currentActionsProperty.remove(index);
  }

  public void removeAction(String action) {
    currentActionsProperty.remove(action);
  }

  public void setActions(List<String> actions) {
    currentActionsProperty.setAll(actions);
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
