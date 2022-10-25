package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import javafx.geometry.Point2D;

import java.util.concurrent.atomic.AtomicBoolean;

public class MolotovProjectileObject extends ProjectileObject {
    
    public MolotovProjectileObject(Position position, Point2D velocity) {
        super(position, velocity);
        setFill(ImageUtil.getRawImagePattern("images/molotov_icon.png"));
    }
    
    @Override
    public void update() {
        super.update();
        
        AtomicBoolean hit = new AtomicBoolean(false);
        getPosition().getGameField().getEntities().stream()
                .filter(EnemyObject.class::isInstance)
                .forEach(entity -> {
                
                    EnemyObject livingEntity = (EnemyObject) entity;
                
                    if(livingEntity.getBoundsInParent().intersects(getBoundsInParent())) {
                        
                        livingEntity.damage(2);
                        damage(1);
                        
                        hit.set(true);
                        return;
                    }
                });
    
        if(!hit.get())
            return;
        
        FireObject fireObject = new FireObject(getPosition());
        getPosition().getGameField().getEntities().add(fireObject);
    }
}
