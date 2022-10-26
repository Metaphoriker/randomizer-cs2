package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.AbstractStaticObject;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class FireObject extends AbstractStaticObject {

    private static final int speed = 1;
    
    public FireObject(Position position) {
        super(position, ThreadLocalRandom.current().nextInt(50, 100), ThreadLocalRandom.current().nextInt(20, 50));
    
        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
    
        setFill(ImageUtil.getImagePattern("images/fire_icon.gif"));
    }
    
    @Override
    public void update() {
        
        setWidth(getWidth() - speed);
        setHeight(getHeight() - speed);
        
        AtomicInteger addFires = new AtomicInteger();
        getPosition().getGameField().getEntities().stream()
                .filter(EnemyObject.class::isInstance)
                .forEach(entity -> {
                
                    EnemyObject livingEntity = (EnemyObject) entity;
                
                    if(livingEntity.getBoundsInParent().intersects(getBoundsInParent())) {
    
                        livingEntity.damage(3);
    
                        if(addFires.get() < 3 && ThreadLocalRandom.current().nextInt(100) <= 25)
                            addFires.getAndIncrement();
                    }
                });
        
        if(addFires.get() > 0) {
            for(int i = 0; i < addFires.get(); i++) {
                FireObject fireObject = new FireObject(getPosition());
                getPosition().getGameField().getEntities().add(fireObject);
            }
        }
        
        if(getWidth() <= 0 || getHeight() <= 0)
            damage(1);
    }
}
