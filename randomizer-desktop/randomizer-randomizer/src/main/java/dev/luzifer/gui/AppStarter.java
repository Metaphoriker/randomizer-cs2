package dev.luzifer.gui;

import dev.luzifer.Main;
import dev.luzifer.gui.view.ViewController;
import dev.luzifer.gui.view.models.BuilderViewModel;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import dev.luzifer.gui.view.models.SelectionViewModel;
import dev.luzifer.gui.view.views.BuilderView;
import dev.luzifer.gui.view.views.RandomizerView;
import dev.luzifer.gui.view.views.SelectionView;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppStarter extends Application {
    
    @Override
    public void start(Stage stage) {
        
        ViewController viewController = new ViewController();
        viewController.showView(new SelectionView(new SelectionViewModel(
                () -> viewController.showView(new RandomizerView(new RandomizerViewModel())),
                () -> viewController.showView(new BuilderView(new BuilderViewModel(Main.getEventClusterRepository()))))));
    }
    
    @Override
    public void stop() {
        System.exit(0);
    }
}
