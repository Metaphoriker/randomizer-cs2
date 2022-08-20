package dev.luzifer.ui.view.views;

import dev.luzifer.event.Event;
import dev.luzifer.event.EventDispatcher;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.BuilderViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
    
    @FXML
    private ScrollPane testScrollPane;
    
    @FXML
    private VBox testVBox;
    
    @FXML
    private Label bottomPaneTitle;
    
    // Bottom root
    @FXML
    private GridPane bottomRoot;
    
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
        
        bottomPaneTitle.setStyle(STYLING_HEADER + STYLING_BORDER);
    
        bottomRoot.setStyle(STYLING_BACKGROUND_DARKER);
        rootPane.setStyle(STYLING_BACKGROUND + STYLING_BORDER);
        
        getIcons().clear();
        getIcons().add(new Image("images/16x16/builder16x16.png"));
    }
    
    private void setupEventComponentDragActions() {
        
        double initialEventComponentX = root.getTranslateX();
        double initialEventComponentY = root.getTranslateY();
    
        // wtf is this?
        root.setOnMouseClicked(enter -> changeParent(root, rootPane));
        
        root.setOnMouseDragged(drag -> {
    
            root.setManaged(false);
            
            // Hardcoded offsets make it jump centralized
            root.setTranslateX(drag.getX() + root.getTranslateX() - 200);
            root.setTranslateY(drag.getY() + root.getTranslateY() - 25);
            
            drag.consume();
        });
        
        root.setOnMouseDragReleased(release -> {
            
            root.setTranslateX(initialEventComponentX);
            root.setTranslateY(initialEventComponentY);
        
            root.setManaged(true);
            
            release.consume();
        });
        
        testVBox.setOnDragOver(drag -> {
            drag.acceptTransferModes(TransferMode.COPY);
            drag.consume();
        });
    
        testVBox.setOnDragDropped(drop -> {
            
            changeParent(root, testVBox);
            drop.setDropCompleted(true);
            
            drop.consume();
        });
    }
    
    private void fillAndSetupBuilder() {
    
        for(Event event : EventDispatcher.getRegisteredEvents()) {
        
            Label label = new Label(event.getClass().getSimpleName());
            label.setFont(new Font("Arial", 16));
        
            label.setOnMouseEntered(enter -> label.setStyle(STYLING_SELECTED));
            label.setOnMouseExited(exit -> label.setStyle(STYLING_CLEAR));
            label.setOnMouseClicked(click -> {
                nameLabel.setText(event.name());
                descriptionLabel.setText(event.description());
            });
        
            eventVBox.getChildren().add(label);
        }
    }
}
