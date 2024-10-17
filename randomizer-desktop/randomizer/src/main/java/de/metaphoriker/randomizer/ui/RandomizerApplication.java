package de.metaphoriker.randomizer.ui;

import de.metaphoriker.randomizer.Main;
import de.metaphoriker.randomizer.ui.util.ImageUtil;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.views.MainWindow;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerApplication extends Application {

  private static final int MIN_WIDTH = 700;
  private static final int MIN_HEIGHT = 500;

  @Override
  public void start(Stage stage) {
    log.debug("Starte Randomizer...");
    try {
      buildAndShowApplication(stage);
      log.debug("Hauptfenster angezeigt");
    } catch (Exception e) {
      log.error("Ein Fehler ist beim Starten der Anwendung aufgetreten", e);
    }
  }

  private void buildAndShowApplication(Stage stage) {
    ViewProvider viewProvider = Main.injector.getInstance(ViewProvider.class);
    log.debug("Lade Hauptfenster...");
    Parent root = viewProvider.requestView(MainWindow.class);
    Scene scene = new Scene(root);
    stage.setTitle("Randomizer");
    stage.getIcons().add(ImageUtil.getImage("images/randomizerLogo.jpg"));
    stage.setMinWidth(MIN_WIDTH);
    stage.setMinHeight(MIN_HEIGHT);
    stage.setOnCloseRequest(_ -> System.exit(0));
    stage.setScene(scene);
    stage.show();
  }
}
