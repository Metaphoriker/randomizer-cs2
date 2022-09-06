package dev.luzifer.model.config;

import dev.luzifer.model.event.cluster.EventCluster;

import java.util.Arrays;
import java.util.Objects;

public class Config {
    
    private final String name;
    private final String description;
    
    private final EventCluster[] eventClusters;
    
    public Config(String name, String description, EventCluster... eventClusters) {
        this.name = name;
        this.description = description;
        this.eventClusters = eventClusters;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public EventCluster[] getEventClusters() {
        return eventClusters;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(name, config.name) && Objects.equals(description, config.description) && Arrays.equals(eventClusters, config.eventClusters);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(name, description);
        result = 31 * result + Arrays.hashCode(eventClusters);
        return result;
    }
}
