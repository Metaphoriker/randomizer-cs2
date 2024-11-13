package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.model.action.Action;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.component.MinMaxSlider;
import com.revortix.randomizer.ui.view.viewmodel.ActionSettingsViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

@View
public class ActionSettingsController {

    private final ActionSettingsViewModel actionSettingsViewModel;

    @FXML
    private VBox actionSettingsVBox;
    @FXML
    private Label actionInFocusLabel;
    @FXML
    private MinMaxSlider minMaxSlider;

    @Inject
    public ActionSettingsController(ActionSettingsViewModel actionSettingsViewModel) {
        this.actionSettingsViewModel = actionSettingsViewModel;
        Platform.runLater(this::initialize);
    }

    @FXML
    void onApply(ActionEvent event) {
        if (minMaxSlider.isEven())
            return; // this should not be possible

        actionSettingsViewModel.applyInterval();
        actionSettingsViewModel.getActionInFocusProperty().set(null);
    }

    @FXML
    void onPanelClose(ActionEvent event) {
        setAction(null);
    }

    private void initialize() {
        initializeMinMaxSlider();
        setupBindings();
    }

    private void initializeMinMaxSlider() {
        minMaxSlider.setMinLowerValue(0);
        minMaxSlider.setMaxHigherValue(10);
        minMaxSlider.getMinProperty().bindBidirectional(actionSettingsViewModel.getMinIntervalProperty());
        minMaxSlider.getMaxProperty().bindBidirectional(actionSettingsViewModel.getMaxIntervalProperty());
    }

    private void setupBindings() {
        // this is important, since if there is no action to adjust,
        // we don't need this view
        actionSettingsVBox
                .visibleProperty()
                .bind(actionSettingsViewModel.getActionInFocusProperty().isNotNull());

        actionSettingsViewModel
                .getActionInFocusProperty()
                .addListener(
                        (_, _, newValue) -> {
                            if (newValue == null) return;
                            actionInFocusLabel.setText(newValue.getName());

                            /*
                             * even if it seems nonsensly to set this manually, since we already bound it bidirectional
                             * to each other, this is still needed, because otherwise the values are not correctly set whyever.
                             */
                            Platform.runLater(() -> {
                                minMaxSlider.setMinMaxValue(
                                        actionSettingsViewModel.getMinIntervalProperty().get(),
                                        actionSettingsViewModel.getMaxIntervalProperty().get()
                                );
                            });
                        });
    }

    public void bindOnVisibleProperty(Consumer<Boolean> consumer) {
        actionSettingsVBox.visibleProperty().addListener((_, _, newValue) -> consumer.accept(newValue));
    }

    public void setAction(Action action) {
        actionSettingsViewModel.getActionInFocusProperty().set(action);
    }
}
