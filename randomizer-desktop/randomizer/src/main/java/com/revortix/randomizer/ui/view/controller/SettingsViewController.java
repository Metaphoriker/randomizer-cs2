package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.controller.settings.GeneralSettingsController;
import com.revortix.randomizer.ui.view.controller.settings.UpdaterSettingsViewController;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

@View
public class SettingsViewController {

  private final ViewProvider viewProvider;

  @FXML private ToggleButton generalToggleButton;
  @FXML private ToggleButton updaterToggleButton;
  @FXML private GridPane contentPane;

  @Inject
  public SettingsViewController(ViewProvider viewProvider) {
    this.viewProvider = viewProvider;
  }

  @FXML
  private void initialize() {
    setupButton(generalToggleButton, UpdaterSettingsViewController.class, updaterToggleButton);
    setupButton(updaterToggleButton, GeneralSettingsController.class, generalToggleButton);
    generalToggleButton.setSelected(true);
  }

  private void setupButton(ToggleButton thisButton, Class<?> viewClass, ToggleButton otherButton) {
    thisButton
        .selectedProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue) {
                otherButton.setSelected(false);
                contentPane.getChildren().setAll(viewProvider.requestView(viewClass).parent());
              } else if (!otherButton.isSelected()) {
                thisButton.setSelected(true);
              }
            });
  }
}
