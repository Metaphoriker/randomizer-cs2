package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.config.RandomizerConfig;
import com.revortix.randomizer.ui.view.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@View
public class UpdaterSettingsViewController {

  private static final String REPO_LINK = "https://github.com/Metaphoriker/randomizer-cs2";

  private final RandomizerConfig randomizerConfig;

  @FXML private ToggleButton autoUpdateToggleButton;
  @FXML private ToggleButton updateNotifierToggleButton;

  @Inject
  public UpdaterSettingsViewController(RandomizerConfig randomizerConfig) {
    this.randomizerConfig = randomizerConfig;
  }

  @FXML
  private void initialize() {
    initializeButtonStates();
  }

  @FXML
  public void onUpdateCheck(ActionEvent event) {}

  private void initializeButtonStates() {
    autoUpdateToggleButton.setSelected(randomizerConfig.isAutoupdateEnabled());
    updateNotifierToggleButton.setSelected(randomizerConfig.isUpdateNotifier());

    // TODO: via bindings in viewmodel
    autoUpdateToggleButton
        .selectedProperty()
        .addListener((_, _, newVal) -> randomizerConfig.setAutoupdateEnabled(newVal));
    updateNotifierToggleButton
        .selectedProperty()
        .addListener((_, _, newVal) -> randomizerConfig.setUpdateNotifier(newVal));
  }

  public void onRepoLinkClicked(MouseEvent mouseEvent) throws IOException {
    // TODO: viewmodel logic
    Desktop.getDesktop().browse(URI.create(REPO_LINK));
  }
}
