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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import lombok.Getter;

public class BuilderViewModel {

  private static final String SEQUENCE_CREATION_NAME_PREFIX = "Unnamed";

  @Getter
  private final ObjectProperty<ActionSequence> currentActionSequenceProperty =
      new SimpleObjectProperty<>();

  @Getter private final ObjectProperty<Action> actionInFocusProperty = new SimpleObjectProperty<>();

  @Getter
  private final ListProperty<Action> currentActionsProperty =
      new SimpleListProperty<>(FXCollections.observableArrayList());

  @Getter private final StringProperty sequenceNameProperty = new SimpleStringProperty();
  @Getter private final StringProperty sequenceDescriptionProperty = new SimpleStringProperty();
  @Getter private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
  @Getter private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

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
    setupListeners();
  }

  private void setupListeners() {
    setupActionSequenceListener();
    setupMinAndMaxIntervalListeners();
    setupActionInFocusListener();
  }

  private void setupActionSequenceListener() {
    currentActionSequenceProperty.addListener(
        (_, _, newSequence) -> {
          currentActionsProperty.clear();
          actionInFocusProperty.set(null);
          if (newSequence != null) {
            setActions(newSequence.getActions());
            sequenceNameProperty.set(newSequence.getName());
            sequenceDescriptionProperty.set(newSequence.getDescription());
          } else {
            sequenceNameProperty.set("");
            sequenceDescriptionProperty.set("");
          }
        });
  }

  private void setupActionInFocusListener() {
    actionInFocusProperty.addListener(
        (_, oldAction, newAction) -> {
          if (oldAction != null) {
            oldAction.setInterval(
                Interval.of(minIntervalProperty.get(), maxIntervalProperty.get()));
          }
          if (newAction != null) {
            minIntervalProperty.set(newAction.getInterval().getMin());
            maxIntervalProperty.set(newAction.getInterval().getMax());
          }
        });
  }

  private void setupMinAndMaxIntervalListeners() {
    minIntervalProperty.addListener(
        (_, _, newValue) -> updateIntervalProperty(newValue.intValue(), true));
    maxIntervalProperty.addListener(
        (_, _, newValue) -> updateIntervalProperty(newValue.intValue(), false));
  }

  private void updateIntervalProperty(int newValue, boolean isMin) {
    Action currentAction = actionInFocusProperty.get();
    if (currentAction != null) {
      Interval interval = currentAction.getInterval();
      if (isMin) {
        interval.setMin(newValue);
      } else {
        interval.setMax(newValue);
      }
    }
  }

  public void openSequenceFolder() throws IOException {
    Desktop.getDesktop().open(ActionSequenceDao.ACTION_SEQUENCE_FOLDER);
  }

  private ActionSequence craftActionSequence() {
    ActionSequence actionSequence = new ActionSequence(sequenceNameProperty.get());
    actionSequence.setActions(new ArrayList<>(currentActionsProperty.get()));
    actionSequence.setDescription(sequenceDescriptionProperty.get());
    return actionSequence;
  }

  public void createNewActionSequence() {
    String name = generateUniqueSequenceName();
    ActionSequence actionSequence = new ActionSequence(name);
    actionSequenceRepository.saveActionSequence(actionSequence);
    currentActionSequenceProperty.set(actionSequence);
  }

  private String generateUniqueSequenceName() {
    int sequenceCount = 1;
    String name = SEQUENCE_CREATION_NAME_PREFIX + sequenceCount;
    while (actionSequenceRepository.getActionSequence(name).isPresent()) {
      sequenceCount++;
      name = SEQUENCE_CREATION_NAME_PREFIX + sequenceCount;
    }
    return name;
  }

  public void saveActionSequence() {
    ActionSequence actionSequence = craftActionSequence();
    actionSequenceRepository.saveActionSequence(actionSequence);
  }

  public void addRandomActions(int count) {
    List<Action> allActions = new ArrayList<>(actionRepository.getActions().keySet());
    int randomCount = ThreadLocalRandom.current().nextInt(1, count);
    for (int i = 0; i < randomCount; i++) {
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

  public void addAction(Action action) {
    currentActionsProperty.add(action);
  }

  public void addActionAt(Action action, int index) {
    currentActionsProperty.add(index, action);
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
