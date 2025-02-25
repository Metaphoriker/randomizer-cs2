package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.model.action.Action;
import com.revortix.model.action.sequence.ActionSequence;
import com.revortix.model.config.keybind.KeyBindType;
import com.revortix.model.persistence.JsonUtil;
import com.revortix.randomizer.ui.RandomizerApplication;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.controller.settings.ActionSettingsController;
import com.revortix.randomizer.ui.view.controller.settings.DescriptionSettingsController;
import com.revortix.randomizer.ui.view.controller.settings.TitleSettingsController;
import com.revortix.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private static final String HBOX_STYLE_CLASS = "builder-sequences-hbox";
  private static final String LABEL_STYLE_CLASS = "builder-sequences-title";
  private static final String ACTION_COUNT_STYLE_CLASS = "logbook-history-entry-action-count";
  private static final double BUTTON_HBOX_SPACING = 10;
  private static final String ACTION_NAME_STYLING = "logbook-sequence-actions-name";
  private static final String START_ACTION_NAME_STYLING = "logbook-sequence-actions-name-start";
  private static final String MIDDLE_ACTION_NAME_STYLING = "logbook-sequence-actions-name-middle";
  private static final String END_ACTION_NAME_STYLING = "logbook-sequence-actions-name-end";
  private static final String START_ACTIVE_ACTION_NAME_STYLING =
      "logbook-sequence-actions-name-start-active";
  private static final String MIDDLE_ACTIVE_ACTION_NAME_STYLING =
      "logbook-sequence-actions-name-middle-active";
  private static final String END_ACTIVE_ACTION_NAME_STYLING =
      "logbook-sequence-actions-name-end-active";

  private final ObjectProperty<Label> labelInFocusProperty = new SimpleObjectProperty<>();
  private final Separator dropIndicator = new Separator();

  private final ViewProvider viewProvider;
  private final BuilderViewModel builderViewModel;
  private final JsonUtil jsonUtil;

  @FXML private VBox actionSequencesVBox;
  @FXML private VBox actionsVBox;
  @FXML private VBox builderVBox;
  @FXML private TextField searchField;
  @FXML private Label sequenceDescriptionLabel;
  @FXML private ImageView sequenceNameImageView;
  @FXML private Label sequenceNameLabel;
  @FXML private Button randomizeButton;
  @FXML private Button actionsClearButton;
  @FXML private Button saveSequenceButton;
  @FXML private VBox settingsHolder;

  private ActionSettingsController actionSettingsController;

  @Inject
  public BuilderViewController(
      ViewProvider viewProvider, BuilderViewModel builderViewModel, JsonUtil jsonUtil) {
    this.viewProvider = viewProvider;
    this.builderViewModel = builderViewModel;
    this.jsonUtil = jsonUtil;
    Platform.runLater(this::initialize);
  }

  private void setupBindings() {
    builderViewModel
        .getCurrentActionSequenceProperty()
        .addListener(
            (_, _, newSequence) -> {
              fillBuilderWithActionsOfSequence(newSequence);
              settingsHolder.getChildren().clear();
            });

    builderViewModel
        .getCurrentActionsProperty()
        .addListener((ListChangeListener<Action>) _ -> updateBuilderVBox());

    builderVBox
        .disableProperty()
        .bind(builderViewModel.getCurrentActionSequenceProperty().isNull());

    randomizeButton
        .disableProperty()
        .bind(builderViewModel.getCurrentActionSequenceProperty().isNull());

    actionsClearButton
        .disableProperty()
        .bind(builderViewModel.getCurrentActionSequenceProperty().isNull());

    saveSequenceButton
        .disableProperty()
        .bind(builderViewModel.getCurrentActionSequenceProperty().isNull());

    sequenceDescriptionLabel.textProperty().bind(builderViewModel.getSequenceDescriptionProperty());
    sequenceNameLabel.textProperty().bind(builderViewModel.getSequenceNameProperty());
    sequenceNameImageView.visibleProperty().bind(sequenceNameLabel.textProperty().isNotEmpty());

    labelInFocusProperty.addListener(
        (_, oldLabel, newLabel) -> {
          if (oldLabel != null) setPositionalStyling(oldLabel, false);
          if (newLabel != null) setPositionalStyling(newLabel, true);
        });

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
    if (doesAnotherActionSequenceWithThisNameExist()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("NOTICE");
      alert
          .getDialogPane()
          .getStylesheets()
          .add(RandomizerApplication.class.getResource("alert-style.css").toExternalForm());
      alert.setContentText("A sequence with this name already exist. Do you want to overwrite?");
      alert
          .showAndWait()
          .filter(response -> response == ButtonType.OK)
          .ifPresent(
              response -> {
                builderViewModel.saveActionSequence();
                fillActionSequences();
              });
      return;
    }
    builderViewModel.saveActionSequence();
    builderViewModel.getCurrentActionSequenceProperty().set(null);
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
    initActionSettings();
    initTitleSettings();
    initDescriptionSettings();
    initDropIndicator();
    setupBindings();
    fillActions();
    fillActionSequences();
    setupDrop(builderVBox);
  }

  private void initDropIndicator() {
    dropIndicator.getStyleClass().add("builder-separator");
  }

  private void initActionSettings() {
    actionSettingsController =
        viewProvider.requestView(ActionSettingsController.class).controller();
    actionSettingsController.bindOnVisibleProperty(
        visible -> {
          if (visible)
            settingsHolder
                .getChildren()
                .setAll(viewProvider.requestView(ActionSettingsController.class).parent());
          else {
            labelInFocusProperty.set(null);
            settingsHolder.getChildren().clear();
          }
        });
  }

  private boolean doesAnotherActionSequenceWithThisNameExist() {
    return builderViewModel.getActionSequences().stream()
        .filter(
            actionSequence ->
                !actionSequence.equals(builderViewModel.getCurrentActionSequenceProperty().get()))
        .anyMatch(
            actionSequence ->
                actionSequence
                    .getName()
                    .equalsIgnoreCase(builderViewModel.getSequenceNameProperty().get()));
  }

  private void initTitleSettings() {
    sequenceNameLabel.setCursor(Cursor.HAND);
    TitleSettingsController titleSettingsController =
        viewProvider.requestView(TitleSettingsController.class).controller();
    titleSettingsController.setOnPanelClose(() -> settingsHolder.getChildren().clear());
    titleSettingsController.setOnInput(
        input -> {
          builderViewModel.getSequenceNameProperty().set(input);
          settingsHolder.getChildren().clear();
        });
    sequenceNameLabel.setOnMouseClicked(
        _ -> {
          if (builderViewModel.getCurrentActionSequenceProperty().get() == null) return;
          actionSettingsController.setAction(null);
          labelInFocusProperty.set(null);
          titleSettingsController.setText(
              builderViewModel.getSequenceNameProperty().get() == null
                  ? ""
                  : builderViewModel.getSequenceNameProperty().get());
          settingsHolder
              .getChildren()
              .setAll(viewProvider.requestView(TitleSettingsController.class).parent());
        });
  }

  private void initDescriptionSettings() {
    sequenceDescriptionLabel.setCursor(Cursor.HAND);
    DescriptionSettingsController descriptionSettingsController =
        viewProvider.requestView(DescriptionSettingsController.class).controller();
    descriptionSettingsController.setOnPanelClose(() -> settingsHolder.getChildren().clear());
    descriptionSettingsController.setOnInput(
        input -> {
          if (input.isEmpty() || input.isBlank()) {
            return;
          }
          builderViewModel.getSequenceDescriptionProperty().set(input);
          settingsHolder.getChildren().clear();
        });
    sequenceDescriptionLabel.setOnMouseClicked(
        _ -> {
          if (builderViewModel.getCurrentActionSequenceProperty().get() == null) return;
          actionSettingsController.setAction(null);
          labelInFocusProperty.set(null);
          descriptionSettingsController.setText(
              builderViewModel.getSequenceDescriptionProperty().get() == null
                  ? ""
                  : builderViewModel.getSequenceDescriptionProperty().get());
          settingsHolder
              .getChildren()
              .setAll(viewProvider.requestView(DescriptionSettingsController.class).parent());
        });
  }

  private void updateBuilderVBox() {
    labelInFocusProperty.set(null);
    actionSettingsController.setAction(null);
    builderVBox.getChildren().clear();
    builderViewModel
        .getCurrentActionsProperty()
        .forEach(
            action -> {
              Label actionLabel = new Label(action.getName());
              actionLabel.setOnMouseClicked(
                  _ -> {
                    actionSettingsController.setAction(action);
                    labelInFocusProperty.set(actionLabel);
                  });
              setupDragAlreadyDropped(actionLabel, action); // setup special drag within listview
              builderVBox.getChildren().add(actionLabel);
            });

    builderVBox.getChildren().stream()
        .map(Label.class::cast)
        .forEach(node -> setPositionalStyling(node, false));
  }

  private void setPositionalStyling(Label label, boolean active) {
    int index = builderVBox.getChildren().indexOf(label);

    if (index == 0) {
      label.getStyleClass().clear();
      label.getStyleClass().add(ACTION_NAME_STYLING);
      label
          .getStyleClass()
          .add(active ? START_ACTIVE_ACTION_NAME_STYLING : START_ACTION_NAME_STYLING);
      return;
    }

    if (index == builderVBox.getChildren().size() - 1) {
      label.getStyleClass().clear();
      label.getStyleClass().add(ACTION_NAME_STYLING);
      label.getStyleClass().add(active ? END_ACTIVE_ACTION_NAME_STYLING : END_ACTION_NAME_STYLING);
      return;
    }

    label.getStyleClass().clear();
    label.getStyleClass().add(ACTION_NAME_STYLING);
    label
        .getStyleClass()
        .add(active ? MIDDLE_ACTIVE_ACTION_NAME_STYLING : MIDDLE_ACTION_NAME_STYLING);
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
          builderViewModel.removeAction(
              action); // Entferne die Action *vor* dem Hinzufügen zum Dragboard
          content.putString(serializedAction);

          dragboard.setContent(content);
          dragEvent.consume();
        });

    label.setOnDragOver(
        dragEvent -> {
          if (dragEvent.getGestureSource() != label && dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.MOVE);

            int labelIndex = builderVBox.getChildren().indexOf(label);
            double mouseY = dragEvent.getY();
            double labelHeight = label.getHeight();

            // Vereinfachte Hysterese: Nur prüfen, ob Maus im oberen oder unteren Drittel des Labels
            // ist.
            if (mouseY < labelHeight / 3.0) {
              labelIndex = Math.max(0, labelIndex - 1); // Einfügen vor dem Label
            } else if (mouseY > labelHeight * 2.0 / 3.0) {
              labelIndex++; // Einfügen nach dem Label
            }
            // DropIndicator hinzufügen, falls nicht vorhanden.
            if (!builderVBox.getChildren().contains(dropIndicator)) {
              builderVBox.getChildren().add(labelIndex, dropIndicator);
            }
            // DropIndicator Position aktualisieren, wenn nötig.
            else if (builderVBox.getChildren().indexOf(dropIndicator) != labelIndex) {
              builderVBox.getChildren().remove(dropIndicator);
              builderVBox.getChildren().add(labelIndex, dropIndicator);
            }
          }
          dragEvent.consume();
        });

    label.setOnDragDropped(
        dragEvent -> {
          Dragboard dragboard = dragEvent.getDragboard();
          boolean success = false;

          if (dragboard.hasString()) {
            String serializedAction = dragboard.getString();
            Action droppedAction = jsonUtil.deserializeAction(serializedAction);

            int dropIndicatorIndex = builderVBox.getChildren().indexOf(dropIndicator);

            if (dropIndicatorIndex != -1) {
              builderViewModel.addActionAt(droppedAction, dropIndicatorIndex);
              success = true;
            }
          }

          dragEvent.setDropCompleted(success);
          builderVBox.getChildren().remove(dropIndicator); // Immer entfernen
          dragEvent.consume();
        });

    // Kein DragEntered mehr nötig
  }

  private void setupDrop(VBox target) {
    target.setOnDragOver(
        dragEvent -> {
          if (dragEvent.getGestureSource() != target && dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
            // Hier muss der dropIndicator am Ende hinzugefügt werden, falls über die VBox gedragged
            // wird, und nicht über die labels
            if (!builderVBox.getChildren().contains(dropIndicator)) {
              builderVBox.getChildren().add(dropIndicator);
            }
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

            // Stelle sicher, dass hier keine Exception geworfen wird, falls aus irgend einem Grund
            // kein Indikator gesetzt wurde
            if (builderVBox.getChildren().contains(dropIndicator)) {
              int index = builderVBox.getChildren().indexOf(dropIndicator);
              builderVBox.getChildren().remove(dropIndicator);
              builderViewModel.addActionAt(droppedAction, index);
            } else {
              // Wenn kein Indikator gesetzt wurde, am Ende einfügen
              builderViewModel.addAction(droppedAction);
            }
            success = true;
          }

          dragEvent.setDropCompleted(success);
          dragEvent.consume();
        });

    target.setOnDragExited(
        dragEvent -> {
          // Nur entfernen, wenn vorhanden
          if (builderVBox.getChildren().contains(dropIndicator)) {
            builderVBox.getChildren().remove(dropIndicator);
          }
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
                              actionLabel.getStyleClass().add("builder-actions-title");
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
    titledPane.getStyleClass().add("builder-actions-category");
    titledPane.setText(type.name());

    VBox vBox = new VBox();
    actions.forEach(
        action -> {
          Label actionLabel = new Label(action.getName());
          actionLabel.setTooltip(new Tooltip(action.getActionKey().getKey()));
          actionLabel.getStyleClass().add("builder-actions-title");
          setupDrag(actionLabel, action);
          vBox.getStyleClass().add("builder-actions-title-vbox");
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
    List<ActionSequence> actionSequences = builderViewModel.getActionSequences();
    actionSequences.sort(Comparator.comparing(ActionSequence::getName));

    actionSequences.forEach(
        actionSequence -> {
          HBox sequenceHBox = createSequenceHBox(actionSequence);
          actionSequencesVBox.getChildren().add(sequenceHBox);
        });
  }

  private HBox createSequenceHBox(ActionSequence actionSequence) {
    HBox hBox = new HBox();
    hBox.setCursor(Cursor.HAND);
    hBox.getStyleClass().add(HBOX_STYLE_CLASS);

    Label sequenceLabel = createLabel(actionSequence.getName(), LABEL_STYLE_CLASS);
    hBox.setOnMouseClicked(
        _ -> builderViewModel.getCurrentActionSequenceProperty().set(actionSequence));
    hBox.getChildren().add(sequenceLabel);

    HBox buttonHBox = createButtonHBox(actionSequence);
    hBox.getChildren().add(buttonHBox);

    return hBox;
  }

  private HBox createButtonHBox(ActionSequence actionSequence) {
    HBox buttonHBox = new HBox();
    buttonHBox.setAlignment(Pos.CENTER_RIGHT);
    HBox.setHgrow(buttonHBox, Priority.ALWAYS);
    buttonHBox.setSpacing(BUTTON_HBOX_SPACING);

    Label actionsCountLabel =
        createLabel(String.valueOf(actionSequence.getActions().size()), ACTION_COUNT_STYLE_CLASS);
    Button deleteButton = createDeleteButton(actionSequence);
    buttonHBox.getChildren().addAll(actionsCountLabel, deleteButton);

    return buttonHBox;
  }

  private Label createLabel(String text, String styleClass) {
    Label label = new Label(text);
    label.getStyleClass().add(styleClass);
    return label;
  }

  private Button createDeleteButton(ActionSequence actionSequence) {
    Button deleteSequenceButton = new Button("");
    deleteSequenceButton.getStyleClass().add("builder-sequences-delete-button");
    deleteSequenceButton.setOnAction(
        event -> {
          if (builderViewModel.getCurrentActionSequenceProperty().get() != null
              && builderViewModel.getCurrentActionSequenceProperty().get().equals(actionSequence)) {
            builderViewModel.getCurrentActionSequenceProperty().set(null);
          }
          builderViewModel.deleteActionSequence(actionSequence);
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
