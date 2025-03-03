package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.model.action.sequence.ActionSequence;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.component.MinMaxSlider;
import com.revortix.randomizer.ui.view.viewmodel.RandomizerViewModel;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@View
public class RandomizerViewController {

  private static final String ACTION_NAME_STYLING = "logbook-sequence-actions-name";
  private static final String START_ACTION_NAME_STYLING = "logbook-sequence-actions-name-start";
  private static final String MIDDLE_ACTION_NAME_STYLING = "logbook-sequence-actions-name-middle";
  private static final String END_ACTION_NAME_STYLING = "logbook-sequence-actions-name-end";
  private static final String START_ACTIVE_ACTION_NAME_STYLING =
      "logbook-sequence-actions-name-start-active";
  private static final String MIDDLE_ACTIVE_ACTION_NAME_STYLING =
      "logbook-sequence-actions-name-middle-active";
  private static final String END_ACTIVE_ACTION_NAME_STYLING =
      "logbook-sequence-actions-name-end-active";

  private final RandomizerViewModel randomizerViewModel;

  @FXML private Label sequenceNameLabel;
  @FXML private VBox actionsVBox;
  @FXML private MinMaxSlider minMaxSlider;
  @FXML private VBox historyVBox;
  @FXML private ProgressBar sequenceProgressIndicator;
  @FXML private ToggleButton randomizerToggleButton;

  @Inject
  public RandomizerViewController(RandomizerViewModel randomizerViewModel) {
    this.randomizerViewModel = randomizerViewModel;
  }

  @FXML
  void onToggle(ActionEvent event) {
    if (!randomizerToggleButton.isSelected()) {
      randomizerViewModel.setApplicationStateToStopped();
    } else {
      randomizerViewModel.setApplicationStateToRunning();
    }
  }

  @FXML
  void onIntervalApply(ActionEvent event) {
    randomizerViewModel.saveInterval();
  }

  @FXML
  private void initialize() {
    randomizerViewModel.initConfig();
    setupBindings();
    setupListener();
    setupProgressIndicator();
    setupIntervalSlider();
  }

  private void setupBindings() {
    sequenceNameLabel.visibleProperty().bind(sequenceNameLabel.textProperty().isNotEmpty());
  }

  private void setupProgressIndicator() {
    randomizerViewModel
        .getCurrentActionSequenceProgressProperty()
        .addListener(
            (_, _, t1) -> {
              Platform.runLater(
                  () -> {
                    int actionSize =
                        randomizerViewModel
                            .getCurrentActionSequenceProperty()
                            .get()
                            .getActions()
                            .size();
                    if (randomizerViewModel.getCurrentActionSequenceProperty().get() == null) {
                      sequenceProgressIndicator.setProgress(0.0);
                      return;
                    }
                    double progress = actionSize == 0 ? 0.0 : (t1.doubleValue() / actionSize);
                    progress = Math.min(1, progress);
                    sequenceProgressIndicator.setProgress(progress);
                  });
            });
  }

  private void setupIntervalSlider() {
    Platform.runLater(
        () ->
            minMaxSlider.setMinMaxValue(
                randomizerViewModel.getMinIntervalProperty().get(),
                randomizerViewModel.getMaxIntervalProperty().get()));

    minMaxSlider.getMinProperty().bindBidirectional(randomizerViewModel.getMinIntervalProperty());
    minMaxSlider.getMaxProperty().bindBidirectional(randomizerViewModel.getMaxIntervalProperty());
  }

  /** Creates the history container for the ActionSequence */
  private void createActionSequenceContainer(ActionSequence actionSequence) {
    HBox container = new HBox();
    container.getStyleClass().add("logbook-history-entry-container");
    Label actionSequenceNameLabel = new Label(actionSequence.getName());
    actionSequenceNameLabel.getStyleClass().add("logbook-history-entry-name");
    VBox rightbox = new VBox();
    rightbox.getStyleClass().add("logbook-history-entry-rightbox");
    Label actionSequenceActionCount = new Label(String.valueOf(actionSequence.getActions().size()));
    actionSequenceActionCount.getStyleClass().add("logbook-history-entry-action-count");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    Label actionSequenceActionExecutedAt = new Label(LocalTime.now().format(formatter));
    actionSequenceActionExecutedAt.getStyleClass().add("logbook-history-entry-executed-at");
    rightbox.getChildren().addAll(actionSequenceActionCount, actionSequenceActionExecutedAt);
    container.getChildren().addAll(actionSequenceNameLabel, rightbox);
    historyVBox.getChildren().addFirst(container);
  }

  private void setupListener() {
    randomizerViewModel.onActionSequenceFinished(
        actionSequence ->
            Platform.runLater(
                () -> {
                  createActionSequenceContainer(actionSequence);
                  sequenceNameLabel.setText("");
                  actionsVBox.getChildren().clear();
                }));
    randomizerViewModel
        .getCurrentActionSequenceProperty()
        .addListener(
            (_, _, sequence) -> {
              if (sequence == null) {
                return;
              }

              Platform.runLater(
                  () -> {
                    sequenceNameLabel.setText(sequence.getName());
                    actionsVBox.getChildren().clear();
                    sequence
                        .getActions()
                        .forEach(
                            action -> {
                              Label actionLabel = new Label(action.getName());
                              actionLabel.getStyleClass().add("logbook-sequence-actions-name");
                              actionsVBox.getChildren().add(actionLabel);
                            });
                    actionsVBox
                        .getChildren()
                        .forEach(label -> setPositionalStyling((Label) label, false));
                  });
            });

    randomizerViewModel.onActionFinished(
        action ->
            actionsVBox.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .filter(label -> label.getText().equals(action.getName()))
                .filter(label -> !isActive(label))
                .findFirst()
                .ifPresent(label -> setPositionalStyling(label, true)));
  }

  private boolean isActive(Label label) {
    return label.getStyleClass().stream().anyMatch(style -> style.endsWith("-active"));
  }

  private void setPositionalStyling(Label label, boolean active) {
    int index = actionsVBox.getChildren().indexOf(label);
    ObservableList<Node> children = actionsVBox.getChildren(); // Hole die Liste nur einmal

    if (children.isEmpty()) { // Überprüfe, ob die Liste leer ist
      // Optional: Logging, um zu verstehen, wann dies passiert
      // System.err.println("actionsVBox ist leer in setPositionalStyling!");
      return; // Beende die Methode, wenn die Liste leer ist.
    }

    if (index == 0) {
      label.getStyleClass().clear();
      label.getStyleClass().add(ACTION_NAME_STYLING);
      label
          .getStyleClass()
          .add(active ? START_ACTIVE_ACTION_NAME_STYLING : START_ACTION_NAME_STYLING);
      return;
    }

    if (index == children.size() - 1) {
      label.getStyleClass().clear();
      label.getStyleClass().add(ACTION_NAME_STYLING);
      label.getStyleClass().add(active ? END_ACTIVE_ACTION_NAME_STYLING : END_ACTION_NAME_STYLING);
      return;
    }

    if (index > 0 && index < children.size() - 1) { // Stelle sicher index ist im Rahmen.
      label.getStyleClass().clear();
      label.getStyleClass().add(ACTION_NAME_STYLING);
      label
          .getStyleClass()
          .add(active ? MIDDLE_ACTIVE_ACTION_NAME_STYLING : MIDDLE_ACTION_NAME_STYLING);
    } else {
      // Optional: Logge den Fehler, um zu verstehen, wann dies auftritt
      System.err.println(
          "Ungültiger Index in setPositionalStyling: "
              + index
              + ", Listengröße: "
              + children.size());
    }
  }
}
