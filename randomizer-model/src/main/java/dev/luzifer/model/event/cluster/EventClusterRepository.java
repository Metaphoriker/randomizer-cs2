package dev.luzifer.model.event.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventClusterRepository {
    
    private final EventClusterDao eventClusterDao = new EventClusterDao();
    
    private final Map<String, EventCluster> clusters = new HashMap<>();
    
    public void saveCluster(EventCluster cluster) {
        eventClusterDao.saveCluster(cluster);
    }
    
    public void deleteCluster(EventCluster cluster) {
        eventClusterDao.deleteCluster(cluster);
    }
    
    public void addCluster(EventCluster cluster) {
        clusters.put(cluster.getName(), cluster);
    }
    
    public void removeCluster(String name) {
        clusters.remove(name);
    }

    public List<EventCluster> loadClusters() {
        return eventClusterDao.loadClusters();
    }
    
    public EventCluster getCluster(String name) {
        return clusters.get(name);
    }
    
    public List<EventCluster> getClusters() {
        return new ArrayList<>(clusters.values());
    }
    
}
