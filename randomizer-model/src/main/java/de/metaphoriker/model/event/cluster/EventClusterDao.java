package de.metaphoriker.model.event.cluster;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.json.JsonUtil;
import de.metaphoriker.model.stuff.WhateverThisFuckerIs;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EventClusterDao {

  // big no no to be public but i'm lazy
  public static final File CLUSTER_FOLDER =
      new File(WhateverThisFuckerIs.getAppdataFolder() + File.separator + "cluster");

  static {
    CLUSTER_FOLDER.mkdirs();
  }

  public void saveCluster(EventCluster cluster) {

    File file = new File(CLUSTER_FOLDER, cluster.getName() + ".cluster");
    try (PrintWriter writer = new PrintWriter(file)) {

      StringBuilder stringBuilder = new StringBuilder();
      for (Event event : cluster.getEvents())
        stringBuilder.append(JsonUtil.serialize(event)).append(";");

      writer.println(stringBuilder);
      writer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void deleteCluster(EventCluster cluster) {
    File file = new File(CLUSTER_FOLDER, cluster.getName() + ".cluster");
    try {
      Files.delete(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<EventCluster> loadClusters() {

    List<EventCluster> clusters = new ArrayList<>();
    for (File file : CLUSTER_FOLDER.listFiles()) {

      if (!file.getName().endsWith(".cluster")) continue;

      EventCluster cluster;
      try {

        String content = Files.readAllLines(file.toPath()).get(0);
        String[] events = content.split(";");

        List<Event> eventList = new ArrayList<>(events.length);

        for (String event : events) eventList.add(JsonUtil.deserialize(event));

        String name = file.getName().replace(".cluster", "");
        cluster = new EventCluster(name, eventList);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      clusters.add(cluster);
    }

    return clusters;
  }
}
