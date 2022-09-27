package dev.luzifer.model.event.cluster;

import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.EventRegistry;
import dev.luzifer.model.file.WhateverThisFuckerIs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EventClusterDao {
    
    private static final File CLUSTER_FOLDER = new File(WhateverThisFuckerIs.getAppdataFolder() + File.separator + "cluster");
    
    public void saveCluster(EventCluster cluster) {
        
        File file = new File(CLUSTER_FOLDER, cluster.getName() + ".cluster");
        try(PrintWriter writer = new PrintWriter(file)) {
            
            StringBuilder stringBuilder = new StringBuilder();
            for(Event event : cluster.getEvents())
                stringBuilder.append(event.name()).append(";");
            
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
        for(File file : CLUSTER_FOLDER.listFiles()) {
            
            if(!file.getName().endsWith(".cluster"))
                continue;
            
            String name = file.getName().replace(".cluster", "");
            EventCluster cluster;
            
            try {
                
                String content = Files.readAllLines(file.toPath()).get(0);
                String[] events = content.split(";");
                
                Event[] eventArray = new Event[events.length];
                
                int index = 0;
                for(String event : events) {
                    
                    Event eventInstance = EventRegistry.getByName(event);
                    if(eventInstance == null)
                        continue;
                    
                    eventArray[index++] = eventInstance;
                }
                
                cluster = new EventCluster(name, eventArray);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            clusters.add(cluster);
        }
        
        return clusters;
    }
    
}
