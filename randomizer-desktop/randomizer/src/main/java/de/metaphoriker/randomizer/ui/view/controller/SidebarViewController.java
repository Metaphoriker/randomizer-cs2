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
import javafx.scene.shape.Rectangle;

@View
public class SidebarViewController implements Initializable {

  private final SidebarViewModel sidebarViewModel;
  private final ViewProvider viewProvider;

  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private Rectangle logoShape;
  @FXML private Button websiteButton;
  @FXML private Button discordButton;

  @Inject
  public SidebarViewController(SidebarViewModel sidebarViewModel, ViewProvider viewProvider) {
    this.sidebarViewModel = sidebarViewModel;
    this.viewProvider = viewProvider;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
    setupBindings();
    setupToggleButtonLogic();
  }

  private void setupGraphics() {
    logoShape.setFill(ImageUtil.getRawImagePattern("images/randomizerLogo.jpg"));
    websiteButton.setGraphic(ImageUtil.getImageView("images/websiteIcon.png"));
    discordButton.setGraphic(ImageUtil.getImageView("images/discordIcon.png"));
    randomizerButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    builderButton.setGraphic(ImageUtil.getImageView("images/logbookIcon.png"));
  }

  private void setupToggleButtonLogic() {
    randomizerButton
        .selectedProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue) {
                builderButton.setSelected(false);
                // sidebarViewModel.setSelectedView(RandomizerView.class);
              } else {
                sidebarViewModel.setSelectedView(null);
              }
            });

    builderButton
        .selectedProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue) {
                randomizerButton.setSelected(false);
                // sidebarViewModel.setSelectedView(BuilderView.class);
              } else {
                sidebarViewModel.setSelectedView(null);
              }
            });
  }

  private void setupBindings() {
    discordButton.setOnAction(_ -> sidebarViewModel.openDiscordLinkInBrowser());
    websiteButton.setOnAction(_ -> sidebarViewModel.openWebsiteLinkInBrowser());
    logoShape.setOnMouseClicked(_ -> sidebarViewModel.openLogoClickInBrowser());

    sidebarViewModel
        .getSelectedView()
        .addListener((_, _, newView) -> viewProvider.triggerViewChange(newView));
  }
}
