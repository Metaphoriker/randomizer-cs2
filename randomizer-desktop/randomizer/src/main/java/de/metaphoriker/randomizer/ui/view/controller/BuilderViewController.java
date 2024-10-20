package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private final BuilderViewModel builderViewModel;

  @FXML private TextField searchField;
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
        .addListener((_, _, newSequenceName) -> fillBuilderWithActionsOfSequence(newSequenceName));

    setupSearchFieldListener();
  }

  private void setupSearchFieldListener() {
    searchField
        .textProperty()
        .addListener(
            (_, _, newValue) -> {
              actionsVBox.getChildren().clear();
              String filter = newValue.toLowerCase();
              builderViewModel
                  .getActionToTypeMap()
                  .forEach(
                      (type, actionList) -> {
                        List<String> filteredActions =
                            actionList.stream()
                                .filter(action -> action.toLowerCase().contains(filter))
                                .toList();

                        if (!filteredActions.isEmpty()) {
                          actionsVBox.getChildren().add(createTitledPane(type, filteredActions));
                        }
                      });
            });
  }

  private TitledPane createTitledPane(String type, List<String> actions) {
    TitledPane titledPane = new TitledPane();
    titledPane.setCollapsible(true);
    titledPane.setAnimated(true);
    titledPane.setExpanded(false);
    titledPane.setText(type);

    VBox vBox = new VBox();
    actions.forEach(action -> vBox.getChildren().add(new Label(action)));
    applyExpandListener(titledPane);
    titledPane.setContent(vBox);

    return titledPane;
  }

  private void fillActions() {
    builderViewModel
        .getActionToTypeMap()
        .forEach(
            (type, actionList) ->
                actionsVBox.getChildren().add(createTitledPane(type, actionList)));
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
    actions.forEach(action -> builderVBox.getChildren().add(new Label(action)));
  }
}
