package dev.luzifer.gui.view.views;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.RandomizerViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import java.net.URL;
import java.util.ResourceBundle;

public class RandomizerView extends View<RandomizerViewModel> {
    
    @FXML
    private Tab configTab;
    
    @FXML
    private Tab randomizerTab;
    
    public RandomizerView(RandomizerViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        randomizerTab.setGraphic(ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.SMALL));
        configTab.setGraphic(ImageUtil.getImageView("images/settings_icon.png", ImageUtil.ImageResolution.SMALL));
    }
}
