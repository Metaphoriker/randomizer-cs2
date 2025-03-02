package com.revortix.randomizer.ui.view.controller.builder;

import com.google.inject.Inject;
import com.revortix.model.action.Action;
import com.revortix.model.config.keybind.KeyBindType;
import com.revortix.model.persistence.JsonUtil;
import com.revortix.randomizer.ui.view.View;
import com.revortix.randomizer.ui.view.viewmodel.builder.BuilderViewModel;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

@View
public class BuilderActionsViewController {

  private final BuilderViewModel builderViewModel;
  private final JsonUtil jsonUtil;

  @FXML private ToggleButton movementToggleButton;
  @FXML private ToggleButton weaponToggleButton;
  @FXML private ToggleButton inventoryToggleButton;
  @FXML private ToggleButton miscToggleButton;

  @FXML private FlowPane actionsFlowPane;
  @FXML private TextField searchField;

  @Inject
  public BuilderActionsViewController(BuilderViewModel builderViewModel, JsonUtil jsonUtil) {
    this.builderViewModel = builderViewModel;
    this.jsonUtil = jsonUtil;
  }

  private final ChangeListener<Boolean> filterChangeListener = (_, _, _) -> updateActions();

  private void updateActions() {
    actionsFlowPane.getChildren().clear();
    getActivatedFilters()
        .forEach(
            filter -> {
              builderViewModel
                  .getActionToTypeMap()
                  .get(filter)
                  .forEach(
                      action -> {
                        Label actionLabel = new Label(action.getName());
                        Tooltip tooltip = new Tooltip(action.getActionKey().getKey().toUpperCase());
                        tooltip.setShowDelay(Duration.ZERO);
                        actionLabel.setTooltip(tooltip);
                        actionLabel.getStyleClass().add("builder-actions-title");
                        setupDrag(actionLabel, action);
                        actionsFlowPane.getChildren().add(actionLabel);
                      });
            });
  }

  private void setupFilters() {
    movementToggleButton.selectedProperty().addListener(filterChangeListener);
    weaponToggleButton.selectedProperty().addListener(filterChangeListener);
    inventoryToggleButton.selectedProperty().addListener(filterChangeListener);
    miscToggleButton.selectedProperty().addListener(filterChangeListener);
  }

  private List<KeyBindType> getActivatedFilters() {
    List<KeyBindType> activatedFilters = new ArrayList<>();
    if (movementToggleButton.isSelected()) activatedFilters.add(KeyBindType.MOVEMENT);

    if (weaponToggleButton.isSelected()) activatedFilters.add(KeyBindType.WEAPON);

    if (inventoryToggleButton.isSelected()) activatedFilters.add(KeyBindType.INVENTORY);

    if (miscToggleButton.isSelected()) activatedFilters.add(KeyBindType.MISCELLANEOUS);

    return activatedFilters;
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
    setupFilters();
    updateActions();
    setupSearchFieldListener();
  }

  private void setupSearchFieldListener() {
    searchField
        .textProperty()
        .addListener(
            (_, _, newValue) -> {
              if (newValue == null || newValue.isEmpty()) {
                actionsFlowPane.getChildren().clear();
                updateActions();
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
}
