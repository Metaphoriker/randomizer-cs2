package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.util.ImageUtil;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.ControlBarViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

@View
public class NavigationBarController {

  private final ControlBarViewModel controlBarViewModel;
  private final ViewProvider viewProvider;

  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private ToggleButton settingsButton;

  @Inject
  public NavigationBarController(
      ControlBarViewModel controlBarViewModel, ViewProvider viewProvider) {
    this.controlBarViewModel = controlBarViewModel;
    this.viewProvider = viewProvider;

    Platform.runLater(this::initialize);
  }

  private void initialize() {
    setupGraphics();
    setupBindings();
    setupToggleButtonLogic();
  }

  private void setupBindings() {
    controlBarViewModel
        .getSelectedView()
        .addListener(
            (_, _, newView) -> {
              if (newView != null) {
                viewProvider.triggerViewChange(newView);
              }
            });
  }

  private void setupGraphics() {
    randomizerButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    builderButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    settingsButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
  }

  private void setupToggleButtonLogic() {
    addToggleButtonListener(randomizerButton, null, builderButton, settingsButton);
    addToggleButtonListener(
        builderButton, BuilderViewController.class, randomizerButton, settingsButton);
    addToggleButtonListener(settingsButton, null, randomizerButton, builderButton);
  }

  private void addToggleButtonListener(
      ToggleButton button, Class<?> newView, ToggleButton... otherButtons) {
    ChangeListener<Boolean> listener = createToggleButtonListener(newView, otherButtons);
    button.selectedProperty().addListener(listener);
  }

  private ChangeListener<Boolean> createToggleButtonListener(
      Class<?> newView, ToggleButton... otherButtons) {
    return (_, _, isSelected) -> {
      handleToggleSelection(isSelected, newView, otherButtons);
    };
  }

  private void handleToggleSelection(
      boolean isSelected, Class<?> newView, ToggleButton... otherButtons) {
    if (isSelected) {
      controlBarViewModel.setSelectedView(newView);
      for (ToggleButton button : otherButtons) {
        button.setSelected(false);
      }
    } else {
      controlBarViewModel.setSelectedView(RandomizerWindowController.class);
    }
  }
}
