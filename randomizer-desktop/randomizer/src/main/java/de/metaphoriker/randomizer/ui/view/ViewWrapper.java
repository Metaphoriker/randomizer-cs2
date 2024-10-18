package de.metaphoriker.randomizer.ui.view;

import javafx.scene.Parent;

public class ViewWrapper<T> {

  private final Parent parent;
  private final T controller;

  public ViewWrapper(Parent parent, T controller) {
    this.parent = parent;
    this.controller = controller;
  }

  public Parent getParent() {
    return parent;
  }

  public T getController() {
    return controller;
  }
}
