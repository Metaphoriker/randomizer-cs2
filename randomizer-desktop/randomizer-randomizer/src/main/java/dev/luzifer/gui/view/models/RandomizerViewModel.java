package dev.luzifer.gui.view.models;

import dev.luzifer.gui.view.ViewModel;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.event.cluster.EventClusterRepository;

import java.util.List;

public class RandomizerViewModel implements ViewModel {
    
    private final EventClusterRepository eventClusterRepository;
    
    public RandomizerViewModel(EventClusterRepository eventClusterRepository) {
        this.eventClusterRepository = eventClusterRepository;
    }
    
    public List<EventCluster> getClusters() {
        return eventClusterRepository.loadClusters();
    }
    
}
