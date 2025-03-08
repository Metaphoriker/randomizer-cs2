package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.settings.UpdateSettingsViewModel;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

@View
public class UpdaterSettingsViewController {

  private final UpdateSettingsViewModel updateSettingsViewModel;

  @FXML private ToggleButton autoUpdateToggleButton;
  @FXML private ToggleButton updateNotifierToggleButton;

  @Inject
  public UpdaterSettingsViewController(UpdateSettingsViewModel updateSettingsViewModel) {
    this.updateSettingsViewModel = updateSettingsViewModel;
  }

  @FXML
  private void initialize() {
    bind();
  }

  @FXML
  public void onUpdateCheck(ActionEvent event) {}

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
