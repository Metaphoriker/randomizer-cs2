package de.metaphoriker.view.views;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.View;
import de.metaphoriker.view.viewmodel.SidebarViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

@View
public class SidebarView extends VBox implements Initializable {

  private final SidebarViewModel sidebarViewModel = new SidebarViewModel();

  @FXML private Circle discordShape;
  @FXML private Circle gameShape;
  @FXML private Circle logoShape;
  @FXML private Circle randomizerShape;
  @FXML private Circle websiteShape;
  @FXML private Circle builderShape;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
    setupClickInteractions();
  }

  private void setupGraphics() {
    setImagePattern(logoShape, "images/randomizerIcon.png");
    setImagePattern(randomizerShape, "images/randomizerIcon.png");
    setImagePattern(builderShape, "images/discordIcon.png");
    setImagePattern(gameShape, "images/gameIcon.png");
    setImagePattern(websiteShape, "images/websiteIcon.png");
    setImagePattern(discordShape, "images/discordIcon.png");
  }

  private void setupClickInteractions() {
    discordShape.setOnMouseClicked(_ -> sidebarViewModel.openDiscordLinkInBrowser());
    websiteShape.setOnMouseClicked(_ -> sidebarViewModel.openWebsiteLinkInBrowser());
  }

  private void setImagePattern(Circle shape, String imagePath) {
    ImagePattern imagePattern = ImageUtil.getRawImagePattern(imagePath);
    shape.setFill(imagePattern);
  }
}
