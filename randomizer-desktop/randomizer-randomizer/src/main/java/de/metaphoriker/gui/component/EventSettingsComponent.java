package de.metaphoriker.gui.component;

import de.metaphoriker.model.event.Event;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EventSettingsComponent extends VBox {

  private final Label title = new Label();

  private final VBox vBox = new VBox();

  private final Event event;

  public EventSettingsComponent(Event event) {

    this.event = event;

    title.setText(event.name() + " Settings");

    if (event.getInterval() != null) {

      SliderLabelComponent minSlider =
          new SliderLabelComponent("Min", 1, 9999, event.getInterval().getMin());
      SliderLabelComponent maxSlider =
          new SliderLabelComponent("Max", 2, 10000, event.getInterval().getMax());

      Label infoLabel =
          new Label(
              "unit = milliseconds.\nsets the interval between the press and release of a key.");
      infoLabel.setOpacity(0.5);
      infoLabel.setWrapText(true);

      vBox.getChildren().addAll(minSlider, maxSlider, infoLabel);
    }

    getChildren().addAll(title, vBox);
  }

  public void apply() {

    if (event.getInterval() != null) {

      int min = (int) ((SliderLabelComponent) vBox.getChildren().get(0)).getSlider().getValue();
      int max = (int) ((SliderLabelComponent) vBox.getChildren().get(1)).getSlider().getValue();

      if (min >= max) return;

      event.getInterval().setMin(min);
      event.getInterval().setMax(max);
    }
  }
}
