package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.settings.GeneralSettingsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

@View
public class GeneralSettingsController {

  private final GeneralSettingsViewModel generalSettingsViewModel;

  @FXML private ToggleButton showIntroToggleButton;

  @Inject
  public GeneralSettingsController(GeneralSettingsViewModel generalSettingsViewModel) {
    this.generalSettingsViewModel = generalSettingsViewModel;
  }

  @FXML
  private void initialize() {
    showIntroToggleButton
        .selectedProperty()
        .bindBidirectional(generalSettingsViewModel.showIntroProperty());
  }
}
