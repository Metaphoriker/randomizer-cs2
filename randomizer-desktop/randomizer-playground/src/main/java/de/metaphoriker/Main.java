package de.metaphoriker;

import de.metaphoriker.view.views.MainView;
import de.metaphoriker.view.ViewProvider;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  private final ViewProvider viewProvider = ViewProvider.getInstance();

  @Override
  public void start(Stage stage) {
    try {
      Parent root = viewProvider.requestView(MainView.class);

      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
