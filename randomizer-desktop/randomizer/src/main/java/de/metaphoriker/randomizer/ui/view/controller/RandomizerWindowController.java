package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

@View
public class RandomizerWindowController implements Initializable {

  private final ViewProvider viewProvider;

  @FXML private GridPane contentPane;
  @FXML private VBox navigationBarHolder;

  @Inject
  public RandomizerWindowController(ViewProvider viewProvider) {
    this.viewProvider = viewProvider;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    loadControlBar();

    registerViewListener();
    setupControlBarClickTransparency();
  }

  // note: for this pickOnBounds have to be false
  private void setupControlBarClickTransparency() {
    navigationBarHolder.addEventFilter(
        MouseEvent.MOUSE_CLICKED,
        event -> {
          if (event.getTarget() == navigationBarHolder) {
            event.consume();
          }
        });
  }

  private void registerViewListener() {
    viewProvider.registerViewChangeListener(
        RandomizerWindowController.class, _ -> clearContent()); // self-call for clearance
    viewProvider.registerViewChangeListener(BuilderViewController.class, _ -> loadBuilderView());
  }

  private void loadControlBar() {
    Parent controlBarParent = viewProvider.requestView(NavigationBarController.class).parent();
    navigationBarHolder.getChildren().add(controlBarParent);
  }

  private void loadBuilderView() {
    Parent builderViewParent = viewProvider.requestView(BuilderViewController.class).parent();
    setContent(builderViewParent);
  }

  private void setContent(Node node) {
    clearContent();
    if (node != null) {
      contentPane.getChildren().add(node);
    }
  }

  private void clearContent() {
    contentPane.getChildren().clear();
  }
}
