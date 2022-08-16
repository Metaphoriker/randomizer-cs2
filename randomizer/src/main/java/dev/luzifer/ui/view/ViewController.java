package dev.luzifer.ui.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

public class ViewController {
    
    private final Stage stage;
    
    public ViewController(Stage stage) {
        this.stage = stage;
    }
    
    public void showView(View view) {
        loadAndShowView(view.getClass(), (Class<?> param) -> view,
                view.getClass().getSimpleName().substring(0, view.getClass().getSimpleName().length() - 4));
    }
    
    private <T> void loadAndShowView(Class<T> clazz, Callback<Class<?>, Object> controllerFactory, String title) {
        
        Parent root = loadView(clazz, controllerFactory);
        Scene scene = new Scene(root);
        
        stage.getIcons().addAll(new Image("images/random_icon.png"));
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.show();
    }
    
    private <T> Parent loadView(Class<T> clazz, Callback<Class<?>, Object> controllerFactory) {
        
        FXMLLoader fxmlLoader = new FXMLLoader();
        
        URL fxmlLocation = clazz.getResource(clazz.getSimpleName() + ".fxml");
        
        fxmlLoader.setLocation(fxmlLocation);
        fxmlLoader.setControllerFactory(controllerFactory);
        
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException(MessageFormat.format("FXML could not get loaded for class: {0}", clazz), e);
        }
    }
    
}