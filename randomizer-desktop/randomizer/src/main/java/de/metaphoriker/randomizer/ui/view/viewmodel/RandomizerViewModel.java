package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationContext;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.action.sequence.ActionSequenceDispatcher;
import de.metaphoriker.model.action.sequence.ActionSequenceExecutorRunnable;
import de.metaphoriker.randomizer.config.RandomizerConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    @Getter
    private final ObjectProperty<Action> currentActionProperty = new SimpleObjectProperty<>();
    @Getter
    private final BooleanProperty waitingProperty = new SimpleBooleanProperty(false);
    @Getter
    private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
    @Getter
    private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

    @Inject
    public RandomizerViewModel(
            ActionSequenceDispatcher actionSequenceDispatcher, ApplicationContext applicationContext, RandomizerConfig randomizerConfig) {
        this.actionSequenceDispatcher = actionSequenceDispatcher;
        this.applicationContext = applicationContext;
        this.randomizerConfig = randomizerConfig;
        setupInternalHandler();
        setupInternalListener();
    }

    public void saveInterval() {
        randomizerConfig.setMinInterval(minIntervalProperty.get());
        randomizerConfig.setMaxInterval(maxIntervalProperty.get());
        randomizerConfig.saveConfiguration();

        log.info("Save interval {}:{} to configuration.", minIntervalProperty.get(), maxIntervalProperty.get());

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

    private void setupInternalHandler() {
        actionSequenceDispatcher.registerSequenceHandler(currentActionSequenceProperty::set);
        actionSequenceDispatcher.registerSequenceFinishHandler(_ -> waitingProperty.set(true));
        actionSequenceDispatcher.registerActionHandler(currentActionProperty::set);
    }

    private void setupInternalListener() {
        currentActionSequenceProperty.addListener((_, _, _) -> waitingProperty.set(false));
    }
}
