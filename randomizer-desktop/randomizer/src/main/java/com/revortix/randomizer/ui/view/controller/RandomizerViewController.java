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

    private static final String ACTION_NAME_STYLING = "logbook-sequence-actions-name";
    private static final String START_ACTION_NAME_STYLING = "logbook-sequence-actions-name-start";
    private static final String MIDDLE_ACTION_NAME_STYLING = "logbook-sequence-actions-name-middle";
    private static final String END_ACTION_NAME_STYLING = "logbook-sequence-actions-name-end";
    private static final String START_ACTIVE_ACTION_NAME_STYLING = "logbook-sequence-actions-name-start-active";
    private static final String MIDDLE_ACTIVE_ACTION_NAME_STYLING = "logbook-sequence-actions-name-middle-active";
    private static final String END_ACTIVE_ACTION_NAME_STYLING = "logbook-sequence-actions-name-end-active";

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
                            if (sequence == null) {
                                Platform.runLater(() -> actionsVBox.getChildren().clear());
                                return;
                            }

                            Platform.runLater(() -> {
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
                                actionsVBox.getChildren().forEach(label -> setPositionalStyling((Label) label, false));
                            });
                        });

        randomizerViewModel
                .getCurrentActionProperty()
                .addListener(
                        (_, _, action) -> {
                            actionsVBox.getChildren().stream()
                                    .filter(Label.class::isInstance)
                                    .map(Label.class::cast)
                                    .filter(label -> label.getText().equals(action.getName()))
                                    .filter(label -> !isActive(label))
                                    .findFirst()
                                    .ifPresent(label -> setPositionalStyling(label, true));
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

    private boolean isActive(Label label) {
        return label.getStyleClass().stream().anyMatch(style -> style.endsWith("-active"));
    }

    private void setPositionalStyling(Label label, boolean active) {
        int index = actionsVBox.getChildren().indexOf(label);

        if (index == 0) {
            label.getStyleClass().clear();
            label.getStyleClass().add(ACTION_NAME_STYLING);
            label.getStyleClass().add(active ? START_ACTIVE_ACTION_NAME_STYLING : START_ACTION_NAME_STYLING);
            return;
        }

        if (index == actionsVBox.getChildren().size() - 1) {
            label.getStyleClass().clear();
            label.getStyleClass().add(ACTION_NAME_STYLING);
            label.getStyleClass().add(active ? END_ACTIVE_ACTION_NAME_STYLING : END_ACTION_NAME_STYLING);
            return;
        }

        label.getStyleClass().clear();
        label.getStyleClass().add(ACTION_NAME_STYLING);
        label.getStyleClass().add(active ? MIDDLE_ACTIVE_ACTION_NAME_STYLING : MIDDLE_ACTION_NAME_STYLING);
    }
}
