package dev.luzifer.gui.component;

import dev.luzifer.gui.util.CSSUtil;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.model.event.Event;
import dev.luzifer.model.event.cluster.EventCluster;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: This class should not know any model stuff since its UI
public class TitledClusterContainer extends TitledPane {
    
    private final VBox vBox = new VBox();
    private final EventCluster eventCluster;
    
    public TitledClusterContainer(String title, EventCluster eventCluster) {
        
        this.eventCluster = eventCluster;
    
        CSSUtil.applyBasicStyle(this);
        getStyleClass().add("container");
        
        setText(title);
        setContent(vBox);
        setGraphic(ImageUtil.getImageView("images/loading_gif.gif", ImageUtil.ImageResolution.MEDIUM));
        
        fill(eventCluster);
    }
    
    public List<Label> getEventLabels() {
        return vBox.getChildren().stream().filter(Label.class::isInstance).map(Label.class::cast).collect(Collectors.toList());
    }
    
    public void finish() {
        setGraphic(ImageUtil.getImageView("images/checkmark_icon.png", ImageUtil.ImageResolution.MEDIUM));
        setExpanded(false);
    }
    
    public void finish(Event event) {
        getEventLabels().forEach(node -> {
            if(node.getText().equals(event.name()))
                node.setGraphic(ImageUtil.getImageView("images/checkmark_icon.png", ImageUtil.ImageResolution.SMALL));
        });
    }
    
    public EventCluster getEventCluster() {
        return eventCluster;
    }
    
    private void fill(EventCluster eventCluster) {
        Arrays.stream(eventCluster.getEvents()).forEach(event -> {
            
            Label label = new Label(event.name());
            label.setGraphic(ImageUtil.getImageView("images/loading_gif.gif", ImageUtil.ImageResolution.SMALL));
            
            vBox.getChildren().add(label);
        });
    }
}
