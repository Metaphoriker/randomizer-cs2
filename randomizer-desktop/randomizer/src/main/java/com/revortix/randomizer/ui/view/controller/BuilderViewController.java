package com.revortix.randomizer.ui.view.controller;

import com.google.inject.Inject;
import com.revortix.model.action.Action;
import com.revortix.model.action.sequence.ActionSequence;
import com.revortix.model.config.keybind.KeyBindType;
import com.revortix.model.persistence.JsonUtil;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.controller.settings.ActionSettingsController;
import com.revortix.randomizer.ui.view.controller.settings.DescriptionSettingsController;
import com.revortix.randomizer.ui.view.controller.settings.TitleSettingsController;
import com.revortix.randomizer.ui.view.viewmodel.BuilderViewModel;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@View
public class BuilderViewController {

    private final Separator dropIndicator = new Separator();

    private final ViewProvider viewProvider;
    private final BuilderViewModel builderViewModel;
    private final JsonUtil jsonUtil;

    @FXML
    private VBox actionSequencesVBox;
    @FXML
    private VBox actionsVBox;
    @FXML
    private VBox builderVBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label sequenceDescriptionLabel;
    @FXML
    private Label sequenceNameLabel;
    @FXML
    private Button randomizeButton;
    @FXML
    private Button actionsClearButton;
    @FXML
    private Button saveSequenceButton;
    @FXML
    private VBox settingsHolder;

    private ActionSettingsController actionSettingsController;

    @Inject
    public BuilderViewController(ViewProvider viewProvider, BuilderViewModel builderViewModel, JsonUtil jsonUtil) {
        this.viewProvider = viewProvider;
        this.builderViewModel = builderViewModel;
        this.jsonUtil = jsonUtil;
        Platform.runLater(this::initialize);
    }

