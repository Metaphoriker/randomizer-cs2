package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public abstract class GameObject extends Rectangle implements Entity {
    
    protected final Position position;
    
    private Consumer<GameObject> onInterfere;
    
    protected GameObject(Position position, double width, double height) {
        super(width, height);
        
        this.position = position;
        
        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
    }
    
    public final void setOnInterfere(Consumer<GameObject> onInterfere) {
        this.onInterfere = onInterfere;
    }
    
    /**
     * For standard interfere behaviour,
     *      always call the super update method
     */
    @Override
    public void update() {
        
        if (onInterfere != null) {
            getPosition().getGameField().getEntities().stream()
                    .filter(entity -> entity != this)
                    .filter(GameObject.class::isInstance)
                    .map(GameObject.class::cast)
                    .filter(entity -> entity.getBoundsInParent().intersects(getBoundsInParent()))
                    .forEach(gameObject -> onInterfere.accept(gameObject));
        }
    }
    
    @Override
    public Position getPosition() {
        return new Position(position.getGameField(), new Point2D(getTranslateX(), getTranslateY()));
    }
}
