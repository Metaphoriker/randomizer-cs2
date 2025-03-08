package com.revortix.randomizer.ui.view.viewmodel.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.config.RandomizerConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class GeneralSettingsViewModel {

  private final BooleanProperty showIntroProperty = new SimpleBooleanProperty(true);

  private final RandomizerConfig randomizerConfig;

  @Inject
  public GeneralSettingsViewModel(RandomizerConfig randomizerConfig) {
    this.randomizerConfig = randomizerConfig;
    showIntroProperty.addListener((_, _, _) -> saveSettings());
  }

  public BooleanProperty showIntroProperty() {
    return showIntroProperty;
  }

  public void saveSettings() {
    randomizerConfig.setShowIntro(showIntroProperty.get());
    randomizerConfig.saveConfiguration();
  }
}
