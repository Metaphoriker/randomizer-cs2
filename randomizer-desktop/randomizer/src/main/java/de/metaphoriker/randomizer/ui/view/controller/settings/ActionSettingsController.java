package de.metaphoriker.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.component.MinMaxSlider;
import de.metaphoriker.randomizer.ui.view.viewmodel.ActionSettingsViewModel;
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
        actionSettingsViewModel.applyInterval();
    }

    private void initialize() {
        initializeMinMaxSlider();
        setupBindings();
    }

    private void initializeMinMaxSlider() {
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
                        });
    }

    public void bindOnVisibleProperty(Consumer<Boolean> consumer) {
        actionSettingsVBox.visibleProperty().addListener((_, _, newValue) -> consumer.accept(newValue));
    }

    public void setAction(Action action) {
        actionSettingsViewModel.getActionInFocusProperty().set(action);
    }
}
