package com.revortix.randomizer.ui.view.viewmodel.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.config.RandomizerConfig;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

public class UpdateSettingsViewModel {

  private static final String REPO_LINK = "https://github.com/Metaphoriker/randomizer-cs2";

  private final BooleanProperty autoUpdateProperty = new SimpleBooleanProperty(true);
  private final BooleanProperty updateNotifierProperty = new SimpleBooleanProperty(true);

  private final ChangeListener<Boolean> switchSettingsListener =
      (obs, oldVal, newVal) -> saveSettings();

  private final RandomizerConfig randomizerConfig;

  @Inject
  public UpdateSettingsViewModel(RandomizerConfig randomizerConfig) {
    this.randomizerConfig = randomizerConfig;
    setupListener();
  }

  private void setupListener() {
    autoUpdateProperty.addListener(switchSettingsListener);
    updateNotifierProperty.addListener(switchSettingsListener);
  }

  public BooleanProperty autoUpdateProperty() {
    return autoUpdateProperty;
  }

  public BooleanProperty updateNotifierProperty() {
    return updateNotifierProperty;
  }

  public void saveSettings() {
    randomizerConfig.setAutoupdateEnabled(autoUpdateProperty.get());
    randomizerConfig.setUpdateNotifier(updateNotifierProperty.get());
    randomizerConfig.saveConfiguration();
  }

  public void browseRepository() throws IOException {
    Desktop.getDesktop().browse(URI.create(REPO_LINK));
  }
}
