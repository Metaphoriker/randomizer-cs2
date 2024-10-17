package de.metaphoriker.view.views;

import com.google.inject.Inject;
import de.metaphoriker.view.View;
import de.metaphoriker.view.ViewProvider;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@View
public class MainWindow extends HBox implements Initializable {

  private final ViewProvider viewProvider;

  @FXML private VBox sidebarPlaceholder;
  @FXML private GridPane contentPane;

  @Inject
  public MainWindow(ViewProvider viewProvider) {
    this.viewProvider = viewProvider;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Platform.runLater(this::loadSidebarView);
    registerViewListener();
  }

  private void registerViewListener() {
    viewProvider.registerViewChangeListener(BuilderView.class, this::loadBuilderView);
    viewProvider.registerViewChangeListener(RandomizerView.class, this::loadRandomizerView);
  }

  private void loadSidebarView() {
    Parent sidebarView = viewProvider.requestView(SidebarView.class);
    VBox.setVgrow(sidebarView, Priority.ALWAYS);
    VBox.setVgrow(sidebarPlaceholder, Priority.ALWAYS);
    sidebarPlaceholder.getChildren().add(sidebarView);
  }

  private void loadRandomizerView() {
    Parent randomizerView = viewProvider.requestView(RandomizerView.class);
    setContentPane(randomizerView);
  }

  private void loadBuilderView() {
    Parent builderView = viewProvider.requestView(BuilderView.class);
    setContentPane(builderView);
  }

  private void setContentPane(Parent node) {
    contentPane.getChildren().clear();
    contentPane.getChildren().add(node);
  }
}
