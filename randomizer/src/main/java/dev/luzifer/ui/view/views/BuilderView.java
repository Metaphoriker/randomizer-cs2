package dev.luzifer.ui.view.views;

import dev.luzifer.event.Event;
import dev.luzifer.event.EventDispatcher;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.BuilderViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class BuilderView extends View<BuilderViewModel> {
    
    // EventComponent
    @FXML
    private Pane root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    
    // Total root
    @FXML
    private Pane rootPane;
    
    // Bottom root
    @FXML
    private GridPane bottomRoot;
    
    // Left side bottom root
    @FXML
    private VBox eventVBox;
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    
        setupStyling();
        setupEventComponentDragActions();
        fillAndSetupBuilder();
    }
    
    private void setupStyling() {
    
        root.setStyle(STYLING_BORDER);
        nameLabel.setStyle(STYLING_HEADER);
        descriptionLabel.setStyle(STYLING_CONTENT);
    
        bottomRoot.setStyle(STYLING_BACKGROUND_DARKER + STYLING_BORDER);
        rootPane.setStyle(STYLING_BACKGROUND + STYLING_BORDER);
    }
    
    private void setupEventComponentDragActions() {
        
        double initialEventComponentX = root.getTranslateX();
        double initialEventComponentY = root.getTranslateY();
    
        root.setOnMouseDragged(drag -> {
        
            root.setManaged(false);
        
            // Hardcoded offsets make it jump centralized
            root.setTranslateX(drag.getX() + root.getTranslateX() - 200);
            root.setTranslateY(drag.getY() + root.getTranslateY() - 25);
        
            drag.consume();
        });
    
        root.setOnMouseReleased(release -> {
        
            root.setTranslateX(initialEventComponentX);
            root.setTranslateY(initialEventComponentY);
        
            root.setManaged(true);
        });
    }
    
    private void fillAndSetupBuilder() {
    
        for(Event event : EventDispatcher.getRegisteredEvents()) {
        
            Label label = new Label(event.getClass().getSimpleName());
            label.setFont(new Font("Arial", 16));
        
            label.setOnMouseEntered(enter -> label.setStyle(STYLING_SELECTED));
            label.setOnMouseExited(exit -> label.setStyle(STYLING_CLEAR));
            label.setOnMouseClicked(click -> {
                nameLabel.setText(event.getClass().getSimpleName());
                descriptionLabel.setText(event.description());
            });
        
            eventVBox.getChildren().add(label);
        }
    }
}
