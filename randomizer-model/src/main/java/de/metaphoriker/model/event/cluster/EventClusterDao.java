package de.metaphoriker.model.event.cluster;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.json.JsonUtil;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventClusterDao {

  public static final File CLUSTER_FOLDER =
      new File(ApplicationContext.getAppdataFolder() + File.separator + "cluster");

  static {
    if (!CLUSTER_FOLDER.exists() && !CLUSTER_FOLDER.mkdirs()) {
      log.error("Failed to create cluster folder: {}", CLUSTER_FOLDER.getAbsolutePath());
    }
  }

  public synchronized void saveCluster(EventCluster cluster) {
    Objects.requireNonNull(cluster, "Cluster cannot be null");

    File file = new File(CLUSTER_FOLDER, cluster.getName() + ".cluster");

    try (PrintWriter writer = new PrintWriter(file)) {
      StringBuilder stringBuilder = new StringBuilder();
      for (Event event : cluster.getEvents()) {
        stringBuilder.append(JsonUtil.serialize(event)).append(";");
      }

      writer.println(stringBuilder);
      writer.flush();
      log.info("Successfully saved cluster: {}", cluster.getName());
    } catch (IOException e) {
      log.error("Failed to save cluster: {}", cluster.getName(), e);
    }
  }

  public synchronized void deleteCluster(EventCluster cluster) {
    Objects.requireNonNull(cluster, "Cluster cannot be null");

    File file = new File(CLUSTER_FOLDER, cluster.getName() + ".cluster");
    if (!file.exists()) {
      log.warn("Cluster file does not exist: {}", file.getAbsolutePath());
      return;
    }

    try {
      Files.delete(file.toPath());
      log.info("Successfully deleted cluster: {}", cluster.getName());
    } catch (IOException e) {
      log.error("Failed to delete cluster: {}", cluster.getName(), e);
      throw new RuntimeException(e);
    }
  }

  public List<EventCluster> loadClusters() {
    List<EventCluster> clusters = new ArrayList<>();
    File[] clusterFiles = CLUSTER_FOLDER.listFiles();

    if (clusterFiles == null || clusterFiles.length == 0) {
      log.warn("No cluster files found in: {}", CLUSTER_FOLDER.getAbsolutePath());
      return clusters;
    }

    for (File file : clusterFiles) {
      if (!file.getName().endsWith(".cluster")) continue;

      try {
        String content = Files.readAllLines(file.toPath()).get(0);
        String[] events = content.split(";");
        List<Event> eventList = new ArrayList<>(events.length);

        for (String event : events) {
          eventList.add(JsonUtil.deserialize(event));
        }

        String name = file.getName().replace(".cluster", "");
        clusters.add(new EventCluster(name, eventList));
      } catch (IOException e) {
        log.error("Failed to load cluster from file: {}", file.getAbsolutePath(), e);
      }
    }

    log.info("Successfully loaded {} clusters", clusters.size());
    return clusters;
  }
}
