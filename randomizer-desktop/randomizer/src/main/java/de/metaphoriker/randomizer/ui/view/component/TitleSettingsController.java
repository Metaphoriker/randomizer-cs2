package de.metaphoriker.randomizer.ui.view.component;

import de.metaphoriker.randomizer.ui.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

@View
public class TitleSettingsController {

    @FXML
    private TextField textField;

    private Consumer<String> input;

    public TitleSettingsController() {
        Platform.runLater(this::initialize);
    }

    @FXML
    void onApply(ActionEvent event) {
        if (input != null) {
            input.accept(textField.getText());
            textField.setText("");
        }
    }

    private void initialize() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                textField.setText(oldValue);
            }
        });
    }


    public void setOnInput(Consumer<String> consumer) {
        input = consumer;
    }
}
