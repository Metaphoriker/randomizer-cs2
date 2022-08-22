package dev.luzifer.ui.view.views;

import com.google.gson.JsonSyntaxException;
import dev.luzifer.backend.event.Event;
import dev.luzifer.backend.event.cluster.EventCluster;
import dev.luzifer.backend.json.JsonUtil;
import dev.luzifer.ui.util.ImageUtil;
import dev.luzifer.ui.util.Styling;
import dev.luzifer.ui.view.View;
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
    
    @FXML
    private Pane panePane;
    
    @FXML
    private Label panePaneTitle;
    
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
    private VBox eventVBox;
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        setupStyling();
        setupClusterBuilderVBoxAcceptDrag();
        
        fillVBoxes();
    }
    
    private void setupStyling() {
        
        topPaneTitle.setStyle(Styling.HEADER + Styling.BORDER);
        panePaneTitle.setStyle(Styling.HEADER + Styling.BORDER);
        
        topRoot.setStyle(Styling.BACKGROUND_DARKER);
        panePane.setStyle(Styling.BACKGROUND_DARKER);
        rootPane.setStyle(Styling.BACKGROUND);
        
        getIcons().add(ImageUtil.getImage("images/build_icon.png"));
    }
    
    private void setupEventLabelDragAndDropActions(Label label) {
        
        Event event = getViewModel().getEvent(label.getText()); // the event that this label represents
    
        label.setOnDragDetected(dragEvent -> {
            
            Dragboard db = label.startDragAndDrop(TransferMode.ANY);
            db.setDragView(label.snapshot(null, null), dragEvent.getX(), dragEvent.getY()); // TODO: MiniEventComponent as drag view
            
            ClipboardContent content = new ClipboardContent();
            content.putString(JsonUtil.serialize(event));
            
            db.setContent(content);
            dragEvent.consume();
        });
        label.setOnDragDone(dragEvent -> {
            
            if (clusterBuilderVBox.getChildren().isEmpty())
                dragAndDropLabel.setVisible(true);
            
            dragEvent.consume();
        });
    }
    
    private void setupClusterBuilderVBoxAcceptDrag() {
        
        clusterBuilderScrollPane.setOnDragOver(dragEvent -> {
            
            if (dragEvent.getGestureSource() != clusterBuilderScrollPane && dragEvent.getDragboard().hasString())
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            
            dragAndDropLabel.setVisible(false);
            dragEvent.consume();
        });
        clusterBuilderScrollPane.setOnDragDropped(dragEvent -> {
            
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;
            
            if (db.hasString()) {
                
                try {
                    
                    Event eventWrapped = JsonUtil.deserialize(db.getString());
                    clusterBuilderVBox.getChildren().add(createMiniEventComponent(eventWrapped));
                    
                    success = true;
                } catch (JsonSyntaxException ignored) { // this could be probably done better
                }
            }
            
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    }
    
    private MiniEventComponent createMiniEventComponent(Event event) {
        
        MiniEventComponent miniEventComponent = new MiniEventComponent();
        miniEventComponent.getModel().acceptEvent(event);
        
        setupMiniEventComponentDragAndDropActions(miniEventComponent);
        
        return miniEventComponent;
    }
    
    private void setupMiniEventComponentDragAndDropActions(MiniEventComponent miniEventComponent) {
        
        miniEventComponent.setOnDragDropped(dragEvent -> {
            
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
            
            if (dragboard.hasString()) {
                
                int index = clusterBuilderVBox.getChildren().indexOf(miniEventComponent);
                clusterBuilderVBox.getChildren().add(index + 1, createMiniEventComponent(JsonUtil.deserialize(dragboard.getString())));
                
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
    
            clusterBuilderVBox.getChildren().remove(miniEventComponent);
            
            if (clusterBuilderVBox.getChildren().isEmpty())
                dragAndDropLabel.setVisible(true);
            
            dragEvent.consume();
        });
    }
    
    private void fillVBoxes() {
        
        for (Event event : getViewModel().getEvents()) {
            
            Label label = new Label(event.getClass().getSimpleName());
            label.setFont(new Font("Arial", 16));
            
            if (!getViewModel().isEventEnabled(event)) {
                label.setStyle(Styling.FONT_RED);
                label.setDisable(true);
            }
            
            label.setOnMouseEntered(enter -> label.setStyle(Styling.SELECTED));
            label.setOnMouseExited(exit -> label.setStyle(Styling.CLEAR));
            
            setupEventLabelDragAndDropActions(label);
            
            eventVBox.getChildren().add(label);
        }
        
        for (EventCluster eventCluster : getViewModel().getEventClusters()) {
            clusterVBox.getChildren().add(new Label(eventCluster.getName()));
            // TODO
        }
    }
}
