package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.component.MinMaxSlider;
import com.revortix.randomizer.ui.view.viewmodel.settings.GeneralSettingsViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

@View
public class GeneralSettingsController {

  private final GeneralSettingsViewModel generalSettingsViewModel;

  @FXML private ToggleButton showIntroToggleButton;
  @FXML private Button syncConfigButton;
  @FXML private TextField configPathTextField;
  @FXML private MinMaxSlider minMaxSlider;
  @FXML private Label syncFailedIndicator;
  @FXML private Label spentTimeLabel;

  @Inject
  public GeneralSettingsController(GeneralSettingsViewModel generalSettingsViewModel) {
    this.generalSettingsViewModel = generalSettingsViewModel;
  }

  @FXML
  private void initialize() {
    initializeSpentTimeLabel();
    generalSettingsViewModel.setupViewModel();
    setupSettingsOptions();
    setupIntervalSlider();
  }

  private void initializeSpentTimeLabel() {
    generalSettingsViewModel
        .getSpentTimeProperty()
        .addListener(
            (_, _, newValue) -> spentTimeLabel.setText(longInHours(newValue.longValue()) + "h"));
  }

  private int longInHours(long timestamp) {
    return (int) (timestamp / 3600000);
  }

  private void setupSettingsOptions() {
    configPathTextField
        .textProperty()
        .bindBidirectional(generalSettingsViewModel.getConfigPathProperty());
    configPathTextField.setText(generalSettingsViewModel.getConfigPath());
    syncConfigButton.setTooltip(new Tooltip("Sync"));
    showIntroToggleButton
        .selectedProperty()
        .bindBidirectional(generalSettingsViewModel.getShowIntroProperty());
  }

  private void setupIntervalSlider() {
    Platform.runLater(
        () ->
            minMaxSlider.setMinMaxValue(
                generalSettingsViewModel.getMinIntervalProperty().get(),
                generalSettingsViewModel.getMaxIntervalProperty().get()));

    minMaxSlider
        .getMinProperty()
        .bindBidirectional(generalSettingsViewModel.getMinIntervalProperty());
    minMaxSlider
        .getMaxProperty()
        .bindBidirectional(generalSettingsViewModel.getMaxIntervalProperty());
  }

  @FXML
  private void onConfigSync(ActionEvent event) {
    generalSettingsViewModel
        .loadConfigs()
        .thenRunAsync(
            () -> {
              syncConfigButton.getStyleClass().add("sync-config-path-success");
              syncFailedIndicator.setVisible(false);
            },
            Platform::runLater)
        .exceptionallyAsync(
            e -> {
              syncConfigButton.getStyleClass().add("sync-config-path-failed");
              syncFailedIndicator.setVisible(true);
              return null;
            },
            Platform::runLater);
  }
}
