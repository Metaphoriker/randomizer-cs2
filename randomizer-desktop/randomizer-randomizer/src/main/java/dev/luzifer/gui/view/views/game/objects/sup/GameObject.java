package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Facing;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Vector;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GameObject extends Rectangle implements Entity {
    
    protected final Position position;
    
    protected GameObject(Position position, double width, double height) {
        super(width, height);
        
        this.position = position;

        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
    }

    @SafeVarargs
    public final List<Entity> rayTrace(Facing direction, double distance, Class<? extends Entity>... filter) {

        Vector vector = direction.getVector().multiply(distance);
        Point2D ray = new Point2D(vector.getX(), vector.getY());

        return position.getGameField().getEntities().stream()
                .filter(entity -> entity != this)
                .filter(GameObject.class::isInstance)
                .filter(entity -> {

                    if (filter.length == 0)
                        return true;

                    for (Class<? extends Entity> aClass : filter)
                        if (aClass.isInstance(entity))
                            return true;

                    return false;
                })
                .filter(entity -> entity.getHitBox().intersects(getHitBox().getMinX() + ray.getX(), getHitBox().getMinY() + ray.getY(), getHitBox().getWidth(), getHitBox().getHeight()))
                .collect(Collectors.toList());
    }

    @Override
    public final Facing getFacing() {
        return Facing.getClosest(getRotate());
    }

    @Override
    public final Rectangle2D getHitBox() {
        return new Rectangle2D(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    @Override
    public Position getPosition() {
        return new Position(position.getGameField(), new Point2D(getTranslateX(), getTranslateY()));
    }
}
