package de.metaphoriker.view.views;

import de.metaphoriker.view.View;
import de.metaphoriker.view.ViewProvider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@View
public class MainWindow extends HBox implements Initializable {

  private final ViewProvider viewProvider = ViewProvider.getInstance();

  @FXML private VBox sidebarPlaceholder;
  @FXML private GridPane contentPane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    loadSidebarView();
    loadBuilderView();
  }

  private void loadSidebarView() {
    Parent sidebarView = viewProvider.requestView(SidebarView.class);
    sidebarPlaceholder.getChildren().add(sidebarView);
  }

  private void loadBuilderView() {
    Parent builderView = viewProvider.requestView(BuilderView.class);
    setContentPane(builderView);
  }

  private void setContentPane(Parent node) {
    contentPane.getChildren().clear();
    contentPane.getChildren().add(node);
    System.out.println("Content pane children: " + contentPane.getChildren());
  }
}
