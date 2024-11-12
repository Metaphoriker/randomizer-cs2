package com.revortix.randomizer.ui.view.controller.settings;

import com.revortix.randomizer.ui.view.View;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.util.function.Consumer;

@View
public class TitleSettingsController {

    @FXML
    private VBox root;
    @FXML
    private TextField textField;

    @Setter
    private Runnable onPanelClose;
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

    @FXML
    void onPanelClose(ActionEvent event) {
        if (onPanelClose != null)
            onPanelClose.run();
    }

    private void initialize() {
        textField.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                textField.setText(oldValue);
            }
        });
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setOnInput(Consumer<String> consumer) {
        input = consumer;
    }

    public ReadOnlyBooleanProperty visibleProperty() {
        return root.visibleProperty();
    }
}
