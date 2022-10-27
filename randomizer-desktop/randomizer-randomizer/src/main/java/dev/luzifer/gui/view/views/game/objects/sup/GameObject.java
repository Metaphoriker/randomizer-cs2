package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Facing;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

import java.util.Collections;
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

    public final List<Entity> rayTrace(Facing direction, double distance, Class<? extends Entity>... filter) {

        Point2D ray = new Point2D(0, 0);

        switch (direction) {
            case NORTH:
                ray = new Point2D(0, -distance);
                break;
            case SOUTH:
                ray = new Point2D(0, distance);
                break;
            case EAST:
                ray = new Point2D(distance, 0);
                break;
            case WEST:
                ray = new Point2D(-distance, 0);
                break;
            case NORTH_EAST:
                ray = new Point2D(distance, -distance);
                break;
            case NORTH_WEST:
                ray = new Point2D(-distance, -distance);
                break;
            case SOUTH_EAST:
                ray = new Point2D(distance, distance);
                break;
            case SOUTH_WEST:
                ray = new Point2D(-distance, distance);
                break;
        }

        return rayTrace(ray, filter);
    }

    @Override
    public final Facing getFacing() {
        return Facing.getByAngle(getRotate());
    }

    @Override
    public final Rectangle2D getHitBox() {
        return new Rectangle2D(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    @Override
    public Position getPosition() {
        return new Position(position.getGameField(), new Point2D(getTranslateX(), getTranslateY()));
    }

    private List<Entity> rayTrace(Point2D ray, Class<? extends Entity>... filter) {
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
}
