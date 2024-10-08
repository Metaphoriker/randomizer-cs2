package de.metaphoriker;

import java.net.URL;
import java.util.ResourceBundle;

import de.metaphoriker.util.ImageUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainMenu extends AnchorPane implements Initializable {
  @FXML private Label randomizerIcon;
  @FXML private Label logbookIcon;
  @FXML private Label gameIcon;
  @FXML private Label discordIcon;
  @FXML private Label websiteIcon;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setUpGraphics();
  }

  private void setUpGraphics() {
    randomizerIcon.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    logbookIcon.setGraphic(ImageUtil.getImageView("images/logbookIcon.png"));
    gameIcon.setGraphic(ImageUtil.getImageView("images/gameIcon.png"));
    discordIcon.setGraphic(ImageUtil.getImageView("images/discordIcon.png"));
    websiteIcon.setGraphic(ImageUtil.getImageView("images/websiteIcon.png"));
  }
}