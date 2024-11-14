package com.revortix.randomizer.ui.view.component;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import org.controlsfx.control.RangeSlider;

import java.util.function.Consumer;

public class MinMaxSlider extends HBox {

    private final RangeSlider rangeSlider = new RangeSlider(0, 300, 30, 100); // max 5 minutes
    private final Label minLabel = new Label("s");
    private final Label maxLabel = new Label("s");
    private final Spinner<Integer> minSpinner = new Spinner<>(0, 300, 30);
    private final Spinner<Integer> maxSpinner = new Spinner<>(0, 300, 100);

    private Consumer<Double> minValueChangeConsumer;
    private Consumer<Double> maxValueChangeConsumer;

    public MinMaxSlider() {
        Platform.runLater(this::initialize);
    }

    public boolean isEven() {
        return rangeSlider.getLowValue() == rangeSlider.getHighValue();
    }

    public DoubleProperty getMinProperty() {
        return rangeSlider.lowValueProperty();
    }

    public DoubleProperty getMaxProperty() {
        return rangeSlider.highValueProperty();
    }

    public void setOnMinValueChange(Consumer<Double> consumer) {
        minValueChangeConsumer = consumer;
    }

    public void setOnMaxValueChange(Consumer<Double> consumer) {
        maxValueChangeConsumer = consumer;
    }

    public void setMaxHigherValue(int value) {
        rangeSlider.setMax(value);
        ((IntegerSpinnerValueFactory) maxSpinner.getValueFactory()).setMax(value);
    }

    public void setMinLowerValue(int value) {
        rangeSlider.setMin(value);
        ((IntegerSpinnerValueFactory) minSpinner.getValueFactory()).setMin(value);
    }

    public void setMinMaxValue(int min, int max) {
        minLabel.setText(min + "s");
        maxLabel.setText(max + "s");
        rangeSlider.setLowValue(min);
        rangeSlider.setHighValue(max);
        minSpinner.getValueFactory().setValue(min);
        maxSpinner.getValueFactory().setValue(max);
    }

    private void initialize() {
        initializeStyleclasses();
        initializeSpinners();
        setAlignment(Pos.CENTER);
        HBox minBox = new HBox(5);
        minBox.getChildren().addAll(minLabel, minSpinner);
        HBox maxBox = new HBox(5);
        maxBox.getChildren().addAll(maxSpinner, maxLabel);
        getChildren().addAll(minBox, rangeSlider, maxBox);

        rangeSlider.setBlockIncrement(1);
        setListener();
    }

    private void initializeStyleclasses() {
        minLabel.getStyleClass().add("rangeslider-min-label");
        maxLabel.getStyleClass().add("rangeslider-max-label");
        minSpinner.getStyleClass().add("rangeslider-spinner");
        maxSpinner.getStyleClass().add("rangeslider-spinner");
    }

    private void initializeSpinners() {
        // Set initial min/max values for spinners based on the rangeSlider
        ((IntegerSpinnerValueFactory) minSpinner.getValueFactory()).setMin((int) rangeSlider.getMin());
        ((IntegerSpinnerValueFactory) minSpinner.getValueFactory()).setMax((int) rangeSlider.getMax());
        ((IntegerSpinnerValueFactory) maxSpinner.getValueFactory()).setMin((int) rangeSlider.getMin());
        ((IntegerSpinnerValueFactory) maxSpinner.getValueFactory()).setMax((int) rangeSlider.getMax());

        minSpinner.valueProperty().addListener((_, _, newValue) -> rangeSlider.setLowValue(newValue));
        maxSpinner.valueProperty().addListener((_, _, newValue) -> rangeSlider.setHighValue(newValue));
        minSpinner.setEditable(true);
        maxSpinner.setEditable(true);
    }

    private void setListener() {
        rangeSlider.lowValueProperty().addListener(
                (_, _, newValue) -> {
                    minSpinner.getValueFactory().setValue(newValue.intValue());
                    minLabel.setText(newValue.intValue() + "s");
                    if (minValueChangeConsumer != null) {
                        minValueChangeConsumer.accept(newValue.doubleValue());
                    }
                }
        );

        rangeSlider.highValueProperty().addListener(
                (_, _, newValue) -> {
                    maxSpinner.getValueFactory().setValue(newValue.intValue());
                    maxLabel.setText(newValue.intValue() + "s");
                    if (maxValueChangeConsumer != null) {
                        maxValueChangeConsumer.accept(newValue.doubleValue());
                    }
                }
        );
    }
}