package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.component.MinMaxSlider;
import com.revortix.randomizer.ui.view.viewmodel.settings.GeneralSettingsViewModel;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@View
public class GeneralSettingsController {

  private static final String CONFIG_FILE_TITLE = "Choose CS2 Config";
  private static final String CONFIG_FILE_EXTENSION = "*.vcfg";
  private static final String CONFIG_FILE_DESCRIPTION = "VCFG";

  private final GeneralSettingsViewModel generalSettingsViewModel;

  @FXML private ToggleButton showIntroToggleButton;
  @FXML private Button syncConfigButton;
  @FXML private TextField configPathTextField;
  @FXML private MinMaxSlider minMaxSlider;
  @FXML private Label syncFailedIndicator;
  @FXML private Label spentTimeLabel;

  @Inject
  public GeneralSettingsController(GeneralSettingsViewModel generalSettingsViewModel) {
    this.generalSettingsViewModel = generalSettingsViewModel;
  }

  @FXML
  private void initialize() {
    initializeSpentTimeLabel();
    generalSettingsViewModel.setupViewModel();
    setupSettingsOptions();
    setupIntervalSlider();
  }

  private void initializeSpentTimeLabel() {
    generalSettingsViewModel
        .getSpentTimeProperty()
        .addListener(
            (_, _, newValue) -> spentTimeLabel.setText(longInHours(newValue.longValue()) + "h"));
  }

  private int longInHours(long timestamp) {
    return (int) (timestamp / 3600000);
  }

  private void setupSettingsOptions() {
    configPathTextField
        .textProperty()
        .bindBidirectional(generalSettingsViewModel.getConfigPathProperty());
    configPathTextField.setText(generalSettingsViewModel.getConfigPath());
    syncConfigButton.setTooltip(new Tooltip("Sync"));
    showIntroToggleButton
        .selectedProperty()
        .bindBidirectional(generalSettingsViewModel.getShowIntroProperty());
  }

  private void setupIntervalSlider() {
    Platform.runLater(
        () ->
            minMaxSlider.setMinMaxValue(
                generalSettingsViewModel.getMinIntervalProperty().get(),
                generalSettingsViewModel.getMaxIntervalProperty().get()));

    minMaxSlider
        .getMinProperty()
        .bindBidirectional(generalSettingsViewModel.getMinIntervalProperty());
    minMaxSlider
        .getMaxProperty()
        .bindBidirectional(generalSettingsViewModel.getMaxIntervalProperty());
  }

  @FXML
  private void onConfigSync(ActionEvent event) {
    generalSettingsViewModel
        .loadConfigs()
        .thenRunAsync(
            () -> {
              syncConfigButton.getStyleClass().add("sync-config-path-success");
              syncFailedIndicator.setVisible(false);
            },
            Platform::runLater)
        .exceptionallyAsync(
            e -> {
              syncConfigButton.getStyleClass().add("sync-config-path-failed");
              syncFailedIndicator.setVisible(true);
              return null;
            },
            Platform::runLater);
  }

  @FXML
  private void onConfigChoose(ActionEvent event) {
    String currentConfigPath = generalSettingsViewModel.getCurrentConfigPath();

    File selectedFile = showConfigFileChooser(currentConfigPath);

    if (selectedFile != null) {
      log.info("Selected config file: {}", selectedFile.getAbsolutePath());
      String normalizedPath = normalizeFilePath(selectedFile.getAbsolutePath());
      generalSettingsViewModel.setConfigPath(normalizedPath);
    }
  }

  private File showConfigFileChooser(String currentPath) {
    FileChooser fileChooser = new FileChooser();
    configureFileChooser(fileChooser, currentPath);
    return fileChooser.showOpenDialog(syncConfigButton.getScene().getWindow());
  }

  private void configureFileChooser(FileChooser fileChooser, String currentPath) {
    Path path = Path.of(currentPath);

    if (!Files.exists(path)) {
      fileChooser.setInitialDirectory(Path.of(System.getProperty("user.home")).toFile());
    } else {
      fileChooser.setInitialDirectory(path.toFile().getParentFile());
      fileChooser.setInitialFileName(path.getFileName().toString());
    }

    fileChooser.setTitle(CONFIG_FILE_TITLE);
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter(CONFIG_FILE_DESCRIPTION, CONFIG_FILE_EXTENSION));
  }

  private String normalizeFilePath(String filePath) {
    return filePath.replace("\\", "/");
  }
}
