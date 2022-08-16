package dev.luzifer.ui;

import dev.luzifer.ui.view.ViewController;
import dev.luzifer.ui.view.viewmodel.RandomizerViewModel;
import dev.luzifer.ui.view.views.RandomizerView;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppStarter extends Application {
    
    private static volatile ApplicationState state = ApplicationState.IDLING;
    
    @Override
    public void start(Stage stage) {
        
        ViewController viewController = new ViewController(stage);
        viewController.showView(new RandomizerView(new RandomizerViewModel(() -> {
            if(state == ApplicationState.IDLING)
                state = ApplicationState.RUNNING;
            else
                state = ApplicationState.IDLING;
        })));
    }
    
    public static ApplicationState getState() {
        return state;
    }
}
