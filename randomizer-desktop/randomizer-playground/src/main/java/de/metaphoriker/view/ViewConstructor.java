package de.metaphoriker.view;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

public class ViewConstructor {

  public <T> Parent constructView(Class<T> clazz, Callback<Class<?>, Object> controllerFactory) {
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
