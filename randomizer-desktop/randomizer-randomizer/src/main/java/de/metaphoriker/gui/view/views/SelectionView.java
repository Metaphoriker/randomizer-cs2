package de.metaphoriker.gui.view.views;

import de.metaphoriker.gui.util.CSSUtil;
import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.gui.view.View;
import de.metaphoriker.gui.view.models.SelectionViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class SelectionView extends View<SelectionViewModel> {

  @FXML private Circle logoShape;

  @FXML private Label randomizerLabel;

  @FXML private Label builderLabel;

  @FXML private ImageView invasionGameBanner;

  @FXML private ComboBox<CSSUtil.Theme> themeComboBox;

  public SelectionView(SelectionViewModel selectionViewModel) {
    super(selectionViewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    setResizable(false);

    setupGraphics();
    setupMouseEvents();
    setupThemeComboBox();
  }

  @Override
  public void onClose() {
    System.exit(0);
  }

  private void setupGraphics() {

    getIcons().add(ImageUtil.getImage("images/randomizer-icon.jpg"));

    logoShape.setFill(
        ImageUtil.getRawImagePattern(
            "images/randomizer-icon.jpg"));

    randomizerLabel.setGraphic(
        ImageUtil.getImageView("images/shuffle_icon.png", ImageUtil.ImageResolution.MEDIUM));
    builderLabel.setGraphic(
        ImageUtil.getImageView("images/build_icon.png", ImageUtil.ImageResolution.MEDIUM));
    invasionGameBanner.setImage(ImageUtil.getRawImage("images/game/invasion_game_banner.png"));
  }

  private void setupMouseEvents() {

    randomizerLabel.setOnMouseClicked(event -> getViewModel().openRandomizer());
    builderLabel.setOnMouseClicked(event -> getViewModel().openBuilder());
    invasionGameBanner.setOnMouseClicked(event -> getViewModel().openGame());
  }

  private void setupThemeComboBox() {

    getViewModel().getThemeProperty().bindBidirectional(themeComboBox.valueProperty());

    themeComboBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observableValue, oldTheme, newTheme) -> getViewModel().switchTheme());

    themeComboBox.getItems().addAll(CSSUtil.Theme.values());
    themeComboBox.getSelectionModel().select(CSSUtil.Theme.COZY);
  }
}
