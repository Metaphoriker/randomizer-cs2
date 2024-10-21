package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private final Separator dropIndicator = new Separator();
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

  private void setupBindings() {
    builderViewModel
        .getCurrentActionSequenceProperty()
        .addListener((_, _, newSequenceName) -> fillBuilderWithActionsOfSequence(newSequenceName));

    builderViewModel
        .getCurrentActionsProperty()
        .addListener((ListChangeListener<String>) _ -> updateBuilderVBox());

    builderVBox
        .disableProperty()
        .bind(builderViewModel.getCurrentActionSequenceProperty().isEmpty());

    setupSearchFieldListener();
  }

  @FXML
  void onRandomize(ActionEvent event) {
    builderViewModel.addRandomActions(10);
  }

  @FXML
  void onActionsClear(ActionEvent event) {
    builderViewModel.setActions(List.of());
  }

  @FXML
  void onSaveSequence(ActionEvent event) {
    builderViewModel.saveActionSequence();
    fillActionSequences();
  }

  @FXML
  void onAddSequence(ActionEvent event) {
    builderViewModel.createNewActionSequence();
    fillActionSequences();
  }

  private void initialize() {
    setupBindings();
    fillActions();
    fillActionSequences();
    setupDrop(builderVBox);

    actionsVBox
        .getChildren()
        .forEach(
            node -> {
              if (node instanceof TitledPane) {
                VBox content = (VBox) ((TitledPane) node).getContent();
                content
                    .getChildren()
                    .forEach(
                        child -> {
                          if (child instanceof Label) {
                            setupDrag((Label) child);
                          }
                        });
              }
            });
  }

  private void updateBuilderVBox() {
    builderVBox.getChildren().clear();
    builderViewModel
        .getCurrentActionsProperty()
        .forEach(
            actionText -> {
              Label actionLabel = new Label(actionText);
              setupDragAlreadyDropped(actionLabel); // setup special drag within listview
              builderVBox.getChildren().add(actionLabel);
            });
  }

  private void setupDrag(Label label) {
    if (label.getText() == null || label.getText().isEmpty()) return;

    label.setCursor(Cursor.OPEN_HAND);
    label.setOnDragDetected(
        dragEvent -> {
          Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
          dragboard.setDragView(label.snapshot(null, null), dragEvent.getX(), dragEvent.getY());

          ClipboardContent content = new ClipboardContent();
          content.putString(label.getText());

          dragboard.setContent(content);
          dragEvent.consume();
        });
  }

  private void setupDragAlreadyDropped(Label label) {
    setupDrag(label);

    label.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {
            int index = builderVBox.getChildren().indexOf(label);
            builderViewModel.addActionAt(dragboard.getString(), Math.max(0, index - 1));
            success = true;
          }

          dragEvent.setDropCompleted(success);
          dragEvent.consume();
        });

    label.setOnDragEntered(
        _ ->
            builderVBox.getChildren().add(builderVBox.getChildren().indexOf(label), dropIndicator));

    label.setOnDragExited(_ -> builderVBox.getChildren().remove(dropIndicator));
  }

  private void setupDrop(VBox target) {
    target.setOnDragOver(
        dragEvent -> {
          if (dragEvent.getGestureSource() != target && dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
          }
          dragEvent.consume();
        });

    // TODO: actions validation
    target.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {
            String actionText = dragboard.getString();

            builderViewModel.addAction(actionText);
            success = true;
          }

          dragEvent.setDropCompleted(success);
          dragEvent.consume();
        });
  }

  private void setupSearchFieldListener() {
    searchField
        .textProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue == null || newValue.isEmpty()) {
                actionsVBox.getChildren().clear();
                fillActions();
                return;
              }

              String filter = newValue.toLowerCase();
              actionsVBox.getChildren().clear();
              builderViewModel
                  .getActionToTypeMap()
                  .forEach(
                      (_, actionList) -> {
                        List<String> filteredActions =
                            actionList.stream()
                                .filter(action -> action.toLowerCase().contains(filter))
                                .toList();

                        filteredActions.forEach(
                            action -> {
                              Label actionLabel = new Label(action);
                              setupDrag(actionLabel);
                              actionsVBox.getChildren().add(actionLabel);
                            });
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
    actions.forEach(
        action -> {
          Label actionLabel = new Label(action);
          setupDrag(actionLabel);
          vBox.getChildren().add(actionLabel);
        });
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
    actionSequencesVBox.getChildren().clear();
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
    builderViewModel.setActions(actions);
  }
}
