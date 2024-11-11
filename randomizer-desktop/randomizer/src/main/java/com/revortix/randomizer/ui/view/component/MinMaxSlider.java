package com.revortix.randomizer.ui.view.component;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.HBox;
import org.controlsfx.control.RangeSlider;

import java.util.function.Consumer;

public class MinMaxSlider extends HBox {

    private final RangeSlider rangeSlider = new RangeSlider(0, 300, 30, 100); // max 5 minutes

    private Consumer<Double> minValueChangeConsumer;
    private Consumer<Double> maxValueChangeConsumer;

    public MinMaxSlider() {
        Platform.runLater(this::initialize);
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

    public void setMinMaxValue(double min, double max) {
        rangeSlider.setLowValue(min);
        rangeSlider.setHighValue(max);
    }

    private void initialize() {
        getChildren().addAll(rangeSlider);

        rangeSlider.setShowTickMarks(true);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setBlockIncrement(5);
        setListener();
    }

    private void setListener() {
        rangeSlider.lowValueProperty().addListener(
                (_, _, newValue) -> {
                    if (minValueChangeConsumer != null) {
                        minValueChangeConsumer.accept(newValue.doubleValue());
                    }
                }
        );

        rangeSlider.highValueProperty().addListener(
                (_, _, newValue) -> {
                    if (maxValueChangeConsumer != null) {
                        maxValueChangeConsumer.accept(newValue.doubleValue());
                    }
                }
        );
    }
}
