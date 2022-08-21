package dev.luzifer.ui.view.views;

import com.google.gson.JsonSyntaxException;
import dev.luzifer.Main;
import dev.luzifer.backend.event.Event;
import dev.luzifer.backend.event.cluster.EventCluster;
import dev.luzifer.backend.json.JsonUtil;
import dev.luzifer.ui.util.Styling;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.component.components.EventComponent;
import dev.luzifer.ui.view.component.components.MiniEventComponent;
import dev.luzifer.ui.view.models.BuilderViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

// TODO: A Section to build events from scratch
public class BuilderView extends View<BuilderViewModel> {
    
    // Total root
    @FXML
    private Pane rootPane;
    
    // Top root
    @FXML
    private Label topPaneTitle;
    
    @FXML
    private GridPane topRoot;
    
    @FXML
    private VBox clusterVBox;
    
    @FXML
    private Label dragAndDropLabel;
    
    @FXML
    private ScrollPane clusterBuilderScrollPane;
    
    @FXML
    private VBox clusterBuilderVBox;
    
    // Bottom root
    @FXML
    private Label bottomPaneTitle;
    
    @FXML
    private GridPane bottomRoot;
    
    @FXML
    private EventComponent eventComponent;
    
    @FXML
    private VBox eventVBox;
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    
        setupStyling();
        setupEventComponentDragAndDropActions();
        fillAndSetupVBoxes();
    }
    
    private void setupStyling() {
    
        eventComponent.setStyle(Styling.BORDER);
        topPaneTitle.setStyle(Styling.HEADER + Styling.BORDER);
        bottomPaneTitle.setStyle(Styling.HEADER + Styling.BORDER);
        topRoot.setStyle(Styling.BACKGROUND_DARKER);
        bottomRoot.setStyle(Styling.BACKGROUND_DARKER);
        rootPane.setStyle(Styling.BACKGROUND);
        
        getIcons().add(new Image("images/16x16/builder16x16.png"));
    }
    
    private void setupEventComponentDragAndDropActions() {
    
        eventComponent.setOnDragDetected(event -> {
        
            if(!eventComponent.getModel().isEventAccepted()) {
                
                eventComponent.setStyle(Styling.BORDER_RED);
                Main.getScheduler().schedule(() -> eventComponent.setStyle(Styling.BORDER), 150);
                
                return;
            }
            
            Dragboard db = eventComponent.startDragAndDrop(TransferMode.ANY);
            db.setDragView(eventComponent.snapshot(null, null), event.getX(), event.getY());
        
            ClipboardContent content = new ClipboardContent();
            content.putString(JsonUtil.serialize(eventComponent.getModel().getEventWrapped()));
            
            db.setContent(content);
            event.consume();
        });
        eventComponent.setOnDragDone(event -> {
        
            if(clusterBuilderVBox.getChildren().isEmpty())
                dragAndDropLabel.setVisible(true);
            
            event.consume();
        });
        
        clusterBuilderScrollPane.setOnDragOver(event -> {
    
            if (event.getGestureSource() != clusterBuilderScrollPane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
    
            dragAndDropLabel.setVisible(false);
            event.consume();
        });
        clusterBuilderScrollPane.setOnDragDropped(event -> {
    
            Dragboard db = event.getDragboard();
            boolean success = false;
    
            if (db.hasString()) {
    
                try {
    
                    Event eventWrapped = JsonUtil.deserialize(db.getString());
                    clusterBuilderVBox.getChildren().add(createMiniEventComponent(eventWrapped));
    
                    success = true;
                } catch (JsonSyntaxException ignored) { // this could be probably done better
                }
            }
    
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    private MiniEventComponent createMiniEventComponent(Event event) {
        
        MiniEventComponent miniEventComponent = new MiniEventComponent();
        setupMiniEventComponentDragAndDropActions(miniEventComponent);
        miniEventComponent.getModel().acceptEvent(event);
        
        return miniEventComponent;
    }
    
    private void setupMiniEventComponentDragAndDropActions(MiniEventComponent miniEventComponent) {
        
        miniEventComponent.setOnDragDropped(dragEvent -> {
        
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
        
            if (dragboard.hasString()) {
            
                int index = clusterBuilderVBox.getChildren().indexOf(miniEventComponent);
                clusterBuilderVBox.getChildren().add(index+1, createMiniEventComponent(JsonUtil.deserialize(dragboard.getString())));
            
                success = true;
            }
        
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
        miniEventComponent.setOnDragEntered(dragEvent -> miniEventComponent.setStyle(Styling.BOTTOM_BORDER_BLUE));
        miniEventComponent.setOnDragExited(dragEvent -> miniEventComponent.setStyle(Styling.CLEAR));
        miniEventComponent.setOnDragDetected(dragEvent -> {
        
            Dragboard db = miniEventComponent.startDragAndDrop(TransferMode.ANY);
            db.setDragView(miniEventComponent.snapshot(null, null), dragEvent.getX(), dragEvent.getY());
        
            ClipboardContent content = new ClipboardContent();
            content.putString(JsonUtil.serialize(miniEventComponent.getModel().getEventWrapped()));
        
            db.setContent(content);
            dragEvent.consume();
        });
        miniEventComponent.setOnDragDone(dragEvent -> {
        
            if(clusterBuilderVBox.getChildren().isEmpty())
                dragAndDropLabel.setVisible(true);
            
            clusterBuilderVBox.getChildren().remove(miniEventComponent);
        
            dragEvent.consume();
        });
    }
    
    private void fillAndSetupVBoxes() {
    
        for(Event event : getViewModel().getEvents()) {
        
            Label label = new Label(event.getClass().getSimpleName());
            label.setFont(new Font("Arial", 16));
            
            if(!getViewModel().isEventEnabled(event)) {
                label.setStyle(Styling.FONT_RED);
                label.setDisable(true);
            }
            
            label.setOnMouseEntered(enter -> label.setStyle(Styling.SELECTED));
            label.setOnMouseExited(exit -> label.setStyle(Styling.CLEAR));
            label.setOnMouseClicked(click -> eventComponent.getModel().acceptEvent(event));
        
            eventVBox.getChildren().add(label);
        }
        
        for(EventCluster eventCluster : getViewModel().getEventClusters()) {
            clusterVBox.getChildren().add(new Label(eventCluster.getName()));
            // TODO
        }
    }
}
