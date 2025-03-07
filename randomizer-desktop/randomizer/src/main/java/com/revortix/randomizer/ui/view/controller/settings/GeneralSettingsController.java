package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.config.RandomizerConfig;
import com.revortix.randomizer.ui.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

@View
public class GeneralSettingsController {

  private final RandomizerConfig randomizerConfig;

  @FXML private ToggleButton showIntroToggleButton;

  @Inject
  public GeneralSettingsController(RandomizerConfig randomizerConfig) {
    this.randomizerConfig = randomizerConfig;
  }

  @FXML
  private void initialize() {
    showIntroToggleButton.setSelected(randomizerConfig.isShowIntro());

    showIntroToggleButton
        .selectedProperty()
        .addListener((obs, oldVal, newVal) -> randomizerConfig.setShowIntro(newVal));
  }
}
