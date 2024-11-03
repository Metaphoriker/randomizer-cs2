package de.metaphoriker.randomizer.ui.view.component;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.ActionSettingsViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    private Slider maxSlider;
    @FXML
    private Label maxSliderLabel;
    @FXML
    private Slider minSlider;
    @FXML
    private Label minSliderLabel;

    @Inject
    public ActionSettingsController(ActionSettingsViewModel actionSettingsViewModel) {
        this.actionSettingsViewModel = actionSettingsViewModel;
        Platform.runLater(this::initialize);
    }

    private void initialize() {
        setupBindings();
    }

    private void setupBindings() {
        minSlider.setValue(actionSettingsViewModel.getMinIntervalProperty().get());
        maxSlider.setValue(actionSettingsViewModel.getMaxIntervalProperty().get());

        minSlider.valueProperty().bindBidirectional(actionSettingsViewModel.getMinIntervalProperty());
        maxSlider.valueProperty().bindBidirectional(actionSettingsViewModel.getMaxIntervalProperty());

        minSlider
                .valueProperty()
                .addListener(
                        (_, _, newValue) -> {
                            int minValue = newValue.intValue();
                            int maxValue = actionSettingsViewModel.getMaxIntervalProperty().get();

                            if (minValue > maxValue) {
                                actionSettingsViewModel.getMaxIntervalProperty().set(Math.max(minValue + 1, maxValue));
                            }

                            minSliderLabel.setText(minValue + " ms");
                        });

        maxSlider
                .valueProperty()
                .addListener(
                        (_, _, newValue) -> {
                            int maxValue = newValue.intValue();
                            int minValue = actionSettingsViewModel.getMinIntervalProperty().get();

                            if (maxValue < minValue) {
                                actionSettingsViewModel.getMinIntervalProperty().set(Math.min(maxValue - 1, minValue));
                            }

                            maxSliderLabel.setText(maxValue + " ms");
                        });

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
