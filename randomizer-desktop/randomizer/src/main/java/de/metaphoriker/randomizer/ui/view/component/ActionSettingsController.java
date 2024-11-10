package de.metaphoriker.randomizer.ui.view.component;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.ActionSettingsViewModel;
import javafx.application.Platform;
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
    private VBox timeFrameVBox;
    @FXML
    private Label actionInFocusLabel;

    @Inject
    public ActionSettingsController(ActionSettingsViewModel actionSettingsViewModel) {
        this.actionSettingsViewModel = actionSettingsViewModel;
        Platform.runLater(this::initialize);
    }

    private void initialize() {
        initializeMinMaxSlider();
        setupBindings();
    }

    private void initializeMinMaxSlider() {
        MinMaxSlider minMaxSlider = new MinMaxSlider();
        minMaxSlider.getMinProperty().bindBidirectional(actionSettingsViewModel.getMinIntervalProperty());
        minMaxSlider.getMaxProperty().bindBidirectional(actionSettingsViewModel.getMaxIntervalProperty());
        timeFrameVBox.getChildren().add(minMaxSlider);
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
