package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import javafx.geometry.Point2D;

public class BulletProjectileObject extends ProjectileObject {
    
    public BulletProjectileObject(Position position, Point2D velocity) {
        super(position, velocity);
        setFill(ImageUtil.getImagePattern("images/bullet_icon.png"));
    }
    
    @Override
    public void update() {
        super.update();
    
        getPosition().getGameField().getEntities().stream()
                .filter(EnemyObject.class::isInstance)
                .forEach(entity -> {
                
                    EnemyObject livingEntity = (EnemyObject) entity;
                
                    if(livingEntity.getBoundsInParent().intersects(getBoundsInParent())) {
                        livingEntity.damage(5);
                        damage(1);
                    }
                });
    }
}
