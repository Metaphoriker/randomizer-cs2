package de.metaphoriker.gui.view.views;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import de.metaphoriker.gui.component.SettingsOverlayComponent;
import de.metaphoriker.gui.component.SliderLabelComponent;
import de.metaphoriker.gui.component.TitledClusterContainer;
import de.metaphoriker.gui.util.FuckYouControl;
import de.metaphoriker.gui.util.ImageUtil;
import de.metaphoriker.gui.view.View;
import de.metaphoriker.gui.view.models.RandomizerViewModel;
import de.metaphoriker.model.stuff.ApplicationContext;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RandomizerView extends View<RandomizerViewModel> {

  private final SettingsOverlayComponent settingsOverlay = new SettingsOverlayComponent();

  @FXML private HBox root;

  @FXML private VBox logVBox;

  @FXML private Button toggleButton;

  @FXML private Button settingsButton;

  public RandomizerView(RandomizerViewModel viewModel) {
    super(viewModel);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    super.initialize(url, resourceBundle);

    root.getChildren().add(settingsOverlay);

    setupStylingAndGraphics();
    setupOverlay();
    setupBindings();
    setupExecutionReactions();
  }

  @FXML
  private void onToggle(ActionEvent actionEvent) {
    getViewModel().toggleApplicationState();
  }

  @FXML
  private void onOpenSettings(ActionEvent actionEvent) {
    FuckYouControl.show(settingsOverlay, settingsButton);
  }

  private void setupStylingAndGraphics() {
    getIcons().add(ImageUtil.getImage("images/shuffle_icon.png"));
    settingsButton.setGraphic(
        ImageUtil.getImageView("images/settings_icon.png", ImageUtil.ImageResolution.SMALL));
  }

  private void setupOverlay() {
    FuckYouControl fuckYouControl = new FuckYouControl(root);
    fuckYouControl.addOverlay(settingsOverlay);
  }

  private void setupBindings() {

    SliderLabelComponent minSlider = settingsOverlay.getMinSlider();
    SliderLabelComponent maxSlider = settingsOverlay.getMaxSlider();

    settingsOverlay.getApplyButton().setOnAction(action -> FuckYouControl.hide(settingsOverlay));

    getViewModel()
        .setOnFailed(
            () -> {
              settingsOverlay.getFailedLabel().setOpacity(1);
              FuckYouControl.show(settingsOverlay, settingsButton);
            });

    getViewModel().getVisibleProperty().bindBidirectional(settingsOverlay.visibleProperty());
    getViewModel().getNextStateProperty().bindBidirectional(toggleButton.textProperty());

    GlobalScreen.addNativeKeyListener(
        new NativeKeyListener() {
          @Override
          public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
            if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_INSERT) {
              Platform.runLater(() -> getViewModel().toggleApplicationState());
            }
          }
        });

    ApplicationContext.registerApplicationStateChangeListener(
        applicationState ->
            Platform.runLater(() -> getViewModel().setApplicationState(applicationState)));

    getViewModel().getMinWaitTimeProperty().bind(minSlider.getSlider().valueProperty());
    getViewModel().getMaxWaitTimeProperty().bind(maxSlider.getSlider().valueProperty());
  }

  private void setupExecutionReactions() {

    getViewModel()
        .setOnClusterExecution(
            cluster ->
                Platform.runLater(
                    () -> {
                      TitledClusterContainer titledClusterContainer =
                          new TitledClusterContainer(cluster.getName(), cluster);
                      logVBox.getChildren().add(0, titledClusterContainer);
                    }));

    getViewModel()
        .setOnClusterExecutionFinished(
            cluster ->
                Platform.runLater(
                    () -> {
                      logVBox.getChildren().stream()
                          .filter(TitledClusterContainer.class::isInstance)
                          .map(TitledClusterContainer.class::cast)
                          .filter(container -> container.getEventCluster().equals(cluster))
                          .forEach(TitledClusterContainer::finish);
                    }));

    getViewModel()
        .setOnEventExecutionFinished(
            event ->
                Platform.runLater(
                    () -> {
                      logVBox.getChildren().stream()
                          .filter(TitledClusterContainer.class::isInstance)
                          .map(TitledClusterContainer.class::cast)
                          .filter(container -> !container.isFinished())
                          .filter(
                              container -> container.getEventCluster().getEvents().contains(event))
                          .forEach(container -> container.finish(event));
                    }));

    getViewModel()
        .setOnEventExecution(
            event ->
                Platform.runLater(
                    () -> {
                      logVBox.getChildren().stream()
                          .filter(TitledClusterContainer.class::isInstance)
                          .map(TitledClusterContainer.class::cast)
                          .filter(container -> !container.isFinished())
                          .filter(
                              container -> container.getEventCluster().getEvents().contains(event))
                          .forEach(container -> container.visualizeExecution(event));
                    }));
  }
}
