package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.bootstrap.RandomizerUpdater;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.controller.NavigationBarController;
import com.revortix.randomizer.ui.view.viewmodel.settings.UpdateSettingsViewModel;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

@View
public class UpdaterSettingsViewController {

  private final UpdateSettingsViewModel updateSettingsViewModel;
  private final RandomizerUpdater randomizerUpdater; // you don't belong here
  private final ViewProvider viewProvider;

  @FXML private ToggleButton autoUpdateToggleButton;
  @FXML private ToggleButton updateNotifierToggleButton;
  @FXML private Label updateLabel;

  @Inject
  public UpdaterSettingsViewController(
      UpdateSettingsViewModel updateSettingsViewModel,
      ViewProvider viewProvider,
      RandomizerUpdater randomizerUpdater) {
    this.updateSettingsViewModel = updateSettingsViewModel;
    this.randomizerUpdater = randomizerUpdater;
    this.viewProvider = viewProvider;
  }

  @FXML
  private void initialize() {
    bind();
    updateSettingsViewModel.setupProperties();
  }

  @FXML
  public void onUpdateCheck(ActionEvent event) {
    boolean isUpdateAvailable = randomizerUpdater.isRandomizerUpdateAvailable();
    updateLabel.setText(
        isUpdateAvailable
            ? "Update Available - v" + randomizerUpdater.getRandomizerVersion()
            : "Randomizer is up to date!");
    viewProvider.requestView(NavigationBarController.class).controller().triggerUpdateCheck();
  }

  private void bind() {
    autoUpdateToggleButton
        .selectedProperty()
        .bindBidirectional(updateSettingsViewModel.autoUpdateProperty());
    updateNotifierToggleButton
        .selectedProperty()
        .bindBidirectional(updateSettingsViewModel.updateNotifierProperty());
  }

  public void onRepoLinkClicked(MouseEvent mouseEvent) throws IOException {
    updateSettingsViewModel.browseRepository();
  }
}
