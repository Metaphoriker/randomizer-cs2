package de.metaphoriker;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

  @Override
  public void start(Stage stage) {
    try {
      Parent root = loadView(MainMenu.class, (Class<?> _) -> new MainMenu());
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

  public static <T> Parent loadView(Class<T> clazz, Callback<Class<?>, Object> controllerFactory) {
    FXMLLoader fxmlLoader = new FXMLLoader();

    URL fxmlLocation = clazz.getResource(clazz.getSimpleName() + ".fxml");

    fxmlLoader.setLocation(fxmlLocation);
    fxmlLoader.setControllerFactory(controllerFactory);

    try {
      return fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(
          MessageFormat.format("FXML could not get loaded for class: {0}", clazz), e);
    }
  }
}
