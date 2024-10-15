package de.metaphoriker.view.views;

import com.google.inject.Inject;
import de.metaphoriker.model.action.Action;
import de.metaphoriker.view.View;
import de.metaphoriker.view.viewmodel.BuilderViewModel;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

@View
public class BuilderView extends HBox {

  @Inject private BuilderViewModel builderViewModel;

  @FXML private FlowPane actionFlowPane;

  public BuilderView() {
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
        .map(action -> new Label(action.name()))
        .forEach(actionFlowPane.getChildren()::add);
  }
}
