package de.metaphoriker.gui.view.models;

import de.metaphoriker.Main;
import de.metaphoriker.gui.view.ViewModel;
import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.cluster.EventCluster;
import de.metaphoriker.model.event.cluster.EventClusterRepository;
import de.metaphoriker.model.json.JsonUtil;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BuilderViewModel implements ViewModel {

  private final EventClusterRepository eventClusterRepository;

  public BuilderViewModel(EventClusterRepository eventClusterRepository) {
    this.eventClusterRepository = eventClusterRepository;
  }

  public void saveCluster(String name, String content) {
    eventClusterRepository.saveCluster(EventCluster.formatEventCluster(name, content));
  }

  public void deleteCluster(String name) {
    eventClusterRepository.deleteCluster(eventClusterRepository.getCluster(name));
  }

  public EventCluster getCluster(String name) {
    return eventClusterRepository.getCluster(name);
  }

  public String serialize(Event event) {
    return JsonUtil.serialize(event);
  }

  public Event deserialize(String json) {
    return JsonUtil.deserialize(json);
  }

  public Event getEvent(String name) {
    return Main.getEventRegistry().getByName(name);
  }

  public Event getRandomEvent() {

    Set<Event> events = Main.getEventRegistry().getEvents();
    int index = ThreadLocalRandom.current().nextInt(0, events.size());

    return (Event) events.toArray()[index];
  }

  public Set<Event> getEvents() {
    return Main.getEventRegistry().getEvents();
  }

  public List<EventCluster> loadEventClusters() {
    return eventClusterRepository.loadClusters();
  }

  public List<EventCluster> getClusters() {
    return eventClusterRepository.getClusters();
  }
}
