package de.metaphoriker.view.views;

import de.metaphoriker.view.View;
import de.metaphoriker.view.viewmodel.BuilderViewModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

@View
public class BuilderView extends HBox {

  private final BuilderViewModel builderViewModel = new BuilderViewModel();

  public BuilderView() {
    initialize();
  }

  private void initialize() {
    HBox.setHgrow(this, Priority.ALWAYS);
  }
}
