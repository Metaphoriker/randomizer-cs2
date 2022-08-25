package dev.luzifer.gui.view.views;

import com.google.gson.JsonSyntaxException;
import dev.luzifer.Main;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.cluster.EventCluster;
import dev.luzifer.model.json.JsonUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.util.Styling;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.component.components.EventComponent;
import dev.luzifer.gui.view.models.BuilderViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

// TODO: A Section to build events from scratch
public class BuilderView extends View<BuilderViewModel> {
    
    // Total root
    @FXML
    private Pane rootPane;
    
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
    
    @FXML
    private FlowPane eventFlowPane;
    
    @FXML
    private Button clearButton;
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    
        Tooltip tooltip = new Tooltip("Clear");
        tooltip.setShowDelay(Duration.ONE);
        tooltip.setHideDelay(Duration.ONE);
        clearButton.setTooltip(tooltip);
        
        setupStyling();
        setupClusterBuilderVBoxAcceptDrag();
        
        fillVBoxes();
    }
    
    @FXML
    private void onClear(ActionEvent actionEvent) {
        clusterBuilderVBox.getChildren().clear();
    }
    
    private void setupStyling() {
        
        topPaneTitle.setStyle(Styling.HEADER + Styling.BORDER);
        panePaneTitle.setStyle(Styling.HEADER + Styling.BORDER);
        
        rootPane.setStyle(Styling.BASE);
        topRoot.setStyle(Styling.BACKGROUND_DARKER);
        eventFlowPane.setStyle(Styling.BACKGROUND_DARKER);
        
        getIcons().add(ImageUtil.getImage("images/build_icon.png"));
        clearButton.setGraphic(ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.OKAY));
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
                    clusterBuilderVBox.getChildren().add(createEventComponent(eventWrapped));
                    
                    success = true;
                } catch (JsonSyntaxException ignored) { // this could be probably done better
                }
            }
            
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    }
    
    private EventComponent createEventComponent(Event event) {
        
        EventComponent eventComponent = new EventComponent();
        eventComponent.getModel().acceptEvent(event);
        
        setupEventComponentDragAndDropActions(eventComponent);
        
        return eventComponent;
    }
    
    private void setupEventComponentDragAndDropActions(EventComponent eventComponent) {
        
        eventComponent.setOnDragDropped(dragEvent -> {
            
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
            
            if (dragboard.hasString()) {
                
                int index = clusterBuilderVBox.getChildren().indexOf(eventComponent);
                // offset -1 because then it gets set before the separator which gets removed in the next step
                clusterBuilderVBox.getChildren().add(index - 1, createEventComponent(JsonUtil.deserialize(dragboard.getString())));
                
                success = true;
            }
            
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
        eventComponent.setOnDragEntered(dragEvent -> {
            Separator separator = new Separator();
            separator.setStyle(Styling.BACKGROUND_RED);
            clusterBuilderVBox.getChildren().add(clusterBuilderVBox.getChildren().indexOf(eventComponent), separator);
        });
        eventComponent.setOnDragExited(dragEvent -> {
            clusterBuilderVBox.getChildren().remove(clusterBuilderVBox.getChildren().indexOf(eventComponent) - 1);
        });
        eventComponent.setOnDragDetected(dragEvent -> {
            
            Dragboard db = eventComponent.startDragAndDrop(TransferMode.ANY);
            db.setDragView(eventComponent.snapshot(null, null), dragEvent.getX(), dragEvent.getY());
            
            ClipboardContent content = new ClipboardContent();
            content.putString(JsonUtil.serialize(eventComponent.getModel().getEventWrapped()));
            
            db.setContent(content);
            dragEvent.consume();
        });
        eventComponent.setOnDragDone(dragEvent -> {
            
            clusterBuilderVBox.getChildren().remove(eventComponent);
            
            if (clusterBuilderVBox.getChildren().isEmpty())
                dragAndDropLabel.setVisible(true);
            
            dragEvent.consume();
        });
    }
    
    private void fillVBoxes() {
        
        for (Event event : getViewModel().getEvents()) {
            
            Label label = new Label(event.getClass().getSimpleName());
            label.setFont(new Font("Arial", 16));
            label.setPadding(new Insets(0, 0, 0, 24));
            
            Main.getScheduler().schedule(() -> Platform.runLater(() -> label.setPrefWidth(label.getWidth() + 24)));
            
            label.setContentDisplay(ContentDisplay.RIGHT);
            
            if (!getViewModel().isEventEnabled(event)) {
                label.setStyle(Styling.FONT_RED);
                label.setDisable(true);
            }
            
            label.setStyle(Styling.BACKGROUND_BANZAI_BLUE + Styling.BORDER);
            label.setOnMouseEntered(enter -> label.setGraphic(ImageUtil.getImageView("images/wip_icon.png", ImageUtil.ImageResolution.SMALL)));
            label.setOnMouseExited(exit -> label.setGraphic(null));
            
            setupEventLabelDragAndDropActions(label);
            eventFlowPane.getChildren().add(label);
        }
        
        for (EventCluster eventCluster : getViewModel().getEventClusters()) {
            clusterVBox.getChildren().add(new Label(eventCluster.getName()));
            // TODO
        }
    }
}