package dev.luzifer.ui.view.component;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Component<M extends ComponentModel> extends Pane implements Initializable {
    
    private final M model;
    
    protected Component(M model) {
        
        this.model = model;
    
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName() + ".fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setControllerFactory(clazz -> this);
    
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.out.println("Failed to load FXML for " + getClass().getSimpleName() + "|" + exception.getMessage());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    
    public M getModel() {
        return model;
    }
}
