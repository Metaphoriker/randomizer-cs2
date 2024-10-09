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
import lombok.Getter;
import lombok.Setter;

public class RandomizerViewModel implements ViewModel {

  @Getter private final BooleanProperty visibleProperty = new SimpleBooleanProperty();
  @Getter private final IntegerProperty minWaitTimeProperty = new SimpleIntegerProperty();
  @Getter private final IntegerProperty maxWaitTimeProperty = new SimpleIntegerProperty();
  @Getter private final StringProperty nextStateProperty = new SimpleStringProperty("Run");

  private final EventClusterRepository eventClusterRepository;

  @Setter private Runnable onFailed;

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
    } else {
      WhateverThisFuckerIs.setApplicationState(ApplicationState.IDLING);
    }
  }

  public void setApplicationState(ApplicationState applicationState) {
    setRespectiveStatePropertyValue(applicationState);
  }

  private void setRespectiveStatePropertyValue(ApplicationState applicationState) {
    switch (applicationState) {
      case IDLING -> nextStateProperty.setValue("Run");
      case RUNNING -> nextStateProperty.setValue("Idle");
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

  public Set<Event> getEvents() {
    return EventRegistry.getEvents();
  }

  public List<EventCluster> getClusters() {
    return eventClusterRepository.loadClusters();
  }
}
