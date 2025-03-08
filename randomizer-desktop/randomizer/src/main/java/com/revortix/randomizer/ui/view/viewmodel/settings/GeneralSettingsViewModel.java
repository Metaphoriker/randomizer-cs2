package com.revortix.randomizer.ui.view.viewmodel.settings;

import com.google.inject.Inject;
import com.revortix.model.action.sequence.ActionSequenceExecutorRunnable;
import com.revortix.randomizer.bootstrap.RandomizerConfigLoader;
import com.revortix.randomizer.config.RandomizerConfig;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public class GeneralSettingsViewModel {

  @Getter private final StringProperty configPathProperty = new SimpleStringProperty();
  @Getter private final BooleanProperty showIntroProperty = new SimpleBooleanProperty(true);
  @Getter private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
  @Getter private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

  private final RandomizerConfig randomizerConfig;
  private final RandomizerConfigLoader randomizerConfigLoader;

  @Inject
  public GeneralSettingsViewModel(
      RandomizerConfig randomizerConfig, RandomizerConfigLoader randomizerConfigLoader) {
    this.randomizerConfig = randomizerConfig;
    this.randomizerConfigLoader = randomizerConfigLoader;
  }

  public CompletionStage<Void> loadConfigs() {
    return CompletableFuture.runAsync(
            () -> {
              randomizerConfig.setConfigPath(randomizerConfigLoader.ladeUserConfigPath());
              randomizerConfigLoader.ladeDefaultKeyBinds();
              randomizerConfigLoader.ladeUserKeyBinds();
            })
        .thenRunAsync(
            () -> configPathProperty.set(randomizerConfig.getConfigPath()), Platform::runLater);
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
