package com.revortix.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import com.revortix.model.ApplicationContext;
import com.revortix.model.ApplicationState;
import com.revortix.model.action.Action;
import com.revortix.model.action.sequence.ActionSequence;
import com.revortix.model.action.sequence.ActionSequenceDispatcher;
import com.revortix.model.action.sequence.ActionSequenceExecutorRunnable;
import com.revortix.randomizer.config.RandomizerConfig;
import java.util.function.Consumer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerViewModel {

  private final ActionSequenceDispatcher actionSequenceDispatcher;
  private final ApplicationContext applicationContext;
  private final RandomizerConfig randomizerConfig;

  @Getter
  private final ObjectProperty<ActionSequence> currentActionSequenceProperty =
      new SimpleObjectProperty<>();

  @Getter private final ObjectProperty<Action> currentActionProperty = new SimpleObjectProperty<>();
  @Getter private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
  @Getter private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

  @Inject
  public RandomizerViewModel(
      ActionSequenceDispatcher actionSequenceDispatcher,
      ApplicationContext applicationContext,
      RandomizerConfig randomizerConfig) {
    this.actionSequenceDispatcher = actionSequenceDispatcher;
    this.applicationContext = applicationContext;
    this.randomizerConfig = randomizerConfig;
    setupInternalHandler();
  }

  public void saveInterval() {
    randomizerConfig.setMinInterval(minIntervalProperty.get());
    randomizerConfig.setMaxInterval(maxIntervalProperty.get());
    randomizerConfig.saveConfiguration();

    log.info(
        "Save interval {}:{} to configuration.",
        minIntervalProperty.get(),
        maxIntervalProperty.get());

    ActionSequenceExecutorRunnable.setMinWaitTime(minIntervalProperty.get());
    ActionSequenceExecutorRunnable.setMaxWaitTime(maxIntervalProperty.get());
  }

  public void initConfig() {
    loadIntervalFromConfig();
  }

  private void loadIntervalFromConfig() {
    minIntervalProperty.set(randomizerConfig.getMinInterval());
    maxIntervalProperty.set(randomizerConfig.getMaxInterval());
  }

  public void setApplicationStateToRunning() {
    applicationContext.setApplicationState(ApplicationState.RUNNING);
  }

  public void setApplicationStateToStopped() {
    applicationContext.setApplicationState(ApplicationState.IDLING);
  }

  public void onStateChange(Consumer<ApplicationState> consumer) {
    applicationContext.registerApplicationStateChangeListener(consumer);
  }

  private void setupInternalHandler() {
    actionSequenceDispatcher.registerSequenceHandler(currentActionSequenceProperty::set);
    actionSequenceDispatcher.registerActionHandler(currentActionProperty::set);
  }
}
