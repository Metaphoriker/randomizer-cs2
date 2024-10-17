package de.metaphoriker.randomizer.ui.view.views;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.BuilderViewModel;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

@View
public class BuilderView extends HBox {

  private final BuilderViewModel builderViewModel;

  @FXML private FlowPane actionFlowPane;

  @Inject
  public BuilderView(BuilderViewModel builderViewModel) {
    this.builderViewModel = builderViewModel;
    Platform.runLater(this::initialize);
  }

  private void initialize() {
    HBox.setHgrow(this, Priority.ALWAYS);
    fillFlowPane();
  }

  private void fillFlowPane() {
    actionFlowPane.getChildren().clear();
    List<Action> actions = builderViewModel.getEnabledActions();

    actions.stream()
        .map(action -> new Label(action.getName()))
        .forEach(actionFlowPane.getChildren()::add);
  }
}