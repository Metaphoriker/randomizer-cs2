package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.settings.GeneralSettingsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

@View
public class GeneralSettingsController {

  private final GeneralSettingsViewModel generalSettingsViewModel;

  @FXML private ToggleButton showIntroToggleButton;
  @FXML private Button syncConfigButton;
  @FXML private TextField configPathTextField;

  @Inject
  public GeneralSettingsController(GeneralSettingsViewModel generalSettingsViewModel) {
    this.generalSettingsViewModel = generalSettingsViewModel;
  }

  @FXML
  private void initialize() {
    configPathTextField.setText(generalSettingsViewModel.getConfigPath());
    syncConfigButton.setTooltip(new Tooltip("Sync"));
    showIntroToggleButton
        .selectedProperty()
        .bindBidirectional(generalSettingsViewModel.showIntroProperty());
  }
}
