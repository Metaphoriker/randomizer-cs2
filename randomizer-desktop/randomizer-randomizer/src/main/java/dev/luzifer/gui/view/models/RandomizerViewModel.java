package dev.luzifer.gui.view.models;

import dev.luzifer.gui.view.ViewModel;
import dev.luzifer.model.ApplicationState;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventDispatcher;
import dev.luzifer.model.event.EventExecutorRunnable;
import dev.luzifer.model.event.EventRegistry;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.event.cluster.EventClusterRepository;
import dev.luzifer.model.stuff.WhateverThisFuckerIs;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RandomizerViewModel implements ViewModel {
    
    private final BooleanProperty visibleProperty = new SimpleBooleanProperty();
    private final IntegerProperty minWaitTimeProperty = new SimpleIntegerProperty();
    private final IntegerProperty maxWaitTimeProperty = new SimpleIntegerProperty();
    private final StringProperty nextStateProperty = new SimpleStringProperty("Run");
    private final EventClusterRepository eventClusterRepository;
    
    public RandomizerViewModel(EventClusterRepository eventClusterRepository) {
        
        this.eventClusterRepository = eventClusterRepository;
    
        visibleProperty.addListener((observableValue, aBoolean, t1) -> {
            if(t1) {
    
                int minWaitTime = minWaitTimeProperty.get();
                int maxWaitTime = maxWaitTimeProperty.get();
                
                if(maxWaitTime <= minWaitTime) {
                    visibleProperty.setValue(false);
                    return;
                }
                
                EventExecutorRunnable.setMinWaitTime(minWaitTimeProperty.get());
                EventExecutorRunnable.setMaxWaitTime(maxWaitTimeProperty.get());
            }
        });
    }
    
    public void toggleApplicationState() {
        if(WhateverThisFuckerIs.getApplicationState() == ApplicationState.IDLING) {
            WhateverThisFuckerIs.setApplicationState(ApplicationState.RUNNING);
            nextStateProperty.setValue("Idle");
        } else {
            WhateverThisFuckerIs.setApplicationState(ApplicationState.IDLING);
            nextStateProperty.setValue("Run");
        }
    }
    
    public void setOnClusterExecution(Consumer<EventCluster> callback) {
        getClusters().forEach(cluster -> EventDispatcher.registerGenericClusterHandler(cluster, callback));
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
