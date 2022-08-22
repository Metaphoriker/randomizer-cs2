package dev.luzifer.ui.view;

import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public abstract class View<T extends ViewModel> extends Stage implements Initializable {
    private final T viewModel;
    
    protected View(T viewModel) {
        this.viewModel = viewModel;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    
    public T getViewModel() {
        return viewModel;
    }

    /**
     * This method does set an {@link ImagePattern} as fill of a {@link Shape}.
     * Means the background is being set.
     */
    protected void setFillImage(Shape shape, String imageName) {
        
        URL url = View.class.getClassLoader().getResource("images/logo.png");
        if (url == null)
            throw new IllegalStateException(MessageFormat.format("A resource with this name could not be found: {0}", imageName));
        
        Image image = getImageByURL(fetchResourceAsURL(imageName));
        shape.setFill(new ImagePattern(image));
    }
    
    /**
     * This method sets the {@link BackgroundImage} of a {@link Region}, means Buttons, Panes, Fields etc.
     */
    protected void setBackgroundImage(Region region, String imageName) {
        region.setBackground(new Background(convertPNGToBackgroundImage(imageName)));
    }
    
    private BackgroundImage convertPNGToBackgroundImage(String name) {
        
        Image image = getImageByURL(fetchResourceAsURL(name));
        
        return new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        );
    }
    
    private Image getImageByURL(URL url) {
        return new Image(url.toExternalForm());
    }
    
    private URL fetchResourceAsURL(String name) {
        
        URL url = View.class.getClassLoader().getResource(name);
        if (url == null)
            throw new IllegalStateException(MessageFormat.format("A resource with this name could not be found: {0}", name));
        
        return url;
    }
}
