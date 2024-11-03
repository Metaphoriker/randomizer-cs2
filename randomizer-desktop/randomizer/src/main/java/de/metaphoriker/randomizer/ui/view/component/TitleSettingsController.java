package de.metaphoriker.randomizer.ui.view.component;

import de.metaphoriker.randomizer.ui.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

@View
public class TitleSettingsController {

    @FXML
    private TextField textField;

    public void onInput(Consumer<String> consumer) {
        textField.setOnAction(_ -> consumer.accept(textField.getText()));
    }
}
