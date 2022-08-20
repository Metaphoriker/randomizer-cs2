package dev.luzifer.ui.view.views;

import dev.luzifer.ui.view.View;
import dev.luzifer.ui.view.viewmodel.SelectionViewModel;
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
        ((Control) mouseEvent.getSource()).setStyle(STYLING_SELECTED + STYLING_BORDER);
    }
    
    @FXML
    public void onExit(MouseEvent mouseEvent) {
        ((Control) mouseEvent.getSource()).setStyle(STYLING_UNSELECTED + STYLING_BORDER);
    }
    
    @FXML
    public void onSettingsPress(ActionEvent actionEvent) {
    }
    
    private void setupStyling() {
        
        root.setStyle(STYLING_BACKGROUND);
        randomizerLabel.setStyle(STYLING_UNSELECTED + STYLING_BORDER);
        builderLabel.setStyle(STYLING_UNSELECTED + STYLING_BORDER);
        settingsButton.setStyle(STYLING_UNSELECTED + STYLING_BORDER);
    
        // Not a ^ styling, but still style based
        settingsButton.setFocusTraversable(false);
        settingsButton.setGraphic(new ImageView("images/settings_icon.png"));
    }
    
    private void setupClickActions() {
        randomizerLabel.setOnMouseClicked(event -> getViewModel().openRandomizer());
        builderLabel.setOnMouseClicked(event -> getViewModel().openBuilder());
    }
}
