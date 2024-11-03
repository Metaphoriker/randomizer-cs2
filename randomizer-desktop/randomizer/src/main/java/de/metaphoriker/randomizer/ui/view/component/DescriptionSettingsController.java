package de.metaphoriker.randomizer.ui.view.component;

import de.metaphoriker.randomizer.ui.view.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.function.Consumer;

@View
public class DescriptionSettingsController {

    @FXML
    private TextArea textArea;

    private Consumer<String> input;

    @FXML
    void onApply(ActionEvent event) {
        if (input != null) {
            input.accept(textArea.getText());
        }
    }

    public void setOnInput(Consumer<String> input) {
        this.input = input;
    }
}
