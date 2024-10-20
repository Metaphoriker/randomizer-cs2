package de.metaphoriker.randomizer.ui.view.controller;

import de.metaphoriker.randomizer.ui.view.View;
import javafx.application.Platform;

@View
public class BuilderViewController {

  public BuilderViewController() {
    Platform.runLater(this::initialize);
  }

  private void initialize() {}
}
