package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.util.Styling;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.BuilderViewModel;
import dev.luzifer.model.event.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class BuilderView extends View<BuilderViewModel> {
    
    @FXML
    private HBox buttonHBox;
    
    @FXML
    private VBox buttonVBox;
    
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
    
    public BuilderView(BuilderViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        root.setStyle(Styling.BASE_DARKER);
        
        setGraphics();
        fillEventHBox();
    }
    
    @FXML
    public void onRandomize(ActionEvent actionEvent) {
        
        clusterBuilderVBox.getChildren().clear();
        
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {
            
            int randomEventIndex = ThreadLocalRandom.current().nextInt(0, getViewModel().getEvents().size());
            Event randomEvent = getViewModel().getEvents().get(randomEventIndex);
            
            clusterBuilderVBox.getChildren().add(new Label(randomEvent.name()));
        }
    }
    
    @FXML
    public void onSave(ActionEvent actionEvent) {
        
        if (clusterBuilderVBox.getChildren().isEmpty())
            return;
        
        clusterBuilderVBox.getChildren().clear();
        clusterVBox.getChildren().add(new Label("Insert stuff here"));
    }
    
    @FXML
    public void onClear(ActionEvent actionEvent) {
        clusterBuilderVBox.getChildren().clear();
    }
    
    private void setGraphics() {
    
        getIcons().add(ImageUtil.getImage("images/build_icon.png"));
    
        randomizerButton.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.SMALL));
        saveButton.setGraphic(ImageUtil.getImageView("images/plus_icon.png", ImageUtil.ImageResolution.SMALL));
        clearButton.setGraphic(ImageUtil.getImageView("images/delete_icon.png", ImageUtil.ImageResolution.SMALL));
    }
    
    private void fillEventHBox() {
    
        for (Event event : getViewModel().getEvents()) {
        
            Label label = new Label(event.name());
            label.setStyle(Styling.BORDER + Styling.HEADER);
            label.setContentDisplay(ContentDisplay.RIGHT);
            label.setPadding(new Insets(0, 0, 0, 5));
            label.setFont(new Font("Arial", 16));
            label.setGraphic(ImageUtil.getImageView("images/drag_icon.png", ImageUtil.ImageResolution.SMALL));
            label.getGraphic().setOpacity(0);
        
            label.setOnMouseEntered(mouseEvent -> label.getGraphic().setOpacity(1));
            label.setOnMouseExited(mouseEvent -> label.getGraphic().setOpacity(0));
        
            eventFlowPane.getChildren().add(label);
        }
    }
}
