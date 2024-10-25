package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.sequence.ActionSequence;
import de.metaphoriker.model.config.keybind.KeyBindType;
import de.metaphoriker.model.persistence.JsonUtil;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private static final String DELETE_ICON_PATH = "de/metaphoriker/randomizer/images/deleteIcon.png";

  private final Separator dropIndicator = new Separator();

  private final BuilderViewModel builderViewModel;
  private final JsonUtil jsonUtil;

  @FXML private Label actionInFocusLabel;
  @FXML private VBox actionSequencesVBox;
  @FXML private VBox actionSettingsVBox;
  @FXML private VBox actionsVBox;
  @FXML private VBox builderVBox;
  @FXML private Slider maxSlider;
  @FXML private Label maxSliderLabel;
  @FXML private Slider minSlider;
  @FXML private Label minSliderLabel;
  @FXML private TextField searchField;
  @FXML private Label sequenceDescriptionLabel;
  @FXML private Label sequenceNameLabel;

  @Inject
  public BuilderViewController(BuilderViewModel builderViewModel, JsonUtil jsonUtil) {
    this.builderViewModel = builderViewModel;
    this.jsonUtil = jsonUtil;
    Platform.runLater(this::initialize);
  }

  private void setupSliderBindings() {
    minSlider.setValue(builderViewModel.getMinIntervalProperty().get());
    maxSlider.setValue(builderViewModel.getMaxIntervalProperty().get());

    minSlider.valueProperty().bindBidirectional(builderViewModel.getMinIntervalProperty());
    maxSlider.valueProperty().bindBidirectional(builderViewModel.getMaxIntervalProperty());

    minSlider
        .valueProperty()
        .addListener(
            (_, _, newValue) -> {
              int minValue = newValue.intValue();
              int maxValue = builderViewModel.getMaxIntervalProperty().get();

              if (minValue > maxValue) {
                builderViewModel.getMaxIntervalProperty().set(Math.max(minValue + 1, maxValue));
              }

              minSliderLabel.setText(minValue + " ms");
            });

    maxSlider
        .valueProperty()
        .addListener(
            (_, _, newValue) -> {
              int maxValue = newValue.intValue();
              int minValue = builderViewModel.getMinIntervalProperty().get();

              if (maxValue < minValue) {
                builderViewModel.getMinIntervalProperty().set(Math.min(maxValue - 1, minValue));
              }

              maxSliderLabel.setText(maxValue + " ms");
            });
  }

  private void setupBindings() {
    builderViewModel
        .getCurrentActionSequenceProperty()
        .addListener((_, _, newSequence) -> fillBuilderWithActionsOfSequence(newSequence));

    builderViewModel
        .getCurrentActionsProperty()
        .addListener((ListChangeListener<Action>) _ -> updateBuilderVBox());

    builderVBox
        .disableProperty()
        .bind(builderViewModel.getCurrentActionSequenceProperty().isNull());

    actionSettingsVBox
        .visibleProperty()
        .bind(builderViewModel.getActionInFocusProperty().isNotNull());

    builderViewModel
        .getSequenceNameProperty()
        .addListener((_, _, newValue) -> sequenceNameLabel.setText(newValue));

    builderViewModel
        .getSequenceDescriptionProperty()
        .addListener(
            (_, _, newValue) -> {
              String description = newValue;
              if (description.isEmpty()
                  && builderViewModel.getCurrentActionSequenceProperty().get() != null)
                description = "No description provided";
              sequenceDescriptionLabel.setText(description);
            });

    builderViewModel
        .getActionInFocusProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue == null) return;
              actionInFocusLabel.setText(newValue.getName());
            });

    setupSearchFieldListener();
    setupSliderBindings();
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

  @FXML
  void onOpenSequenceFolder(ActionEvent event) {
    try {
      builderViewModel.openSequenceFolder();
    } catch (IOException e) {
      throw new IllegalStateException("Konnte Sequencefolder nicht öffnen, Fehler:", e);
    }
  }

  private void initialize() {
    setupBindings();
    fillActions();
    fillActionSequences();
    setupDrop(builderVBox);
  }

  private void updateBuilderVBox() {
    builderVBox.getChildren().clear();
    builderViewModel
        .getCurrentActionsProperty()
        .forEach(
            action -> {
              Label actionLabel = new Label(action.getName());
              actionLabel.setOnMouseClicked(
                  _ -> builderViewModel.getActionInFocusProperty().set(action));
              setupDragAlreadyDropped(actionLabel, action); // setup special drag within listview
              builderVBox.getChildren().add(actionLabel);
            });
  }

  private void setupDrag(Label label, Action action) {
    label.setCursor(Cursor.HAND);
    label.setOnDragDetected(
        dragEvent -> {
          Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
          dragboard.setDragView(label.snapshot(null, null), dragEvent.getX(), dragEvent.getY());

          ClipboardContent content = new ClipboardContent();
          String serializedAction = jsonUtil.serialize(action);
          content.putString(serializedAction);

          dragboard.setContent(content);
          dragEvent.consume();
        });
  }

  private void setupDragAlreadyDropped(Label label, Action action) {
    label.setCursor(Cursor.HAND);
    label.setOnDragDetected(
        dragEvent -> {
          Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
          dragboard.setDragView(label.snapshot(null, null), dragEvent.getX(), dragEvent.getY());

          ClipboardContent content = new ClipboardContent();
          String serializedAction = jsonUtil.serialize(action);
          builderViewModel.removeAction(action);
          content.putString(serializedAction);

          dragboard.setContent(content);
          dragEvent.consume();
        });

    label.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {
            String serializedAction = dragboard.getString();
            Action droppedAction = jsonUtil.deserializeAction(serializedAction);
            int index = builderVBox.getChildren().indexOf(label);
            builderViewModel.addActionAt(droppedAction, Math.max(0, index - 1));
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

    target.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {
            String serializedAction = dragboard.getString();
            Action droppedAction = jsonUtil.deserializeAction(serializedAction);

            builderViewModel.addAction(droppedAction);
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
                        List<Action> filteredActions =
                            actionList.stream()
                                .filter(action -> action.getName().toLowerCase().contains(filter))
                                .toList();

                        filteredActions.forEach(
                            action -> {
                              Label actionLabel = new Label(action.getName());
                              setupDrag(actionLabel, action);
                              actionsVBox.getChildren().add(actionLabel);
                            });
                      });
            });
  }

  private TitledPane createTitledPane(KeyBindType type, List<Action> actions) {
    TitledPane titledPane = new TitledPane();
    titledPane.setCollapsible(true);
    titledPane.setAnimated(true);
    titledPane.setExpanded(false);
    titledPane.setText(type.name());

    VBox vBox = new VBox();
    actions.forEach(
        action -> {
          Label actionLabel = new Label(action.getName());
          setupDrag(actionLabel, action);
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
              HBox hBox = new HBox();
              Label label = new Label(actionSequence.getName());
              label.setOnMouseClicked(
                  _ -> builderViewModel.getCurrentActionSequenceProperty().set(actionSequence));

              hBox.getChildren().add(label);
              Button deleteSequenceButton = createDeleteButton(actionSequence);

              HBox buttonHBox = new HBox();
              buttonHBox.setAlignment(Pos.CENTER_RIGHT);
              HBox.setHgrow(buttonHBox, Priority.ALWAYS);
              buttonHBox.getChildren().add(deleteSequenceButton);
              hBox.getChildren().add(buttonHBox);

              actionSequencesVBox.getChildren().add(hBox);
            });
  }

  private Button createDeleteButton(ActionSequence actionSequence) {
    Button deleteSequenceButton = new Button("");
    deleteSequenceButton.getStyleClass().add("builder-button");
    // deleteSequenceButton.setGraphic(new ImageView(new Image(DELETE_ICON_PATH)));
    deleteSequenceButton.setOnAction(
        event -> {
          builderViewModel.deleteActionSequence(actionSequence);
          if (builderViewModel.getCurrentActionSequenceProperty().get() != null
              && builderViewModel
                  .getCurrentActionSequenceProperty()
                  .get()
                  .getName()
                  .equalsIgnoreCase(actionSequence.getName()))
            builderViewModel.getCurrentActionSequenceProperty().set(null);
          fillActionSequences();
          event.consume();
        });
    return deleteSequenceButton;
  }

  private void fillBuilderWithActionsOfSequence(ActionSequence sequence) {
    if (sequence == null) return;
    List<Action> actions = builderViewModel.getActionsOfSequence(sequence);
    builderViewModel.setActions(actions);
  }
}
