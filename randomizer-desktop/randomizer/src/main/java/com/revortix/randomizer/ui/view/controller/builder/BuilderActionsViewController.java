package com.revortix.randomizer.ui.view.controller.builder;

import com.google.inject.Inject;
import com.revortix.model.action.Action;
import com.revortix.model.config.keybind.KeyBindType;
import com.revortix.model.persistence.JsonUtil;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderViewModel;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

@View
public class BuilderActionsViewController {

  private final BuilderViewModel builderViewModel;
  private final JsonUtil jsonUtil;

  @FXML private FlowPane actionsFlowPane;
  @FXML private TextField searchField;

  @Inject
  public BuilderActionsViewController(BuilderViewModel builderViewModel, JsonUtil jsonUtil) {
    this.builderViewModel = builderViewModel;
    this.jsonUtil = jsonUtil;
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

  @FXML
  private void initialize() {
    fillActions();
    setupSearchFieldListener();
  }

  private void setupSearchFieldListener() {
    searchField
        .textProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue == null || newValue.isEmpty()) {
                actionsFlowPane.getChildren().clear();
                fillActions();
                return;
              }

              String filter = newValue.toLowerCase();
              actionsFlowPane.getChildren().clear();
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
                              actionsFlowPane.getChildren().add(actionLabel);
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
    titledPane.setContent(vBox);

    return titledPane;
  }

  private void fillActions() {
    builderViewModel
        .getActionToTypeMap()
        .forEach(
            (type, actionList) ->
                actionsFlowPane.getChildren().add(createTitledPane(type, actionList)));
  }
}
