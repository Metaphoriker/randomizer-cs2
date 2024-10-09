package de.metaphoriker.gui.component;

import de.metaphoriker.model.event.Action;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EventSettingsComponent extends VBox {

  private final Label title = new Label();

  private final VBox vBox = new VBox();

  private final Action action;

  public EventSettingsComponent(Action action) {

    this.action = action;

    title.setText(action.name() + " Settings");

    if (action.getInterval() != null) {

      SliderLabelComponent minSlider =
          new SliderLabelComponent("Min", 1, 9999, action.getInterval().getMin());
      SliderLabelComponent maxSlider =
          new SliderLabelComponent("Max", 2, 10000, action.getInterval().getMax());

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

    if (action.getInterval() != null) {

      int min = (int) ((SliderLabelComponent) vBox.getChildren().get(0)).getSlider().getValue();
      int max = (int) ((SliderLabelComponent) vBox.getChildren().get(1)).getSlider().getValue();

      if (min >= max) return;

      action.getInterval().setMin(min);
      action.getInterval().setMax(max);
    }
  }
}
