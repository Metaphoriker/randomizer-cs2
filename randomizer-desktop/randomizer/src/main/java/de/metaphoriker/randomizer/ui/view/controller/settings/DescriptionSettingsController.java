package de.metaphoriker.randomizer.ui.view.controller.settings;

import de.metaphoriker.randomizer.ui.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.function.Consumer;

@View
public class DescriptionSettingsController {

    @FXML
    private TextArea textArea;

    private Consumer<String> input;

    public DescriptionSettingsController() {
        Platform.runLater(this::initialize);
    }

    private void initialize() {
        textArea.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue.length() > 200) {
                textArea.setText(oldValue);
            }
        });
    }

    @FXML
    void onApply(ActionEvent event) {
        if (input != null) {
            input.accept(textArea.getText());
            textArea.setText("");
        }
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void setOnInput(Consumer<String> input) {
        this.input = input;
    }
}
