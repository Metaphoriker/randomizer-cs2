package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.util.List;
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
  @FXML private VBox builderVBox;

  @Inject
  public BuilderViewController(BuilderViewModel builderViewModel) {
    this.builderViewModel = builderViewModel;
    Platform.runLater(this::initialize);
  }

  private void initialize() {
    setupBindings();
    fillActions();
    fillActionSequences();
  }

  private void setupBindings() {
    builderViewModel
        .getCurrentActionSequenceProperty()
        .addListener(
            (_, _, newSequenceName) -> {
              fillBuilderWithActionsOfSequence(newSequenceName);
            });
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
              titledPane.setText(type);
              VBox vBox = new VBox();
              actionList.forEach(
                  action -> {
                    Label label = new Label(action);
                    vBox.getChildren().add(label);
                  });
              applyExpandListener(titledPane);
              titledPane.setContent(vBox);
              actionsVBox.getChildren().add(titledPane);
            });
  }

  private void applyExpandListener(TitledPane titledPane) {
    titledPane
        .expandedProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue) {
                actionsVBox.getChildren().stream()
                    .filter(node -> node instanceof TitledPane && node != titledPane)
                    .forEach(node -> ((TitledPane) node).setExpanded(false));
              }
            });
  }

  private void fillActionSequences() {
    builderViewModel
        .getActionSequences()
        .forEach(
            actionSequence -> {
              Label label = new Label(actionSequence);
              label.setOnMouseClicked(
                  _ -> builderViewModel.getCurrentActionSequenceProperty().set(actionSequence));
              actionSequencesVBox.getChildren().add(label);
            });
  }

  private void fillBuilderWithActionsOfSequence(String sequenceName) {
    List<String> actions = builderViewModel.getActionsOfSequence(sequenceName);
    builderVBox.getChildren().clear();
    actions.forEach(
        action -> {
          Label label = new Label(action);
          builderVBox.getChildren().add(label);
        });
  }
}
