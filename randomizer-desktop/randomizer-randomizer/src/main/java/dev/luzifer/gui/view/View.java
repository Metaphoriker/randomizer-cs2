package dev.luzifer.gui.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class View<T extends ViewModel> extends Stage implements Initializable {

  private final T viewModel;

  protected View(T viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {}

  public void onClose() {}

  protected T getViewModel() {
    return viewModel;
  }
}
