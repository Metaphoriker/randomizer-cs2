package dev.luzifer.ui.view.component.components;

import dev.luzifer.ui.util.Styling;
import dev.luzifer.ui.view.component.Component;
import dev.luzifer.ui.view.component.models.EventComponentModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class EventComponent extends Component<EventComponentModel> {
    
    @FXML
    protected Label nameLabel;
    
    @FXML
    protected Label descriptionLabel;
    
    public EventComponent() {
        super(new EventComponentModel());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStyling();
        setupBindings();
    }
    
    private void setupStyling() {
        nameLabel.setStyle(Styling.HEADER);
        descriptionLabel.setStyle(Styling.CONTENT);
    }
    
    private void setupBindings() {
        getModel().getNameProperty().bindBidirectional(nameLabel.textProperty());
        getModel().getDescriptionProperty().bindBidirectional(descriptionLabel.textProperty());
    }
}
