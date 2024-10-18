package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.util.ImageUtil;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.SidebarViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

@View
public class SidebarView extends VBox implements Initializable {

  private final SidebarViewModel sidebarViewModel;
  private final ViewProvider viewProvider;

  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private Rectangle logoShape;
  @FXML private Button websiteButton;
  @FXML private Button discordButton;

  @Inject
  public SidebarView(SidebarViewModel sidebarViewModel, ViewProvider viewProvider) {
    this.sidebarViewModel = sidebarViewModel;
    this.viewProvider = viewProvider;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
    setupToggles();
    setupClickInteractions();
  }

  private void setupClickInteractions() {
    logoShape.setOnMouseClicked(_ -> sidebarViewModel.openLogoClickInBrowser());
    discordButton.setOnAction(_ -> sidebarViewModel.openDiscordLinkInBrowser());
    websiteButton.setOnAction(_ -> sidebarViewModel.openWebsiteLinkInBrowser());
  }

  private void setupGraphics() {
    logoShape.setFill(ImageUtil.getRawImagePattern("images/randomizerLogo.jpg"));
    websiteButton.setGraphic(ImageUtil.getImageView("images/websiteIcon.png"));
    discordButton.setGraphic(ImageUtil.getImageView("images/discordIcon.png"));
    randomizerButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    builderButton.setGraphic(ImageUtil.getImageView("images/logbookIcon.png"));
  }

  private void setupToggles() {
    randomizerButton.setOnAction(
        _ -> updateButtonStateAndToggleView(randomizerButton, RandomizerView.class));
    builderButton.setOnAction(
        _ -> updateButtonStateAndToggleView(builderButton, SequencesView.class));
  }

  private void updateButtonStateAndToggleView(ToggleButton selectedButton, Class<?> viewClass) {
    randomizerButton.setSelected(selectedButton == randomizerButton);
    builderButton.setSelected(selectedButton == builderButton);

    if (viewClass != null) {
      viewProvider.triggerViewChange(viewClass);
    }
  }
}
