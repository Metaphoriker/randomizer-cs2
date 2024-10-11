package de.metaphoriker;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class MainMenu extends AnchorPane implements Initializable {

  @FXML private AnchorPane contentPane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Parent builderMenu = Main.loadView(BuilderMenu.class, (Class<?> _) -> new BuilderMenu());
    setContentPane(builderMenu);
  }

  private void setContentPane(Node node) {
    contentPane.getChildren().clear();
    contentPane.getChildren().add(node);

    System.out.println("Content pane children: " + contentPane.getChildren());
  }
}
