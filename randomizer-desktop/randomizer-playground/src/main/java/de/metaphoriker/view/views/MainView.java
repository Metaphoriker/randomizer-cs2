package de.metaphoriker.view.views;

import de.metaphoriker.view.View;
import de.metaphoriker.view.ViewProvider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

@View
public class MainView extends HBox implements Initializable {

  private final ViewProvider viewProvider = ViewProvider.getInstance();

  private Parent sidebarView;
  private Parent builderView;

  @FXML private GridPane contentPane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    loadSidebarView();
    loadBuilderView();
  }

  private void loadSidebarView() {
    if (sidebarView == null) sidebarView = viewProvider.requestView(SidebarView.class);
    getChildren().addFirst(sidebarView);
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
}
