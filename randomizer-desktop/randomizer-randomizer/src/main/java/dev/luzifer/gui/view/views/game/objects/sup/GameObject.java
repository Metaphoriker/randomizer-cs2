package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public abstract class GameObject extends Rectangle {
    
    protected final Position position;
    
    protected GameObject(Position position, double width, double height) {
        super(position.getLocation().getX(), position.getLocation().getY(), width, height);
        
        this.position = position;
        
        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
    }
    
    public Position getPosition() {
        return new Position(position.getGameField(), new Point2D(getTranslateX(), getTranslateY()));
    }
}
