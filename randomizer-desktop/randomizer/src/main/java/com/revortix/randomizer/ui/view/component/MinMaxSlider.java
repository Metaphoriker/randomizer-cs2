package com.revortix.randomizer.ui.view.component;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.control.RangeSlider;

import java.util.function.Consumer;

public class MinMaxSlider extends HBox {

    private final RangeSlider rangeSlider = new RangeSlider(0, 300, 30, 100); // max 5 minutes
    private final Label minLabel = new Label("s");
    private final Label maxLabel = new Label("s");

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
        maxLabel.setText(value + "s");
        rangeSlider.setMax(value);
    }

    public void setMinLowerValue(int value) {
        minLabel.setText(value + "s");
        rangeSlider.setMin(value);
    }

    public void setMinMaxValue(double min, double max) {
        rangeSlider.setLowValue(min);
        rangeSlider.setHighValue(max);
    }

    private void initialize() {
        initializeLabels();
        getChildren().addAll(minLabel, rangeSlider, maxLabel);

        rangeSlider.setBlockIncrement(5);
        setListener();
    }

    private void initializeLabels() {
        minLabel.getStyleClass().add("rangeslider-min-label");
        maxLabel.getStyleClass().add("rangeslider-max-label");
    }

    private void setListener() {
        rangeSlider.lowValueProperty().addListener(
                (_, _, newValue) -> {
                    minLabel.setText(newValue.intValue() + "s");
                    if (minValueChangeConsumer != null) {
                        minValueChangeConsumer.accept(newValue.doubleValue());
                    }
                }
        );

        rangeSlider.highValueProperty().addListener(
                (_, _, newValue) -> {
                    maxLabel.setText(newValue.intValue() + "s");
                    if (maxValueChangeConsumer != null) {
                        maxValueChangeConsumer.accept(newValue.doubleValue());
                    }
                }
        );
    }
}
