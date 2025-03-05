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
    generalToggleButton
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              updaterToggleButton.setSelected(!newVal);
              if (newVal) {
                contentPane
                    .getChildren()
                    .setAll(viewProvider.requestView(GeneralSettingsController.class).parent());
              }
            });

    updaterToggleButton
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              generalToggleButton.setSelected(!newVal);
              if (newVal) {
                contentPane
                    .getChildren()
                    .setAll(viewProvider.requestView(UpdaterSettingsViewController.class).parent());
              }
            });
  }
}
