package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.controller.builder.BuilderViewController;
import com.revortix.randomizer.ui.view.viewmodel.NavigationBarViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

@View
public class NavigationBarController {
  private final NavigationBarViewModel navigationBarViewModel;
  private final ViewProvider viewProvider;

  @FXML private ToggleButton homeButton;
  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private ToggleButton settingsButton;

  @Inject
  public NavigationBarController(
      NavigationBarViewModel navigationBarViewModel, ViewProvider viewProvider) {
    this.navigationBarViewModel = navigationBarViewModel;
    this.viewProvider = viewProvider;
  }

  @FXML
  private void initialize() {
    setupBindings();
    setupToggleButtonLogic();
    toggle(homeButton);
  }

  private void setupBindings() {
    addListenerForSelectedViewChange();
  }

  private void addListenerForSelectedViewChange() {
    navigationBarViewModel
        .getSelectedView()
        .addListener((_, _, newView) -> triggerViewChange(newView));
  }

  private void triggerViewChange(Class<?> newView) {
    if (newView != null) {
      viewProvider.triggerViewChange(newView);
    }
  }

  private void toggle(ToggleButton button) {
    button.setSelected(!button.isSelected());
  }

  private void setupToggleButtonLogic() {
    addToggleButtonListener(
        homeButton, HomeViewController.class, randomizerButton, builderButton, settingsButton);
    addToggleButtonListener(
        randomizerButton,
        RandomizerViewController.class,
        homeButton,
        builderButton,
        settingsButton);
    addToggleButtonListener(
        builderButton, BuilderViewController.class, homeButton, randomizerButton, settingsButton);
    addToggleButtonListener(
        settingsButton, SettingsViewController.class, homeButton, randomizerButton, builderButton);
  }

  private void addToggleButtonListener(
      ToggleButton button, Class<?> newView, ToggleButton... otherButtons) {
    button
        .selectedProperty()
        .addListener(createToggleButtonListener(button, newView, otherButtons));
  }

  private ChangeListener<Boolean> createToggleButtonListener(
      ToggleButton button, Class<?> newView, ToggleButton... otherButtons) {
    return (_, _, isSelected) -> handleToggleSelection(button, newView, isSelected, otherButtons);
  }

  private void handleToggleSelection(
      ToggleButton button, Class<?> newView, boolean isSelected, ToggleButton... otherButtons) {
    if (isSelected) {
      navigationBarViewModel.setSelectedView(newView);
      deselectOtherButtons(otherButtons);
    } else {
      reactivateButtonIfNoneSelected(button, otherButtons);
    }
  }

  private void deselectOtherButtons(ToggleButton... otherButtons) {
    for (ToggleButton otherButton : otherButtons) {
      if (otherButton.isSelected()) {
        otherButton.setSelected(false);
      }
    }
  }

  private void reactivateButtonIfNoneSelected(ToggleButton button, ToggleButton... otherButtons) {
    if (!isAnotherButtonSelected(otherButtons)) {
      Platform.runLater(() -> button.setSelected(true));
    }
  }

  private boolean isAnotherButtonSelected(ToggleButton... buttons) {
    for (ToggleButton button : buttons) {
      if (button.isSelected()) {
        return true;
      }
    }
    return false;
  }
}
