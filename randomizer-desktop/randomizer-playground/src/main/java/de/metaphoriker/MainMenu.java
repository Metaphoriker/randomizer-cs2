package de.metaphoriker;

import java.net.URL;
import java.util.ResourceBundle;

import de.metaphoriker.util.ImageUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

public class MainMenu extends AnchorPane implements Initializable {

  @FXML private GridPane contentPane;

  @FXML private Circle discordShape;

  @FXML private Circle gameShape;

  @FXML private Circle logoShape;

  @FXML private Circle randomizerShape;

  @FXML private Circle websiteShape;

  @FXML private Circle builderShape;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Parent builderMenu = Main.loadView(BuilderMenu.class, (Class<?> _) -> new BuilderMenu());
    setContentPane(builderMenu);
    setupGraphics();
  }

  private void setContentPane(Node node) {
    contentPane.getChildren().clear();
    contentPane.getChildren().add(node);

    System.out.println("Content pane children: " + contentPane.getChildren());
  }

  private void setupGraphics() {
    logoShape.setFill(ImageUtil.getRawImagePattern("/de/metaphoriker/images/randomizerIcon.png"));
    randomizerShape.setFill(
        ImageUtil.getRawImagePattern("/de/metaphoriker/images/randomizerIcon.png"));
    builderShape.setFill(ImageUtil.getRawImagePattern("/de/metaphoriker/images/discordIcon.png"));
    gameShape.setFill(ImageUtil.getRawImagePattern("/de/metaphoriker/images/gameIcon.png"));
    websiteShape.setFill(ImageUtil.getRawImagePattern("/de/metaphoriker/images/websiteIcon.png"));
    discordShape.setFill(ImageUtil.getRawImagePattern("/de/metaphoriker/images/discordIcon.png"));
  }
}
