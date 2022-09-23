package dev.luzifer.gui.view.views;

import com.google.gson.JsonSyntaxException;
import dev.luzifer.Main;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.BuilderViewModel;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.json.JsonUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class BuilderView extends View<BuilderViewModel> {
    
    // TODO: model stuff
    private static final File CLUSTER_FOLDER = new File(Main.APPDATA_FOLDER + File.separator + "cluster");
    
    @FXML
    private VBox clusterBuilderVBox;
    
    @FXML
    private VBox clusterVBox;
    
    @FXML
    private FlowPane eventFlowPane;
    
    @FXML
    private VBox root;
    
    @FXML
    private Button randomizerButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private ScrollPane clusterVBoxScrollPane;
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        // TODO: move this
        if(!CLUSTER_FOLDER.exists())
            CLUSTER_FOLDER.mkdirs();
        
        setupStyling();
        setGraphics();
        fillEventHBox();
        refreshCluster();
    }
    
    @FXML
    public void onRandomize(ActionEvent actionEvent) {
        
        clusterBuilderVBox.getChildren().clear();
        
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {
            
            int randomEventIndex = ThreadLocalRandom.current().nextInt(0, getViewModel().getEvents().size());
            Event randomEvent = getViewModel().getEvents().get(randomEventIndex);
    
            Label label = new Label(randomEvent.name());
            setupDragAlreadyDropped(label);
            
            clusterBuilderVBox.getChildren().add(label);
        }
    }
    
    @FXML
    public void onSave(ActionEvent actionEvent) {
        
        if (clusterBuilderVBox.getChildren().isEmpty())
            return;
    
        TextInputDialog clusterNameDialog = new TextInputDialog();
        clusterNameDialog.setTitle(null);
        clusterNameDialog.setHeaderText(null);
        clusterNameDialog.setGraphic(null);
        clusterNameDialog.setContentText("Cluster Name:");
        clusterNameDialog.showAndWait();
        
        // TODO: check for invalid/duplicate names
        
        // Viewmodel stuff right there
        File clusterFile = new File(CLUSTER_FOLDER, clusterNameDialog.getResult() + ".cluster");
        try(PrintWriter printWriter = new PrintWriter(clusterFile)) {
            
            StringBuilder stringBuilder = new StringBuilder();
            for(Node node : clusterBuilderVBox.getChildren())
                stringBuilder.append(((Label) node).getText()).append(";");
            
            printWriter.println(stringBuilder);
            printWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        refreshCluster();
        clusterBuilderVBox.getChildren().clear();
    }
    
    @FXML
    public void onClear(ActionEvent actionEvent) {
        clusterBuilderVBox.getChildren().clear();
    }
    
    // TODO: retrieve from viewmodel
    private void refreshCluster() {
            
            clusterVBox.getChildren().clear();
            
            File clusterFolder = new File(Main.APPDATA_FOLDER + File.separator + "cluster");
            if (!clusterFolder.exists())
                return;
            
            for (File file : clusterFolder.listFiles()) {
                
                Label label = new Label(file.getName().substring(0, file.getName().lastIndexOf(".")),
                        ImageUtil.getImageView("images/bundle_icon.png", ImageUtil.ImageResolution.SMALL));
                label.setFont(new Font("Arial", 14));
                
                clusterVBox.getChildren().add(label);
            }
    }
    
    private void setupDrag(Label node) {
    
        Event event = getViewModel().getEvent(node.getText());
        node.setOnDragDetected(dragEvent -> {
        
            Dragboard db = node.startDragAndDrop(TransferMode.ANY);
            db.setDragView(node.snapshot(null, null), dragEvent.getX(), dragEvent.getY());
        
            ClipboardContent content = new ClipboardContent();
            content.putString(JsonUtil.serialize(event));
        
            db.setContent(content);
            dragEvent.consume();
        });
    }
    
    private void setupDragAlreadyDropped(Label node) {
    
        setupDrag(node);
    
        node.setOnDragDropped(dragEvent -> {
        
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
        
            if (dragboard.hasString()) {
            
                int index = clusterBuilderVBox.getChildren().indexOf(node);
                // offset -1 because then it gets set before the separator which gets removed in the next step
                clusterBuilderVBox.getChildren().add(index - 1, new Label(JsonUtil.deserialize(dragboard.getString()).name()));
            
                success = true;
            }
        
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    
        node.setOnDragEntered(dragEvent -> {
            Separator separator = new Separator();
            clusterBuilderVBox.getChildren().add(clusterBuilderVBox.getChildren().indexOf(node), separator);
        });
    
        node.setOnDragExited(dragEvent -> clusterBuilderVBox.getChildren().remove(clusterBuilderVBox.getChildren().indexOf(node) - 1));
    
        node.setOnDragDone(dragEvent -> {
            clusterBuilderVBox.getChildren().remove(node);
            dragEvent.consume();
        });
    }
    
    private void setupDrop(Region region) {
    
        region.setOnDragOver(dragEvent -> {
        
            if (dragEvent.getGestureSource() != region && dragEvent.getDragboard().hasString())
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        
            dragEvent.consume();
        });
        
        region.setOnDragDropped(dragEvent -> {
        
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;
        
            if (db.hasString()) {
            
                try {
                
                    Event eventWrapped = JsonUtil.deserialize(db.getString());
                    Label label = new Label(eventWrapped.name());
                    setupDragAlreadyDropped(label);
                    
                    clusterBuilderVBox.getChildren().add(label);
                
                    success = true;
                } catch (JsonSyntaxException ignored) { // this could be probably done better
                }
            }
        
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    }
    
    private void setupStyling() {
        root.getStylesheets().add(getClass().getResource("BuilderView.css").toExternalForm());
    }
    
    private void setGraphics() {
    
        getIcons().add(ImageUtil.getImage("images/build_icon.png"));
    
        randomizerButton.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.OKAY));
        saveButton.setGraphic(ImageUtil.getImageView("images/plus_icon.png", ImageUtil.ImageResolution.OKAY));
        clearButton.setGraphic(ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.OKAY));
    }
    
    private void fillEventHBox() {
    
        setupDrop(clusterVBoxScrollPane);
        
        for (Event event : getViewModel().getEvents()) {
        
            Label label = new Label(event.name());
            label.setContentDisplay(ContentDisplay.RIGHT);
            label.setPadding(new Insets(0, 0, 0, 5));
            label.setFont(new Font("Arial", 16));
            label.setGraphic(ImageUtil.getImageView("images/drag_icon.png", ImageUtil.ImageResolution.SMALL));
            label.getGraphic().setOpacity(0);
        
            label.setOnMouseEntered(mouseEvent -> label.getGraphic().setOpacity(1));
            label.setOnMouseExited(mouseEvent -> label.getGraphic().setOpacity(0));
            
            setupDrag(label);
        
            eventFlowPane.getChildren().add(label);
        }
    }
}
