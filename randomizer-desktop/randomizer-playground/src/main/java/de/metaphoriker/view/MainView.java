package de.metaphoriker.view;

import de.metaphoriker.util.ImageUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

@View
public class MainView extends AnchorPane implements Initializable {

  private final ViewProvider viewProvider = ViewProvider.getInstance();

  private Parent builderView;

  // TODO: sidebar auch in eine eigene Klasse auslagern
  @FXML private GridPane contentPane;

  @FXML private Circle discordShape;

  @FXML private Circle gameShape;

  @FXML private Circle logoShape;

  @FXML private Circle randomizerShape;

  @FXML private Circle websiteShape;

  @FXML private Circle builderShape;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    loadBuilderView();
    setupGraphics();
  }

  private void loadBuilderView() {
    if (builderView == null) builderView = viewProvider.requestView(BuilderView.class);
    setContentPane(builderView);
  }

  private void setContentPane(Node node) {
    contentPane.getChildren().clear();
    contentPane.getChildren().add(node);

    System.out.println("Content pane children: " + contentPane.getChildren());
  }

  private void setupGraphics() {
    logoShape.setFill(ImageUtil.getRawImagePattern("images/randomizerIcon.png"));
    randomizerShape.setFill(
        ImageUtil.getRawImagePattern("images/randomizerIcon.png"));
    builderShape.setFill(ImageUtil.getRawImagePattern("images/discordIcon.png"));
    gameShape.setFill(ImageUtil.getRawImagePattern("images/gameIcon.png"));
    websiteShape.setFill(ImageUtil.getRawImagePattern("images/websiteIcon.png"));
    discordShape.setFill(ImageUtil.getRawImagePattern("images/discordIcon.png"));
  }
}
