package de.metaphoriker;

import de.metaphoriker.view.views.MainWindow;
import de.metaphoriker.view.ViewProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  private final ViewProvider viewProvider = ViewProvider.getInstance();

  @Override
  public void start(Stage stage) {
    try {
      Parent root = viewProvider.requestView(MainWindow.class);

      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      stage.setScene(scene);
      stage.show();

      Platform.runLater(() -> setMinResizable(stage));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setMinResizable(Stage stage) {
    stage.setMinWidth(800);
    stage.setMinHeight(600);
    stage.setResizable(true);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
