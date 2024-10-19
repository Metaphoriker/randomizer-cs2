package de.metaphoriker.randomizer.ui.view;

import de.metaphoriker.randomizer.Main;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewLoader {

  public <T> ViewWrapper<T> loadView(Class<T> clazz) {
    FXMLLoader fxmlLoader = new FXMLLoader();

    String name = clazz.getSimpleName().replace("Controller", "");
    URL fxmlLocation = clazz.getResource(name + ".fxml");
    if (fxmlLocation == null) {
      throw new IllegalStateException(
          MessageFormat.format("FXML Datei konnte nicht gefunden werden für Klasse: {0}", clazz));
    }

    fxmlLoader.setLocation(fxmlLocation);
    fxmlLoader.setControllerFactory(param -> Main.getInjector().getInstance(param));

    try {
      log.debug("Lade View {}, FXML Datei: {}", clazz.getSimpleName(), fxmlLocation);
      Parent parent = fxmlLoader.load();
      T controller = fxmlLoader.getController();
      return new ViewWrapper<>(parent, controller);
    } catch (IOException e) {
      throw new IllegalStateException(
          MessageFormat.format("FXML konnte nicht geladen werden für die Klasse: {0}", clazz), e);
    }
  }
}
