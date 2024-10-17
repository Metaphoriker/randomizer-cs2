package de.metaphoriker.randomizer.ui.view.views;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

@View
public class BuilderView extends HBox {

  private final BuilderViewModel builderViewModel;

  @Inject
  public BuilderView(BuilderViewModel builderViewModel) {
    this.builderViewModel = builderViewModel;
    Platform.runLater(this::initialize);
  }

  private void initialize() {
    HBox.setHgrow(this, Priority.ALWAYS);
  }
}
