package de.metaphoriker.model.event.cluster;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventClusterRepository {

  private final EventClusterDao eventClusterDao = new EventClusterDao();
  private final Map<String, EventCluster> clusters = new HashMap<>();

  private boolean isCacheUpdated = false;

  public synchronized void saveCluster(EventCluster cluster) {
    Objects.requireNonNull(cluster, "Cluster cannot be null");

    if (clusters.containsKey(cluster.getName())) {
      log.warn("Cluster with name '{}' already exists and will be overwritten.", cluster.getName());
    }

    clusters.put(cluster.getName(), cluster);
    eventClusterDao.saveCluster(cluster);
    isCacheUpdated = true;
  }

  public synchronized void deleteCluster(EventCluster cluster) {
    Objects.requireNonNull(cluster, "Cluster cannot be null");

    if (!clusters.containsKey(cluster.getName())) {
      log.warn("Attempted to delete a non-existent cluster: {}", cluster.getName());
      return;
    }

    clusters.remove(cluster.getName());
    eventClusterDao.deleteCluster(cluster);
    isCacheUpdated = true;
  }

  public synchronized void addCluster(EventCluster cluster) {
    Objects.requireNonNull(cluster, "Cluster cannot be null");

    if (clusters.containsKey(cluster.getName())) {
      log.warn("Cluster with name '{}' already exists in memory.", cluster.getName());
      return;
    }

    clusters.put(cluster.getName(), cluster);
  }

  public synchronized void removeCluster(String name) {
    if (clusters.remove(name) == null) {
      log.warn("No cluster with name '{}' found in memory to remove.", name);
    }
  }

  public synchronized List<EventCluster> loadClusters() {
    if (!isCacheUpdated) {
      updateCache();
    }
    return getClusters();
  }

  public synchronized Optional<EventCluster> getCluster(String name) {
    return Optional.ofNullable(clusters.get(name));
  }

  public synchronized List<EventCluster> getClusters() {
    return new ArrayList<>(clusters.values());
  }

  private synchronized void updateCache() {
    clusters.clear();
    eventClusterDao.loadClusters().forEach(cluster -> clusters.put(cluster.getName(), cluster));
    isCacheUpdated = true;
  }
}
