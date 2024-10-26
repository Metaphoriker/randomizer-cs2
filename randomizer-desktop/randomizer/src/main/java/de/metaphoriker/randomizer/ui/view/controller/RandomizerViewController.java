package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.RandomizerViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

@View
public class RandomizerViewController {

  private final RandomizerViewModel randomizerViewModel;

  @FXML private Label sequenceNameLabel;
  @FXML private VBox actionsVBox;

  @Inject
  public RandomizerViewController(RandomizerViewModel randomizerViewModel) {
    this.randomizerViewModel = randomizerViewModel;
    Platform.runLater(this::initialize);
  }

  @FXML
  void onRun(ActionEvent event) {
    randomizerViewModel.setApplicationStateToRunning();
  }

  @FXML
  void onStop(ActionEvent event) {
    randomizerViewModel.setApplicationStateToStopped();
  }

  private void initialize() {
    setupListener();
  }

  private void setupListener() {
    randomizerViewModel
        .getCurrentActionSequenceProperty()
        .addListener(
            (_, _, sequence) -> {
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
            });

    randomizerViewModel
        .getCurrentActionProperty()
        .addListener(
            (_, _, action) -> {
              // TODO: set current action as running
            });

    randomizerViewModel
        .getWaitingProperty()
        .addListener(
            (_, _, isWaiting) -> {
              if (isWaiting) {
                sequenceNameLabel.setText("");
                actionsVBox.getChildren().clear();
              }
            });
  }
}
