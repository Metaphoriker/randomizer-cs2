package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private final BuilderViewModel builderViewModel;

  @FXML private VBox actionsVBox;
  @FXML private VBox actionSequencesVBox;

  @Inject
  public BuilderViewController(BuilderViewModel builderViewModel) {
    this.builderViewModel = builderViewModel;
    Platform.runLater(this::initialize);
  }

  private void initialize() {
    fillActions();
    fillActionSequences();
  }

  private void fillActions() {
    builderViewModel
        .getActionToTypeMap()
        .forEach(
            (type, actionList) -> {
              TitledPane titledPane = new TitledPane();
              titledPane.setCollapsible(true);
              titledPane.setAnimated(true);
              titledPane.setExpanded(false);
              titledPane.setText(type.name());
              VBox vBox = new VBox();
              actionList.forEach(
                  action -> {
                    Label label = new Label(action.getName());
                    vBox.getChildren().add(label);
                  });
              titledPane.setContent(vBox);
              actionsVBox.getChildren().add(titledPane);
            });
  }

  private void fillActionSequences() {
    builderViewModel
        .getActionSequences()
        .forEach(
            actionSequence -> {
              Label label = new Label(actionSequence.getName());
              actionSequencesVBox.getChildren().add(label);
            });
  }
}
