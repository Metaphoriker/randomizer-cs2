package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@View
public class MainWindowController implements Initializable {

  private final ViewProvider viewProvider;

  @FXML private VBox sidebarPlaceholder;
  @FXML private GridPane contentPane;

  @Inject
  public MainWindowController(ViewProvider viewProvider) {
    this.viewProvider = viewProvider;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Platform.runLater(this::loadSidebarView);
    registerViewListener();
  }

  private void registerViewListener() {
    viewProvider.registerViewChangeListener(
        SidebarViewController.class,
        _ -> setContent(viewProvider.requestView(SidebarViewController.class).getParent()));
  }

  private void loadSidebarView() {
    Parent sidebarView = viewProvider.requestView(SidebarViewController.class).getParent();
    VBox.setVgrow(sidebarView, Priority.ALWAYS);
    VBox.setVgrow(sidebarPlaceholder, Priority.ALWAYS);
    sidebarPlaceholder.getChildren().add(sidebarView);
  }

  private void setContent(Parent node) {
    contentPane.getChildren().clear();
    if (node != null) {
      contentPane.getChildren().add(node);
    }
  }
}
