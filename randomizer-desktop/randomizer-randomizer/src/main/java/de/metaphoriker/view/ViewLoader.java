package de.metaphoriker.view;

import de.metaphoriker.Main;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewLoader {

  public <T> Parent loadView(Class<T> clazz) {
    FXMLLoader fxmlLoader = new FXMLLoader();

    URL fxmlLocation = clazz.getResource(clazz.getSimpleName() + ".fxml");
    if (fxmlLocation == null) {
      throw new IllegalStateException(
          MessageFormat.format("FXML Datei konnte nicht gefunden werden für Klasse: {0}", clazz));
    }

    fxmlLoader.setLocation(fxmlLocation);
    fxmlLoader.setControllerFactory(param -> Main.injector.getInstance(param));

    try {
      return fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(
          MessageFormat.format("FXML konnte nicht geladen werden für die Klasse: {0}", clazz), e);
    }
  }
}
