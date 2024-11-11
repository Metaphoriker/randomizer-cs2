package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.component.MinMaxSlider;
import com.revortix.randomizer.ui.view.viewmodel.RandomizerViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

@View
public class RandomizerViewController {

    private final RandomizerViewModel randomizerViewModel;

    @FXML
    private Label sequenceNameLabel;
    @FXML
    private VBox actionsVBox;
    @FXML
    private MinMaxSlider minMaxSlider;

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

    @FXML
    void onIntervalApply(ActionEvent event) {
        randomizerViewModel.saveInterval();
    }

    private void initialize() {
        randomizerViewModel.initConfig();
        setupListener();
        setupIntervalSlider();
    }

    private void setupIntervalSlider() {
        Platform.runLater(() -> minMaxSlider.setMinMaxValue(
                randomizerViewModel.getMinIntervalProperty().get(),
                randomizerViewModel.getMaxIntervalProperty().get()
        ));

        minMaxSlider.getMinProperty().bindBidirectional(randomizerViewModel.getMinIntervalProperty());
        minMaxSlider.getMaxProperty().bindBidirectional(randomizerViewModel.getMaxIntervalProperty());
    }

    private void setupListener() {
        randomizerViewModel
                .getCurrentActionSequenceProperty()
                .addListener(
                        (_, _, sequence) -> {
                            if (sequence == null) return;
                            Platform.runLater(() -> {
                                sequenceNameLabel.setText(sequence.getName());
                                actionsVBox.getChildren().clear();
                                sequence
                                        .getActions()
                                        .forEach(
                                                action -> {
                                                    Label actionLabel = new Label(action.getName());
                                                    actionLabel.getStyleClass().add("logbook-sequence-actions-name-start");
                                                    actionsVBox.getChildren().add(actionLabel);
                                                });
                            });
                        });

        randomizerViewModel
                .getCurrentActionProperty()
                .addListener(
                        (_, _, action) -> {
                            // TODO: set current action as running - visually
                        });

        randomizerViewModel
                .getWaitingProperty()
                .addListener(
                        (_, _, isWaiting) -> {
                            if (isWaiting) {
                                Platform.runLater(() -> {
                                    sequenceNameLabel.setText("");
                                    actionsVBox.getChildren().clear();
                                });
                            }
                        });
    }
}
