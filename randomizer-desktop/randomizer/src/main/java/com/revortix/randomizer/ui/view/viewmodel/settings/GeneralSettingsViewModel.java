package com.revortix.randomizer.ui.view.viewmodel.settings;

import com.google.inject.Inject;
import com.revortix.model.action.sequence.ActionSequenceExecutorRunnable;
import com.revortix.randomizer.config.RandomizerConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

public class GeneralSettingsViewModel {

  @Getter private final BooleanProperty showIntroProperty = new SimpleBooleanProperty(true);
  @Getter private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
  @Getter private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

  private final RandomizerConfig randomizerConfig;

  @Inject
  public GeneralSettingsViewModel(RandomizerConfig randomizerConfig) {
    this.randomizerConfig = randomizerConfig;
  }

  public void setupViewModel() {
    loadIntervalFromConfig();
    showIntroProperty.set(randomizerConfig.isShowIntro());
    showIntroProperty.addListener((_, _, _) -> saveRegularSettings());
    minIntervalProperty.addListener((_, _, _) -> updateInterval());
    maxIntervalProperty.addListener((_, _, _) -> updateInterval());
  }

  public String getConfigPath() {
    return randomizerConfig.getConfigPath();
  }

  private void loadIntervalFromConfig() {
    minIntervalProperty.set(randomizerConfig.getMinInterval());
    maxIntervalProperty.set(randomizerConfig.getMaxInterval());
  }

  public void updateInterval() {
    randomizerConfig.setMinInterval(minIntervalProperty.get());
    randomizerConfig.setMaxInterval(maxIntervalProperty.get());
    ActionSequenceExecutorRunnable.setMinWaitTime(minIntervalProperty.get());
    ActionSequenceExecutorRunnable.setMaxWaitTime(maxIntervalProperty.get());
    randomizerConfig.save();
  }

  public void saveRegularSettings() {
    randomizerConfig.setShowIntro(showIntroProperty.get());
    randomizerConfig.save();
  }
}
