package com.revortix.randomizer.ui.view.controller.settings;

import com.google.inject.Inject;
import com.revortix.model.action.Action;
import com.revortix.model.config.keybind.KeyBindType;
import com.revortix.model.persistence.JsonUtil;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderViewModel;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

@View
public class BuilderViewController2 {

  private final BuilderViewModel builderViewModel;
  private final JsonUtil jsonUtil;

  @FXML private VBox actionsVBox;
  @FXML private TextField searchField;

  @Inject
  public BuilderViewController2(BuilderViewModel builderViewModel, JsonUtil jsonUtil) {
    this.builderViewModel = builderViewModel;
    this.jsonUtil = jsonUtil;
    Platform.runLater(this::initialize);
  }

  private void initialize() {
    fillActions();
    setupSearchFieldListener();
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
}
