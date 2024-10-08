package de.metaphoriker.gui;

import de.metaphoriker.Main;
import de.metaphoriker.gui.view.ViewController;
import de.metaphoriker.gui.view.models.BuilderViewModel;
import de.metaphoriker.gui.view.models.GameViewModel;
import de.metaphoriker.gui.view.models.RandomizerViewModel;
import de.metaphoriker.gui.view.models.SelectionViewModel;
import de.metaphoriker.gui.view.views.BuilderView;
import de.metaphoriker.gui.view.views.InvasionGameView;
import de.metaphoriker.gui.view.views.RandomizerView;
import de.metaphoriker.gui.view.views.SelectionView;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppStarter extends Application {

  @Override
  public void start(Stage stage) {
    try {
      ViewController viewController = new ViewController();
      viewController.showView(
          new SelectionView(
              new SelectionViewModel(
                  () ->
                      viewController.showView(
                          new RandomizerView(
                              new RandomizerViewModel(Main.getEventClusterRepository()))),
                  () ->
                      viewController.showView(
                          new BuilderView(new BuilderViewModel(Main.getEventClusterRepository()))),
                  () -> viewController.showView(new InvasionGameView(new GameViewModel())),
                  viewController::switchTheme)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() {
    System.exit(0);
  }
}
