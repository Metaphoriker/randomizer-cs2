package de.metaphoriker.randomizer.ui.view.viewmodel;

import com.google.inject.Inject;
import de.metaphoriker.model.ApplicationContext;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.action.sequence.ActionSequenceDispatcher;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerViewModel {

    private final ActionSequenceDispatcher actionSequenceDispatcher;
    private final ApplicationContext applicationContext;

    @Getter
    private final ObjectProperty<ActionSequence> currentActionSequenceProperty =
            new SimpleObjectProperty<>();

    @Getter
    private final ObjectProperty<Action> currentActionProperty = new SimpleObjectProperty<>();
    @Getter
    private final BooleanProperty waitingProperty = new SimpleBooleanProperty(false);

    @Inject
    public RandomizerViewModel(
            ActionSequenceDispatcher actionSequenceDispatcher, ApplicationContext applicationContext) {
        this.actionSequenceDispatcher = actionSequenceDispatcher;
        this.applicationContext = applicationContext;
        initialize();
    }

    private void initialize() {
        setupInternalHandler();
        setupInternalListener();
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
