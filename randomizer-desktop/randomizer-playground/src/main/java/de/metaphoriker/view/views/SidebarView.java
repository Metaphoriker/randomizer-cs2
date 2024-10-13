package de.metaphoriker.view.views;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.View;
import de.metaphoriker.view.ViewProvider;
import de.metaphoriker.view.viewmodel.SidebarViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

@View
public class SidebarView extends VBox implements Initializable {

  private final ViewProvider viewProvider = ViewProvider.getInstance();
  private final SidebarViewModel sidebarViewModel = new SidebarViewModel();

  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private ToggleButton gameButton;
  @FXML private Circle discordShape;
  @FXML private Circle logoShape;
  @FXML private Circle websiteShape;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
    setupButtonImages();
    setupToggles();
    setupClickInteractions();
  }

  private void setupGraphics() {
    setImagePattern(logoShape, "images/randomizerIcon.png");
    setImagePattern(websiteShape, "images/websiteIcon.png");
    setImagePattern(discordShape, "images/discordIcon.png");
  }

  private void setupClickInteractions() {
    discordShape.setOnMouseClicked(_ -> sidebarViewModel.openDiscordLinkInBrowser());
    websiteShape.setOnMouseClicked(_ -> sidebarViewModel.openWebsiteLinkInBrowser());
  }

  private void setupButtonImages() {
    randomizerButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    builderButton.setGraphic(ImageUtil.getImageView("images/logbookIcon.png"));
    gameButton.setGraphic(ImageUtil.getImageView("images/gameIcon.png"));
  }

  private void setupToggles() {
    randomizerButton.setOnAction(
        _ -> {
          randomizerButton.setSelected(true);
          builderButton.setSelected(false);
          gameButton.setSelected(false);
        });
    builderButton.setOnAction(
        _ -> {
          randomizerButton.setSelected(false);
          builderButton.setSelected(true);
          gameButton.setSelected(false);
          viewProvider.triggerViewChange(BuilderView.class);
        });
    gameButton.setOnAction(
        _ -> {
          randomizerButton.setSelected(false);
          builderButton.setSelected(false);
          gameButton.setSelected(true);
        });
  }

  private void setImagePattern(Circle shape, String imagePath) {
    ImagePattern imagePattern = ImageUtil.getRawImagePattern(imagePath);
    shape.setFill(imagePattern);
  }
}
