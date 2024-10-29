package de.metaphoriker.randomizer.ui;

import de.metaphoriker.randomizer.Main;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.controller.RandomizerWindowController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomizerApplication extends Application {

    private static final int MIN_WIDTH = 704;
    private static final int MIN_HEIGHT = 536;

    @Override
    public void start(Stage stage) {
        log.debug("Starte Randomizer...");
        try {
            Thread.setDefaultUncaughtExceptionHandler(new UIUncaughtExceptionHandler());
            buildAndShowApplication(stage);
            log.debug("Hauptfenster angezeigt");
        } catch (Exception e) {
            log.error("Ein Fehler ist beim Starten der Anwendung aufgetreten", e);
        }
    }

    private void buildAndShowApplication(Stage stage) {
        ViewProvider viewProvider = Main.getInjector().getInstance(ViewProvider.class);
        log.debug("Lade Hauptfenster...");
        Parent root = viewProvider.requestView(RandomizerWindowController.class).parent();
        Scene scene = new Scene(root);
        stage.setTitle("Randomizer-CS2");
        stage.getIcons().add(new Image("de/metaphoriker/randomizer/images/randomizer.png"));
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setOnCloseRequest(_ -> System.exit(0));
        stage.setScene(scene);
        stage.show();
    }

    public static class UIUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Unexpected error", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An unexpected error occured");
            alert.setHeaderText("Please open an Issue on GitHub with the following text:");
            alert.setContentText(e.toString());
            Throwable cause = e.getCause();
            while (cause != null) {
                e = cause;
                cause = e.getCause();
            }
            alert.setContentText("An unexpected error occurred:\n" + e);
            alert.showAndWait();
        }
    }
}
