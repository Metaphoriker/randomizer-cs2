package de.metaphoriker.randomizer.ui.view;

import de.metaphoriker.randomizer.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

@Slf4j
public class ViewLoader {

    /**
     * Loads a view and its controller from an FXML file associated with the specified class.
     *
     * @param <T>   the type of the controller
     * @param clazz the class of the controller for the corresponding FXML file
     *
     * @return a {@link ViewWrapper} containing the loaded view and its controller
     *
     * @throws IllegalStateException if the FXML file could not be found or loaded
     */
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
            log.debug("Lade View {}", name);
            Parent parent = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            return new ViewWrapper<>(parent, controller);
        } catch (IOException e) {
            throw new IllegalStateException(
                    MessageFormat.format("FXML konnte nicht geladen werden für die Klasse: {0}", clazz), e);
        }
    }
}
