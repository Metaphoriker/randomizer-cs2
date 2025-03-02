package com.revortix.randomizer.ui.view.controller.builder;

import com.google.inject.Inject;
import com.revortix.model.action.sequence.ActionSequence;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.ViewProvider;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderViewModel;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController {

  private static final String HBOX_STYLE_CLASS = "builder-sequences-hbox";
  private static final String LABEL_STYLE_CLASS = "builder-sequences-title";
  private static final String ACTION_COUNT_STYLE_CLASS = "logbook-history-entry-action-count";
  private static final double BUTTON_HBOX_SPACING = 10;

  private final ViewProvider viewProvider;
  private final BuilderViewModel builderViewModel;

  @FXML private VBox actionSequencesVBox;
  @FXML private GridPane contentPane;

  @Inject
  public BuilderViewController(ViewProvider viewProvider, BuilderViewModel builderViewModel) {
    this.viewProvider = viewProvider;
    this.builderViewModel = builderViewModel;
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

  @FXML
  private void initialize() {
    fillActionSequences();
  }

  public void fillActionSequences() {
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
        _ -> {
          builderViewModel.getCurrentActionSequenceProperty().set(actionSequence);
          contentPane.getChildren().setAll(viewProvider.requestView(BuilderEditorViewController.class).parent());
        });
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
          contentPane.getChildren().clear();
          fillActionSequences();
          event.consume();
        });
    return deleteSequenceButton;
  }
}
