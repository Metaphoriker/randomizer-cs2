package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.ViewProvider;
import de.metaphoriker.randomizer.ui.view.viewmodel.SequenceBuilderViewModel;
import de.metaphoriker.randomizer.ui.view.viewmodel.SequencesViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@View
public class SequencesView extends VBox implements Initializable {

  private final SequencesViewModel sequencesViewModel;
  private final SequenceBuilderViewModel sequenceBuilderViewModel; // ugly
  private final ViewProvider viewProvider;

  @FXML private VBox sequencesVBox;

  @Inject
  public SequencesView(
      SequencesViewModel sequencesViewModel,
      SequenceBuilderViewModel sequenceBuilderViewModel,
      ViewProvider viewProvider) {
    this.sequencesViewModel = sequencesViewModel;
    this.sequenceBuilderViewModel = sequenceBuilderViewModel;
    this.viewProvider = viewProvider;
  }

  @FXML
  void onAddSequence() {}

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    HBox.setHgrow(this, Priority.ALWAYS);
    fillSequencesVBox();
  }

  private void fillSequencesVBox() {
    sequencesVBox.getChildren().clear();
    sequencesViewModel
        .getActionSequences()
        .forEach(
            sequence -> {
              Label sequenceLabel = new Label(sequence.getName());
              sequenceLabel.setOnMouseClicked(
                  event -> {
                    if (event.getClickCount() == 2) {
                      sequenceBuilderViewModel.setCurrentActionSequence(sequence);
                      viewProvider.triggerViewChange(SequenceBuilderView.class);
                    }
                  });
              sequencesVBox.getChildren().add(sequenceLabel);
            });
  }
}
