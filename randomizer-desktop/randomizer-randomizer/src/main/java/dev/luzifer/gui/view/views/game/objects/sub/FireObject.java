package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.AbstractStaticObject;

public class FireObject extends AbstractStaticObject {

    private static final int speed = 1;
    
    public FireObject(Position position) {
        super(position);
        
        setWidth(200);
        setHeight(200);
    
        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
    
        setFill(ImageUtil.getImagePattern("images/fire_icon.gif"));
    }
    
    @Override
    public void update() {
        
         // Maybe make, that it spreads into more FireObjects?
        
        setWidth(getWidth() - speed);
        setHeight(getHeight() - speed);
        
        setTranslateX(getTranslateX() + speed / 2);
        setTranslateY(getTranslateY() + speed / 2);
        
        getPosition().getGameField().getEntities().stream()
                .filter(EnemyObject.class::isInstance)
                .forEach(entity -> {
                
                    EnemyObject livingEntity = (EnemyObject) entity;
                
                    if(livingEntity.getBoundsInParent().intersects(getBoundsInParent()))
                        livingEntity.damage(3);
                });
        
        if(getWidth() <= 0 || getHeight() <= 0)
            damage(1);
    }
}
