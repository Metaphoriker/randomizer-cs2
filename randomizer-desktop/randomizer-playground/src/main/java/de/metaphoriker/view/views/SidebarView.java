package de.metaphoriker.view.views;

import de.metaphoriker.util.ImageUtil;
import de.metaphoriker.view.View;
import de.metaphoriker.view.ViewProvider;
import de.metaphoriker.view.viewmodel.SidebarViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

@View
public class SidebarView extends VBox implements Initializable {

  private final ViewProvider viewProvider = ViewProvider.getInstance();
  private final SidebarViewModel sidebarViewModel = new SidebarViewModel();

  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private ToggleButton gameButton;
  @FXML private Rectangle logoShape;
  @FXML private Button websiteButton;
  @FXML private Button discordButton;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
    setupToggles();
    setupClickInteractions();
  }

  private void setupClickInteractions() {
    discordButton.setOnAction(_ -> sidebarViewModel.openDiscordLinkInBrowser());
    websiteButton.setOnAction(_ -> sidebarViewModel.openWebsiteLinkInBrowser());
  }

  private void setupGraphics() {
    setImagePattern(logoShape, "images/randomizerIcon.png");
    websiteButton.setGraphic(ImageUtil.getImageView("images/websiteIcon.png"));
    discordButton.setGraphic(ImageUtil.getImageView("images/discordIcon.png"));
    randomizerButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    builderButton.setGraphic(ImageUtil.getImageView("images/logbookIcon.png"));
    gameButton.setGraphic(ImageUtil.getImageView("images/gameIcon.png"));
  }

  private void setupToggles() {
    randomizerButton.setOnAction(
        _ -> {
          randomizerButton.setSelected(true);
          builderButton.setSelected(false);
          gameButton.setSelected(false);
        });
    builderButton.setOnAction(
        _ -> {
          randomizerButton.setSelected(false);
          builderButton.setSelected(true);
          gameButton.setSelected(false);
          viewProvider.triggerViewChange(BuilderView.class);
        });
    gameButton.setOnAction(
        _ -> {
          randomizerButton.setSelected(false);
          builderButton.setSelected(false);
          gameButton.setSelected(true);
        });
  }

  private void setImagePattern(Shape shape, String imagePath) {
    ImagePattern imagePattern = ImageUtil.getRawImagePattern(imagePath);
    shape.setFill(imagePattern);
  }
}
