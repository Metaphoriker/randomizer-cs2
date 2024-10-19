package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.util.ImageUtil;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.ControlBarViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;

@View
public class NavigationBarController implements Initializable {

  private final ControlBarViewModel controlBarViewModel;
  private final ViewProvider viewProvider;

  @FXML private ToggleButton randomizerButton;
  @FXML private ToggleButton builderButton;
  @FXML private ToggleButton settingsButton;

  @Inject
  public NavigationBarController(
      ControlBarViewModel controlBarViewModel, ViewProvider viewProvider) {
    this.controlBarViewModel = controlBarViewModel;
    this.viewProvider = viewProvider;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setupGraphics();
    setupBindings();
    setupToggleButtonLogic();
  }

  private void setupBindings() {
    controlBarViewModel
        .getSelectedView()
        .addListener(
            (_, _, newView) -> {
              if (newView != null) {
                viewProvider.triggerViewChange(newView);
              }
            });
  }

  private void setupGraphics() {
    randomizerButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    builderButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
    settingsButton.setGraphic(ImageUtil.getImageView("images/randomizerIcon.png"));
  }

  private void setupToggleButtonLogic() {
    randomizerButton
        .selectedProperty()
        .addListener(createToggleButtonListener(builderButton, settingsButton));
    builderButton
        .selectedProperty()
        .addListener(createToggleButtonListener(randomizerButton, settingsButton));
    settingsButton
        .selectedProperty()
        .addListener(createToggleButtonListener(randomizerButton, builderButton));
  }

  private ChangeListener<Boolean> createToggleButtonListener(ToggleButton... otherButtons) {
    return (_, _, newValue) -> {
      if (newValue) {
        for (ToggleButton button : otherButtons) {
          button.setSelected(false);
        }
      } else {
        controlBarViewModel.setSelectedView(RandomizerWindowController.class);
      }
    };
  }
}
