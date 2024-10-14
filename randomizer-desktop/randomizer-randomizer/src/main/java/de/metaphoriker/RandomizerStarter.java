package de.metaphoriker;

import com.google.inject.Guice;
import de.metaphoriker.model.ModelModule;
import de.metaphoriker.view.ViewProvider;
import de.metaphoriker.view.views.MainWindow;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerStarter extends Application {

  @Override
  public void start(Stage stage) {
    log.debug("Starte Randomizer...");

    Main.injector = Guice.createInjector(new ModelModule(), new AppModule());
    Main mainInstance = Main.injector.getInstance(Main.class);
    try {
      mainInstance.startApplication();
    } catch (IOException | URISyntaxException e) {
      log.error("Fehler beim Starten der Applikation", e);
    }

    ViewProvider viewProvider = Main.injector.getInstance(ViewProvider.class);

    try {
      log.debug("Lade Hauptfenster...");
      Parent root = viewProvider.requestView(MainWindow.class);
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      stage.setTitle("Randomizer Playground");
      stage.setScene(scene);
      stage.show();
      log.debug("Hauptfenster angezeigt");
    } catch (Exception e) {
      log.error("Ein Fehler ist beim Starten der Anwendung aufgetreten", e);
    }
  }
}
