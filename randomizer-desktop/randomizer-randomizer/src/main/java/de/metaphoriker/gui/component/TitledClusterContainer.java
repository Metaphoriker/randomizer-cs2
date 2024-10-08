package de.metaphoriker.gui.component;

import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.cluster.EventCluster;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

// TODO: This class should not know any model stuff since its UI
public class TitledClusterContainer extends TitledPane {

  private final List<Label> finishedEvents = new ArrayList<>();
  private final VBox vBox = new VBox();
  @Getter private final EventCluster eventCluster;

  private boolean finished;

  private static class EventLabel {
    private final Label label;
    private final Event event;

    public EventLabel(Label label, Event event) {
      this.label = label;
      this.event = event;
    }

    public Label getLabel() {
      return label;
    }

    public Event getEvent() {
      return event;
    }
  }

  @Getter private final List<EventLabel> eventLabels = new ArrayList<>();

  public TitledClusterContainer(String title, EventCluster eventCluster) {
    this.eventCluster = eventCluster;
    setText(title);
    setContent(vBox);
    setGraphic(ImageUtil.getImageView("images/loading_gif.gif", ImageUtil.ImageResolution.MEDIUM));

    fill(eventCluster);
  }

  public void visualizeExecution(Event event) {
    Platform.runLater(
        () -> {
          for (EventLabel eventLabel : getEventLabels()) {
            if (eventLabel.getEvent().equals(event)
                && !finishedEvents.contains(eventLabel.getLabel())) {
              eventLabel
                  .getLabel()
                  .setGraphic(
                      ImageUtil.getImageView(
                          "images/loading_gif.gif", ImageUtil.ImageResolution.SMALL));
              break;
            }
          }
        });
  }

  // Mark the event as finished
  public void finish(Event event) {
    Platform.runLater(
        () -> {
          for (EventLabel eventLabel : getEventLabels()) {
            if (eventLabel.getEvent().equals(event)
                && !finishedEvents.contains(eventLabel.getLabel())) {
              eventLabel
                  .getLabel()
                  .setGraphic(
                      ImageUtil.getImageView(
                          "images/checkmark_icon.png", ImageUtil.ImageResolution.SMALL));

              finishedEvents.add(eventLabel.getLabel());

              break;
            }
          }
        });
  }

  public void finish() {
    Platform.runLater(
        () -> {
          this.finished = true;
          setGraphic(
              ImageUtil.getImageView(
                  "images/checkmark_icon.png", ImageUtil.ImageResolution.MEDIUM));
          setExpanded(false);

          getEventLabels()
              .forEach(
                  eventLabel ->
                      eventLabel
                          .getLabel()
                          .setGraphic(
                              ImageUtil.getImageView(
                                  "images/checkmark_icon.png", ImageUtil.ImageResolution.SMALL)));
        });
  }

  public boolean isFinished() {
    return finished;
  }

  private void fill(EventCluster eventCluster) {
    eventCluster
        .getEvents()
        .forEach(
            event -> {
              Label label = new Label(event.name());
              label.setGraphic(
                  ImageUtil.getImageView("images/squat_icon.png", ImageUtil.ImageResolution.SMALL));
              eventLabels.add(new EventLabel(label, event));
              vBox.getChildren().add(label);
            });
  }
}
