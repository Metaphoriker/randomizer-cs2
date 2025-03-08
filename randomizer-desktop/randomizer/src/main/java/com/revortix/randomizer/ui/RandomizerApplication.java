package com.revortix.randomizer.ui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.revortix.model.action.sequence.ActionSequenceDispatcher;
import com.revortix.randomizer.Main;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.controller.RandomizerWindowController;
import de.metaphoriker.updater.Updater;
import de.metaphoriker.updater.util.JarFileUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    setupStage(stage, scene);
    stage.setOnCloseRequest(
        _ -> {
          try {
            GlobalScreen.unregisterNativeHook();
          } catch (NativeHookException e) {
            throw new RuntimeException(e);
          }
          ActionSequenceDispatcher.discardAllRunningActions();
          Updater.close();
          Platform.exit();
        });
    stage.show();
  }

  private void setupStage(Stage stage, Scene scene) {
    try {
      if (Main.isTestMode()) {
        stage.setTitle("Randomizer-CS2 - DEVELOPMENT");
      } else {
        stage.setTitle("Randomizer-CS2");
        Updater.getVersion(JarFileUtil.getJarFile(), Updater.FileType.RANDOMIZER)
            .thenAccept(version -> stage.setTitle("Randomizer-CS2 - " + version));
      }
    } catch (Exception e) {
      log.error("Fehler beim Laden der Version für Titel", e);
    }

    stage.getIcons().add(new Image("com/revortix/randomizer/images/randomizer.png"));
    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    stage.setMinWidth(MIN_WIDTH);
    stage.setMinHeight(MIN_HEIGHT);
    stage.setWidth(MIN_WIDTH);
    stage.setHeight(MIN_HEIGHT);
    stage.setScene(scene);
  }
}
