package de.metaphoriker.view.views;

import com.google.inject.Inject;
import de.metaphoriker.view.View;
import java.net.URL;
import java.util.ResourceBundle;

import de.metaphoriker.view.viewmodel.RandomizerViewModel;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

@View
public class RandomizerView extends HBox implements Initializable {

  @Inject
  private RandomizerViewModel randomizerViewModel;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {}
}
