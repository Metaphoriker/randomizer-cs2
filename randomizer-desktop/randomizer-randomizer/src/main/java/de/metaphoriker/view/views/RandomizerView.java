package de.metaphoriker.view.views;

import com.google.inject.Inject;
import de.metaphoriker.view.View;
import de.metaphoriker.view.viewmodel.RandomizerViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

@View
public class RandomizerView extends HBox implements Initializable {

  private final RandomizerViewModel randomizerViewModel;

  @Inject
  public RandomizerView(RandomizerViewModel randomizerViewModel) {
    this.randomizerViewModel = randomizerViewModel;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {}
}
