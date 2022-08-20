package dev.luzifer.ui;

import dev.luzifer.ui.view.ViewController;
import dev.luzifer.ui.view.viewmodel.BuilderViewModel;
import dev.luzifer.ui.view.viewmodel.RandomizerViewModel;
import dev.luzifer.ui.view.viewmodel.SelectionViewModel;
import dev.luzifer.ui.view.views.BuilderView;
import dev.luzifer.ui.view.views.RandomizerView;
import dev.luzifer.ui.view.views.SelectionView;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppStarter extends Application {
    
    @Override
    public void start(Stage stage) {
        
        ViewController viewController = new ViewController();
        viewController.showView(new SelectionView(new SelectionViewModel(
                () -> viewController.showView(new RandomizerView(new RandomizerViewModel())),
                () -> viewController.showView(new BuilderView(new BuilderViewModel())))));
    }
    
    @Override
    public void stop() {
        System.exit(0);
    }
}
