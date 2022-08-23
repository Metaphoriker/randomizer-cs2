package dev.luzifer.gui.view.views;

import dev.luzifer.backend.updater.UpdateChecker;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.util.Styling;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.SelectionViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class SelectionView extends View<SelectionViewModel> {
    
    private final UpdateChecker updateChecker = new UpdateChecker();
    
    @FXML
    private VBox root;
    
    @FXML
    private Label randomizerLabel;
    
    @FXML
    private Label builderLabel;
    
    @FXML
    private Label updateLabel;
    
    @FXML
    private Button settingsButton;

    public SelectionView(SelectionViewModel selectionViewModel) {
        super(selectionViewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getIcons().add(ImageUtil.getImage("images/csgoremote_icon.png"));

        updateChecker.checkUpdate();
        if(updateChecker.isUpdateAvailable())
            updateLabel.setVisible(true);

        setupStyling();
        setupClickActions();
    }
    
    @FXML
    public void onEnter(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).setStyle(Styling.SELECTED + Styling.BORDER);
    }
    
    @FXML
    public void onExit(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).setStyle(Styling.UNSELECTED + Styling.BORDER);
    }
    
    @FXML
    public void onSettingsPress(ActionEvent actionEvent) {
        // TODO: Implement settings view
    }
    
    private void setupStyling() {
    
        root.setStyle(Styling.BASE);
        randomizerLabel.setStyle(Styling.UNSELECTED + Styling.BORDER);
        builderLabel.setStyle(Styling.UNSELECTED + Styling.BORDER);
        settingsButton.setStyle(Styling.UNSELECTED + Styling.BORDER);
        updateLabel.setStyle(Styling.FONT_RED);
    
        // Not a ^ styling, but still style based
        updateLabel.setCursor(Cursor.HAND);
        settingsButton.setFocusTraversable(false);
        
        settingsButton.setGraphic(ImageUtil.getImageView("images/settings_icon.png", ImageUtil.ImageResolution.SMALL));
        randomizerLabel.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.SMALL));
        builderLabel.setGraphic(ImageUtil.getImageView("images/build_icon.png", ImageUtil.ImageResolution.SMALL));
        updateLabel.setGraphic(ImageUtil.getImageView("images/download_icon.png", ImageUtil.ImageResolution.SMALL));
    }
    
    private void setupClickActions() {
        
        randomizerLabel.setOnMouseClicked(event -> getViewModel().openRandomizer());
        builderLabel.setOnMouseClicked(event -> getViewModel().openBuilder());
        
        updateLabel.setOnMouseClicked(mouseEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/Luziferium/randomizer-csgo").toURI());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
