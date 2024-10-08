package de.metaphoriker.gui.component;

import de.metaphoriker.model.event.Event;
import de.metaphoriker.model.event.Interval;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EventSettingsComponent extends VBox {

  private final Label title = new Label();

  private final VBox vBox = new VBox();

  private final Event event;

  public EventSettingsComponent(Event event) {

    this.event = event;

    title.setText(event.name() + " Settings");

    if (event instanceof Interval) {

      SliderLabelComponent minSlider =
          new SliderLabelComponent("Min", 1, 9999, ((Interval) event).min());
      SliderLabelComponent maxSlider =
          new SliderLabelComponent("Max", 2, 10000, ((Interval) event).max());

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

    if (event instanceof Interval) {

      int min = (int) ((SliderLabelComponent) vBox.getChildren().get(0)).getSlider().getValue();
      int max = (int) ((SliderLabelComponent) vBox.getChildren().get(1)).getSlider().getValue();

      if (min >= max) return;

      ((Interval) event).setMin(min);
      ((Interval) event).setMax(max);
    }
  }
}
