package de.metaphoriker.gui.component;

import de.metaphoriker.model.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import lombok.Getter;

public class EventComponent extends VBox {

  private final HBox innerHBox = new HBox();
  private final Label nameLabel = new Label();
  private final Button button = new Button("Interval-Settings");

  @Getter private final Event represent;
  private final EventSettingsComponent eventSettingsComponent;

  public EventComponent(Event event) {

    this.represent = event;
    this.eventSettingsComponent = new EventSettingsComponent(represent);

    innerHBox.setSpacing(75);

    nameLabel.setText(event.name());

    button.setFont(new Font("Arial", 8));
    button.setOnAction(
        click -> {
          eventSettingsComponent.setVisible(!eventSettingsComponent.isVisible());

          if (eventSettingsComponent.isVisible()) getChildren().add(eventSettingsComponent);
          else getChildren().remove(eventSettingsComponent);
        });

    eventSettingsComponent.setVisible(false);

    Region placeHolder = new Region();
    HBox.setHgrow(placeHolder, Priority.ALWAYS);

    innerHBox.getChildren().addAll(nameLabel, placeHolder);
    if (event.getInterval() != null) {
      innerHBox.getChildren().add(button);
    }

    getChildren().addAll(innerHBox);
  }

  public void apply() {
    eventSettingsComponent.apply();
  }
}
