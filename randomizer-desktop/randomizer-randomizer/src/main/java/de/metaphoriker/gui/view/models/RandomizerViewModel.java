package de.metaphoriker.gui.view.models;

import de.metaphoriker.Main;
import de.metaphoriker.gui.view.ViewModel;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.event.Action;
import de.metaphoriker.model.event.handling.ActionDispatcher;
import de.metaphoriker.model.event.handling.ActionExecutorRunnable;
import de.metaphoriker.model.event.cluster.ActionSequence;
import de.metaphoriker.model.event.cluster.ActionSequenceRepository;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

public class RandomizerViewModel implements ViewModel {

  @Getter private final BooleanProperty visibleProperty = new SimpleBooleanProperty();
  @Getter private final IntegerProperty minWaitTimeProperty = new SimpleIntegerProperty();
  @Getter private final IntegerProperty maxWaitTimeProperty = new SimpleIntegerProperty();
  @Getter private final StringProperty nextStateProperty = new SimpleStringProperty("Run");

  private final ActionSequenceRepository actionSequenceRepository;

  @Setter private Runnable onFailed;

  public RandomizerViewModel(ActionSequenceRepository actionSequenceRepository) {

    this.actionSequenceRepository = actionSequenceRepository;

    visibleProperty.addListener(
        (observableValue, aBoolean, t1) -> {
          if (Boolean.FALSE.equals(t1)) {

            int minWaitTime = minWaitTimeProperty.get();
            int maxWaitTime = maxWaitTimeProperty.get();

            if (minWaitTime > maxWaitTime || minWaitTime == maxWaitTime) {
              if (onFailed != null) onFailed.run();
              return;
            }

            ActionExecutorRunnable.setMinWaitTime(minWaitTimeProperty.get() * 1000);
            ActionExecutorRunnable.setMaxWaitTime(maxWaitTimeProperty.get() * 1000);
          }
        });
  }

  public void toggleApplicationState() {
    if (ApplicationContext.getApplicationState() == ApplicationState.IDLING) {
      ApplicationContext.setApplicationState(ApplicationState.RUNNING);
    } else {
      ApplicationContext.setApplicationState(ApplicationState.IDLING);
    }
  }

  public void setApplicationState(ApplicationState applicationState) {
    setRespectiveStatePropertyValue(applicationState);
  }

  private void setRespectiveStatePropertyValue(ApplicationState applicationState) {
    switch (applicationState) {
      case IDLING -> nextStateProperty.setValue("Run");
      case RUNNING -> nextStateProperty.setValue("Idle");
      case AWAITING -> nextStateProperty.setValue("Awaiting");
    }
  }

  public void setOnClusterExecution(Consumer<ActionSequence> callback) {
    getClusters()
        .forEach(cluster -> ActionDispatcher.registerGenericClusterHandler(cluster, callback));
  }

  public void setOnClusterExecutionFinished(Consumer<ActionSequence> callback) {
    Consumer<Object> wrappedCallback = o -> callback.accept((ActionSequence) o);
    getClusters().forEach(cluster -> ActionDispatcher.registerOnFinish(cluster, wrappedCallback));
  }

  public void setOnEventExecution(Consumer<Action> callback) {
    ActionDispatcher.registerGenericHandler(callback);
  }

  public void setOnEventExecutionFinished(Consumer<Action> callback) {
    Consumer<Object> wrappedCallback = o -> callback.accept((Action) o);
    getEvents().forEach(event -> ActionDispatcher.registerOnFinish(event, wrappedCallback));
  }

  public Set<Action> getEvents() {
    return Main.getEventRegistry().getActions();
  }

  public List<ActionSequence> getClusters() {
    return actionSequenceRepository.loadClusters();
  }
}
