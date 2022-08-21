package dev.luzifer.ui.view.views;

import dev.luzifer.ui.util.Styling;
import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.models.SelectionViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectionView extends View<SelectionViewModel> {
    
    @FXML
    private VBox root;
    
    @FXML
    private Label randomizerLabel;
    
    @FXML
    private Label builderLabel;
    
    @FXML
    private Button settingsButton;
    
    
    public SelectionView(SelectionViewModel selectionViewModel) {
        super(selectionViewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        setupStyling();
        setupClickActions();
    }
    
    @FXML
    public void onEnter(MouseEvent mouseEvent) {
        ((Control) mouseEvent.getSource()).setStyle(Styling.SELECTED + Styling.BORDER);
    }
    
    @FXML
    public void onExit(MouseEvent mouseEvent) {
        ((Control) mouseEvent.getSource()).setStyle(Styling.UNSELECTED + Styling.BORDER);
    }
    
    @FXML
    public void onSettingsPress(ActionEvent actionEvent) {
    }
    
    private void setupStyling() {
        
        root.setStyle(Styling.BACKGROUND);
        randomizerLabel.setStyle(Styling.UNSELECTED + Styling.BORDER);
        builderLabel.setStyle(Styling.UNSELECTED + Styling.BORDER);
        settingsButton.setStyle(Styling.UNSELECTED + Styling.BORDER);
    
        // Not a ^ styling, but still style based
        settingsButton.setFocusTraversable(false);
        
        settingsButton.setGraphic(new ImageView("images/16x16/settings16x16.png"));
        randomizerLabel.setGraphic(new ImageView("images/16x16/shuffle16x16.png"));
        builderLabel.setGraphic(new ImageView("images/16x16/builder16x16.png"));
    }
    
    private void setupClickActions() {
        randomizerLabel.setOnMouseClicked(event -> getViewModel().openRandomizer());
        builderLabel.setOnMouseClicked(event -> getViewModel().openBuilder());
    }
}
