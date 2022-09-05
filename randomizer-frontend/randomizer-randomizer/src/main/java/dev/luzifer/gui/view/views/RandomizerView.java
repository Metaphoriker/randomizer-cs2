package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.util.Styling;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RandomizerView extends View<RandomizerViewModel> {
    
    @FXML
    private TabPane root;
    
    @FXML
    private Tab configTab;
    
    @FXML
    private Tab randomizerTab;
    
    @FXML
    private Label configLabel;
    
    
    public RandomizerView(RandomizerViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        root.setStyle(Styling.BASE_DARKER);
        configLabel.setStyle(Styling.HEADER + Styling.BORDER);
        
        randomizerTab.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.SMALL));
        configTab.setGraphic(ImageUtil.getImageView("images/settings_icon.png", ImageUtil.ImageResolution.SMALL));
    }
}
