package de.metaphoriker.gui.view.models;

import de.metaphoriker.gui.view.ViewModel;
import de.metaphoriker.model.ApplicationState;
import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.EventDispatcher;
import de.metaphoriker.model.event.EventExecutorRunnable;
import de.metaphoriker.model.event.EventRegistry;
import de.metaphoriker.model.event.cluster.EventCluster;
import de.metaphoriker.model.event.cluster.EventClusterRepository;
import de.metaphoriker.model.stuff.WhateverThisFuckerIs;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RandomizerViewModel implements ViewModel {

  private final BooleanProperty visibleProperty = new SimpleBooleanProperty();
  private final IntegerProperty minWaitTimeProperty = new SimpleIntegerProperty();
  private final IntegerProperty maxWaitTimeProperty = new SimpleIntegerProperty();
  private final StringProperty nextStateProperty = new SimpleStringProperty("Run");

  private final EventClusterRepository eventClusterRepository;

  private Runnable onFailed;

  public RandomizerViewModel(EventClusterRepository eventClusterRepository) {

    this.eventClusterRepository = eventClusterRepository;

    visibleProperty.addListener(
        (observableValue, aBoolean, t1) -> {
          if (Boolean.FALSE.equals(t1)) {

            int minWaitTime = minWaitTimeProperty.get();
            int maxWaitTime = maxWaitTimeProperty.get();

            if (minWaitTime > maxWaitTime || minWaitTime == maxWaitTime) {
              if (onFailed != null) onFailed.run();
              return;
            }

            EventExecutorRunnable.setMinWaitTime(minWaitTimeProperty.get() * 1000);
            EventExecutorRunnable.setMaxWaitTime(maxWaitTimeProperty.get() * 1000);
          }
        });
  }

  public void toggleApplicationState() {
    if (WhateverThisFuckerIs.getApplicationState() == ApplicationState.IDLING) {
      WhateverThisFuckerIs.setApplicationState(ApplicationState.RUNNING);
      nextStateProperty.setValue("Idle");
    } else {
      WhateverThisFuckerIs.setApplicationState(ApplicationState.IDLING);
      nextStateProperty.setValue("Run");
    }
  }

  public void setOnClusterExecution(Consumer<EventCluster> callback) {
    getClusters()
        .forEach(cluster -> EventDispatcher.registerGenericClusterHandler(cluster, callback));
  }

  public void setOnClusterExecutionFinished(Consumer<EventCluster> callback) {
    Consumer<Object> wrappedCallback = o -> callback.accept((EventCluster) o);
    getClusters().forEach(cluster -> EventDispatcher.registerOnFinish(cluster, wrappedCallback));
  }

  public void setOnEventExecution(Consumer<Event> callback) {
    EventDispatcher.registerGenericHandler(callback);
  }

  public void setOnEventExecutionFinished(Consumer<Event> callback) {
    Consumer<Object> wrappedCallback = o -> callback.accept((Event) o);
    getEvents().forEach(event -> EventDispatcher.registerOnFinish(event, wrappedCallback));
  }

  public void setOnFailed(Runnable onFailed) {
    this.onFailed = onFailed;
  }

  public Set<Event> getEvents() {
    return EventRegistry.getEvents();
  }

  public List<EventCluster> getClusters() {
    return eventClusterRepository.loadClusters();
  }

  public StringProperty getNextStateProperty() {
    return nextStateProperty;
  }

  public BooleanProperty getVisibleProperty() {
    return visibleProperty;
  }

  public IntegerProperty getMinWaitTimeProperty() {
    return minWaitTimeProperty;
  }

  public IntegerProperty getMaxWaitTimeProperty() {
    return maxWaitTimeProperty;
  }
}
