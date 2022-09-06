package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.util.Styling;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.SelectionViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class SelectionView extends View<SelectionViewModel> {
    
    @FXML
    private GridPane root;
    
    @FXML
    private Label randomizerLabel;
    
    @FXML
    private Label builderLabel;
    
    @FXML
    private Button informationButton;

    public SelectionView(SelectionViewModel selectionViewModel) {
        super(selectionViewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getIcons().add(ImageUtil.getImage("images/csgoremote_icon.png"));

        setupStyling();
        setupGraphics();
        setupMouseEvents();
    }
    
    @Override
    public void onClose() {
        System.exit(0);
    }
    
    @FXML
    public void onInformationRequested(ActionEvent actionEvent) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URL("https://github.com/Luziferium/randomizer-csgo/releases/tag/latest").toURI());
    }
    
    private void setupStyling() {
    
        root.setStyle(Styling.BASE);
        randomizerLabel.setStyle(Styling.UNSELECTED + Styling.BORDER);
        builderLabel.setStyle(Styling.UNSELECTED + Styling.BORDER);
        informationButton.setStyle(Styling.UNSELECTED + Styling.BORDER);
    }
    
    private void setupGraphics() {
        
        informationButton.setGraphic(ImageUtil.getImageView("images/information_icon.png", ImageUtil.ImageResolution.SMALL));
        randomizerLabel.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.SMALL));
        builderLabel.setGraphic(ImageUtil.getImageView("images/build_icon.png", ImageUtil.ImageResolution.SMALL));
    }
    
    private void setupMouseEvents() {

        randomizerLabel.setOnMouseEntered(event -> randomizerLabel.setStyle(Styling.SELECTED + Styling.BORDER));
        randomizerLabel.setOnMouseExited(event -> randomizerLabel.setStyle(Styling.UNSELECTED + Styling.BORDER));
        randomizerLabel.setOnMouseClicked(event -> getViewModel().openRandomizer());

        builderLabel.setOnMouseEntered(event -> builderLabel.setStyle(Styling.SELECTED + Styling.BORDER));
        builderLabel.setOnMouseExited(event -> builderLabel.setStyle(Styling.UNSELECTED + Styling.BORDER));
        builderLabel.setOnMouseClicked(event -> getViewModel().openBuilder());
        
        informationButton.setOnMouseEntered(event -> informationButton.setStyle(Styling.SELECTED + Styling.BORDER));
        informationButton.setOnMouseExited(event -> informationButton.setStyle(Styling.UNSELECTED + Styling.BORDER));
    }
}
