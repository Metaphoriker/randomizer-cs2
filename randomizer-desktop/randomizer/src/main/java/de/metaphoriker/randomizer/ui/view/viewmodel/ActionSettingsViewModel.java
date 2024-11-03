package de.metaphoriker.randomizer.ui.view.viewmodel;

import de.metaphoriker.model.action.Action;
import de.metaphoriker.model.action.value.Interval;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import lombok.Getter;

public class ActionSettingsViewModel {

    @Getter
    private final ObjectProperty<Action> actionInFocusProperty = new SimpleObjectProperty<>();
    private final ChangeListener<Number> minIntervalListener =
            (_, _, newValue) -> updateIntervalProperty(newValue.intValue(), true);
    private final ChangeListener<Number> maxIntervalListener =
            (_, _, newValue) -> updateIntervalProperty(newValue.intValue(), false);
    @Getter
    private final IntegerProperty minIntervalProperty = new SimpleIntegerProperty();
    @Getter
    private final IntegerProperty maxIntervalProperty = new SimpleIntegerProperty();

    public ActionSettingsViewModel() {
        setupMinAndMaxIntervalListeners();
        setupActionInFocusListener();
    }

    private void setupMinAndMaxIntervalListeners() {
        minIntervalProperty.addListener(minIntervalListener);
        maxIntervalProperty.addListener(maxIntervalListener);
    }

    private void setupActionInFocusListener() {
        actionInFocusProperty.addListener(
                (_, _, newAction) -> {
                    if (newAction != null) {
                        minIntervalProperty.removeListener(minIntervalListener);
                        maxIntervalProperty.removeListener(maxIntervalListener);

                        minIntervalProperty.set(newAction.getInterval().getMin());
                        maxIntervalProperty.set(newAction.getInterval().getMax());

                        minIntervalProperty.addListener(minIntervalListener);
                        maxIntervalProperty.addListener(maxIntervalListener);
                    }
                });
    }

    private void updateIntervalProperty(int newValue, boolean isMin) {
        Action currentAction = actionInFocusProperty.get();
        if (currentAction != null) {
            Interval interval = currentAction.getInterval();
            if (isMin) {
                interval.setMin(newValue);
            } else {
                interval.setMax(newValue);
            }
        }
    }
}
