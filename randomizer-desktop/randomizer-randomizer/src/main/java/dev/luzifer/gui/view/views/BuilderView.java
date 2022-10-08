package dev.luzifer.gui.view.views;

import com.google.gson.JsonSyntaxException;
import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.BuilderViewModel;
import dev.luzifer.model.event.cluster.EventCluster;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class BuilderView extends View<BuilderViewModel> {
    
    @FXML
    private VBox clusterBuilderVBox;
    
    @FXML
    private ListView<Label> clusterListView;
    
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
    private Button deleteClusterButton;
    
    @FXML
    private ScrollPane clusterVBoxScrollPane;
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        setupStyling();
        setGraphics();
        fillEventHBox();
        refreshCluster();
    
        // TODO: move!
        clusterListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            
            if(newValue == null)
                return;
            
            clusterBuilderVBox.getChildren().clear();
            
            EventCluster eventCluster = getViewModel().getCluster(newValue.getText());
            try {
                Arrays.stream(eventCluster.getEvents()).forEach(event -> {
            
                    Label eventLabel = new Label(event.name());
                    setupDragAlreadyDropped(eventLabel);
                    clusterBuilderVBox.getChildren().add(eventLabel);
                });
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        });
    }
    
    @FXML
    public void onRandomize(ActionEvent actionEvent) {
        
        clusterBuilderVBox.getChildren().clear();
        
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {
            
            Label label = new Label(getViewModel().getRandomEvent().name());
            setupDragAlreadyDropped(label);
            
            clusterBuilderVBox.getChildren().add(label);
        }
    }
    
    @FXML
    public void onSave(ActionEvent actionEvent) {
        
        if (clusterBuilderVBox.getChildren().isEmpty())
            return;
    
        StringBuilder stringBuilder = new StringBuilder();
        for(Node node : clusterBuilderVBox.getChildren())
            stringBuilder.append(((Labeled) node).getText()).append(";");

        String clusterName = awaitClusterName();
        if(clusterName == null)
            return;

        getViewModel().saveCluster(clusterName, stringBuilder.toString());
    
        refreshCluster();
        clusterBuilderVBox.getChildren().clear();
    }
    
    @FXML
    public void onClear(ActionEvent actionEvent) {
        clusterBuilderVBox.getChildren().clear();
    }
    
    @FXML
    private void onDeleteCluster(ActionEvent actionEvent) {
        
        Label selectedCluster = clusterListView.getSelectionModel().getSelectedItem();
        
        if(selectedCluster == null)
            return;
        
        getViewModel().deleteCluster(selectedCluster.getText());
        refreshCluster();
        
        clusterBuilderVBox.getChildren().clear();
    }
    
    private void refreshCluster() {
    
        clusterListView.getItems().clear();
        
        getViewModel().loadEventClusters().forEach(eventCluster -> {
            
            Label label = new Label(eventCluster.getName(),
                    ImageUtil.getImageView("images/bundle_icon.png", ImageUtil.ImageResolution.SMALL));
            label.setFont(new Font("Arial", 14));
    
            clusterListView.getItems().add(label);
        });
    }
    
    private void setupDrag(Label node) {
        node.setOnDragDetected(dragEvent -> {
        
            Dragboard dragboard = node.startDragAndDrop(TransferMode.ANY);
            dragboard.setDragView(node.snapshot(null, null), dragEvent.getX(), dragEvent.getY());
        
            ClipboardContent content = new ClipboardContent();
            content.putString(getViewModel().serialize(getViewModel().getEvent(node.getText())));
        
            dragboard.setContent(content);
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
                clusterBuilderVBox.getChildren().add(index - 1, new Label(getViewModel().deserialize(dragboard.getString()).name()));
            
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
        
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
        
            if (dragboard.hasString()) {
            
                try {
                
                    Label label = new Label(getViewModel().deserialize(dragboard.getString()).name());
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
        CSSUtil.applyNightTheme(root);
    }
    
    private void setGraphics() {
    
        getIcons().add(ImageUtil.getImage("images/build_icon.png"));
    
        randomizerButton.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.OKAY));
        saveButton.setGraphic(ImageUtil.getImageView("images/plus_icon.png", ImageUtil.ImageResolution.OKAY));
        clearButton.setGraphic(ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.OKAY));
        deleteClusterButton.setGraphic(ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.OKAY));
    }
    
    private void fillEventHBox() {
    
        setupDrop(clusterVBoxScrollPane);
    
        getViewModel().getEvents().forEach(event -> {
    
            Label label = new Label(event.name());
            label.setContentDisplay(ContentDisplay.RIGHT);
            label.setPadding(new Insets(0, 0, 0, 5));
            label.setFont(new Font("Arial", 16));
            label.setTooltip(new Tooltip(event.description()));
            label.setGraphic(ImageUtil.getImageView("images/drag_icon.png", ImageUtil.ImageResolution.SMALL));
            label.getGraphic().setOpacity(0);
    
            label.setOnMouseEntered(mouseEvent -> label.getGraphic().setOpacity(1));
            label.setOnMouseExited(mouseEvent -> label.getGraphic().setOpacity(0));
    
            setupDrag(label);
    
            eventFlowPane.getChildren().add(label);
        });
    }

    private String awaitClusterName() {
        while (true) {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Cluster Name");
            dialog.setHeaderText("Enter a name for the cluster");
            dialog.setContentText("Name:");

            Optional<String> result = dialog.showAndWait();
            if(!result.isPresent())
                return null;

            if (dialog.getResult().isEmpty() ||
                    getViewModel().getClusters()
                            .stream()
                            .anyMatch(eventCluster -> eventCluster.getName().equals(dialog.getResult()))) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("Cluster name is empty or already exists!");
                alert.showAndWait();

                continue;
            }

            return dialog.getResult();
        }
    }
}
