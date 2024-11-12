package com.revortix.randomizer.ui.view.controller.settings;

import com.revortix.randomizer.ui.view.View;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.util.function.Consumer;

@View
public class DescriptionSettingsController {

    @FXML
    private VBox root;
    @FXML
    private TextArea textArea;

    @Setter
    private Runnable onPanelClose;
    private Consumer<String> input;

    public DescriptionSettingsController() {
        Platform.runLater(this::initialize);
    }

    @FXML
    void onPanelClose(ActionEvent event) {
        if (onPanelClose != null)
            onPanelClose.run();
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

    public ReadOnlyBooleanProperty visibleProperty() {
        return root.visibleProperty();
    }
}
