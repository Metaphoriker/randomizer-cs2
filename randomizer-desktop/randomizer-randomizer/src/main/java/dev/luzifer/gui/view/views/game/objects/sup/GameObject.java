package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public abstract class GameObject extends Rectangle implements Entity {
    
    protected final Position position;
    
    protected GameObject(Position position, double width, double height) {
        super(width, height);
        
        this.position = position;
        
        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
    }
    
    @Override
    public Position getPosition() {
        return new Position(position.getGameField(), new Point2D(getTranslateX(), getTranslateY()));
    }
}
