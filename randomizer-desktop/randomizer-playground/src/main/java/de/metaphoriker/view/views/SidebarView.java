package de.metaphoriker.view.views;

import de.metaphoriker.util.ImageUtil;
import java.net.URL;
import java.util.ResourceBundle;

import de.metaphoriker.view.View;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

@View
public class SidebarView extends VBox implements Initializable {

  @FXML private Circle discordShape;
  @FXML private Circle gameShape;
  @FXML private Circle logoShape;
  @FXML private Circle randomizerShape;
  @FXML private Circle websiteShape;
  @FXML private Circle builderShape;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
  }

  private void setupGraphics() {
    setImagePattern(logoShape, "images/randomizerIcon.png");
    setImagePattern(randomizerShape, "images/randomizerIcon.png");
    setImagePattern(builderShape, "images/discordIcon.png");
    setImagePattern(gameShape, "images/gameIcon.png");
    setImagePattern(websiteShape, "images/websiteIcon.png");
    setImagePattern(discordShape, "images/discordIcon.png");
  }

  private void setImagePattern(Circle shape, String imagePath) {
    System.out.println("Setting image pattern for: " + imagePath);
    ImagePattern imagePattern = ImageUtil.getRawImagePattern(imagePath);
    if (imagePattern != null) {
      shape.setFill(imagePattern);
      System.out.println("Successfully set image pattern for: " + imagePath);
    } else {
      System.err.println("Failed to set image pattern for: " + imagePath);
    }
  }
}
