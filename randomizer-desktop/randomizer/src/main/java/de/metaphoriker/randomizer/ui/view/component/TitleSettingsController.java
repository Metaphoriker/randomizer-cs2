package de.metaphoriker.randomizer.ui.view.component;

import de.metaphoriker.randomizer.ui.view.View;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

@View
public class TitleSettingsController {

    @FXML
    private TextField textField;

    public TitleSettingsController() {
        Platform.runLater(this::initialize);
    }

    private void initialize() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                textField.setText(oldValue);
            }
        });
    }

    public void onInput(Consumer<String> consumer) {
        textField.setOnAction(_ -> consumer.accept(textField.getText()));
    }
}
