package de.metaphoriker.view;

import de.metaphoriker.viewmodel.BuilderViewModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class BuilderView extends HBox {

  private final BuilderViewModel builderViewModel = new BuilderViewModel();

  public BuilderView() {
    initialize();
  }

  private void initialize() {
    HBox.setHgrow(this, Priority.ALWAYS);
  }
}
