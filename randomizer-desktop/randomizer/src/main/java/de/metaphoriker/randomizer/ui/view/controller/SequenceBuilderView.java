package de.metaphoriker.randomizer.ui.view.controller;

import com.google.inject.Inject;
import de.metaphoriker.randomizer.ui.view.View;
import de.metaphoriker.randomizer.ui.view.viewmodel.SequenceBuilderViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

@View
public class SequenceBuilderView extends HBox implements Initializable {

  private final SequenceBuilderViewModel sequenceBuilderViewModel;

  @Inject
  public SequenceBuilderView(SequenceBuilderViewModel sequenceBuilderViewModel) {
    this.sequenceBuilderViewModel = sequenceBuilderViewModel;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {}
}
