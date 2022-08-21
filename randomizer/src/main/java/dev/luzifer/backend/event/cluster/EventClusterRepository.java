package dev.luzifer.backend.event.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventClusterRepository {
    
    private final Map<String, EventCluster> clusters = new HashMap<>();
    
    public void addCluster(EventCluster cluster) {
        clusters.put(cluster.getName(), cluster);
    }
    
    public void removeCluster(String name) {
        clusters.remove(name);
    }
    
    public EventCluster getCluster(String name) {
        return clusters.get(name);
    }
    
    public List<EventCluster> getClusters() {
        return new ArrayList<>(clusters.values());
    }
    
}