    private void setupBindings() {
        builderViewModel
                .getCurrentActionSequenceProperty()
                .addListener((_, _, newSequence) -> {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("A sequence with this name already exist.");
            alert.show();
            return;
        }
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
        actionSettingsController = viewProvider.requestView(ActionSettingsController.class).controller();
        actionSettingsController.bindOnVisibleProperty(visible -> {
            if (visible)
                settingsHolder.getChildren().setAll(viewProvider.requestView(ActionSettingsController.class).parent());
            else
                settingsHolder.getChildren().clear();
        });
        builderViewModel.getCurrentActionSequenceProperty().addListener((_, _, _) -> actionSettingsController.setAction(null));
    }

    private boolean doesAnotherActionSequenceWithThisNameExist() {
        return builderViewModel
                .getActionSequences()
                .stream()
                .filter(actionSequence -> !actionSequence.equals(builderViewModel.getCurrentActionSequenceProperty().get()))
                .anyMatch(actionSequence -> actionSequence.getName().equalsIgnoreCase(builderViewModel.getSequenceNameProperty().get()));
    }

    private void initTitleSettings() {
        sequenceNameLabel.setCursor(Cursor.HAND);
        TitleSettingsController titleSettingsController = viewProvider.requestView(TitleSettingsController.class).controller();
        titleSettingsController.setOnInput(input -> {
            builderViewModel.getSequenceNameProperty().set(input);
            settingsHolder.getChildren().clear();
        });
        sequenceNameLabel.setOnMouseClicked(_ -> {
            if (builderViewModel.getCurrentActionSequenceProperty().get() == null)
                return;
            actionSettingsController.setAction(null);
            titleSettingsController.setText(builderViewModel.getSequenceNameProperty().get() == null ?
                    "" : builderViewModel.getSequenceNameProperty().get());
            settingsHolder.getChildren().setAll(viewProvider.requestView(TitleSettingsController.class).parent());
        });
    }

    private void initDescriptionSettings() {
        sequenceDescriptionLabel.setCursor(Cursor.HAND);
        DescriptionSettingsController descriptionSettingsController = viewProvider.requestView(DescriptionSettingsController.class).controller();
        descriptionSettingsController.setOnInput(input -> {
            builderViewModel.getSequenceDescriptionProperty().set(input);
            settingsHolder.getChildren().clear();
        });
        sequenceDescriptionLabel.setOnMouseClicked(_ -> {
            if (builderViewModel.getCurrentActionSequenceProperty().get() == null)
                return;
            actionSettingsController.setAction(null);
            descriptionSettingsController.setText(builderViewModel.getSequenceDescriptionProperty().get() == null ?
                    "" : builderViewModel.getSequenceDescriptionProperty().get());
            settingsHolder.getChildren().setAll(viewProvider.requestView(DescriptionSettingsController.class).parent());
        });
    }

    private void updateBuilderVBox() {
        builderVBox.getChildren().clear();
        builderViewModel
                .getCurrentActionsProperty()
                .forEach(
                        action -> {
                            Label actionLabel = new Label(action.getName());
                            actionLabel.getStyleClass().add("selected-actions-label");
                            actionLabel.setOnMouseClicked(
                                    _ -> actionSettingsController.setAction(action));
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

        label.setOnDragOver(
                dragEvent -> {
                    if (dragEvent.getGestureSource() != label && dragEvent.getDragboard().hasString()) {
                        dragEvent.acceptTransferModes(TransferMode.MOVE);

                        // Ermitteln des korrekten Index für den dropIndicator
                        int labelIndex = builderVBox.getChildren().indexOf(label);
                        double mouseY = dragEvent.getY();
                        double labelHeight = label.getHeight();

                        // Hysterese hinzufügen, um das Springen zu verhindern
                        if (mouseY < labelHeight / 4) {
                            labelIndex = Math.max(0, labelIndex - 1); // Einfügen vor dem Label
                        } else if (mouseY > labelHeight * 3 / 4) {
                            labelIndex++; // Einfügen nach dem Label
                        }

                        // Sicherstellen, dass der dropIndicator nicht am Ende eingefügt wird
                        labelIndex = Math.min(labelIndex, builderVBox.getChildren().size() - 1);

                        // dropIndicator aktualisieren
                        int dropIndicatorIndex = builderVBox.getChildren().indexOf(dropIndicator);
                        if (dropIndicatorIndex != labelIndex) {
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
                        int actionIndex = 0;

                        // actionIndex basierend auf der Position des dropIndicator berechnen
                        for (int i = 0; i < dropIndicatorIndex; i++) {
                            if (builderVBox.getChildren().get(i) instanceof Label) {
                                actionIndex++;
                            }
                        }

                        builderViewModel.addActionAt(droppedAction, actionIndex);
                        success = true;
                    }

                    dragEvent.setDropCompleted(success);
                    builderVBox.getChildren().remove(dropIndicator);
                    dragEvent.consume();
                });

        label.setOnDragEntered(
                dragEvent -> {
                    if (dragEvent.getGestureSource() != label && dragEvent.getDragboard().hasString()) {
                        // Verzögertes Hinzufügen des dropIndicator
                        PauseTransition delay = new PauseTransition(Duration.millis(100));
                        delay.setOnFinished(event -> {
                            if (!builderVBox.getChildren().contains(dropIndicator)) {
                                builderVBox.getChildren().add(0, dropIndicator);
                            }
                        });
                        delay.play();
                    }
                    dragEvent.consume();
                });
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

                        if (builderVBox.getChildren().contains(dropIndicator)) {
                            int index = builderVBox.getChildren().indexOf(dropIndicator);
                            builderVBox.getChildren().remove(dropIndicator);
                            builderViewModel.addActionAt(droppedAction, index);
                        } else {
                            builderViewModel.addAction(droppedAction);
                        }
                        success = true;
                    }

                    dragEvent.setDropCompleted(success);
                    dragEvent.consume();
                });

        target.setOnDragExited(
                dragEvent -> {
                    builderVBox.getChildren().remove(dropIndicator);
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
                    HBox hBox = new HBox();
                    hBox.setCursor(Cursor.HAND);
                    hBox.getStyleClass().add("builder-sequences-hbox");
                    Label label = new Label(actionSequence.getName());
                    label.getStyleClass().add("builder-sequences-title");
                    hBox.setOnMouseClicked(
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
        deleteSequenceButton.getStyleClass().add("builder-sequences-delete-button");
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
