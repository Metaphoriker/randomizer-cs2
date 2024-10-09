package de.metaphoriker.gui.component;

import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
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
  @Getter private final ActionSequence actionSequence;

  private boolean finished;

  private static class EventLabel {
    private final Label label;
    private final Action action;

    public EventLabel(Label label, Action action) {
      this.label = label;
      this.action = action;
    }

    public Label getLabel() {
      return label;
    }

    public Action getEvent() {
      return action;
    }
  }

  @Getter private final List<EventLabel> eventLabels = new ArrayList<>();

  public TitledClusterContainer(String title, ActionSequence actionSequence) {
    this.actionSequence = actionSequence;
    setText(title);
    setContent(vBox);
    setGraphic(ImageUtil.getImageView("images/loading_gif.gif", ImageUtil.ImageResolution.MEDIUM));

    fill(actionSequence);
  }

  public void visualizeExecution(Action action) {
    Platform.runLater(
        () -> {
          for (EventLabel eventLabel : getEventLabels()) {
            if (eventLabel.getEvent().equals(action)
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
  public void finish(Action action) {
    Platform.runLater(
        () -> {
          for (EventLabel eventLabel : getEventLabels()) {
            if (eventLabel.getEvent().equals(action)
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

  private void fill(ActionSequence actionSequence) {
    actionSequence
        .getActions()
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
