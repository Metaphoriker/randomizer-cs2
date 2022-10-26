package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.AbstractStaticObject;

import java.util.concurrent.ThreadLocalRandom;

public class FireObject extends AbstractStaticObject {

    private static final int speed = 1;
    
    public FireObject(Position position) {
        super(position, ThreadLocalRandom.current().nextInt(50, 100), ThreadLocalRandom.current().nextInt(20, 50));
    
        setTranslateX(position.getLocation().getX());
        setTranslateY(position.getLocation().getY());
        
        setOnInterfere(gameObject -> {
            if (gameObject instanceof EnemyObject) {
                EnemyObject enemy = (EnemyObject) gameObject;
                enemy.damage(3);
            }
        });
    
        setFill(ImageUtil.getImagePattern("images/fire_icon.gif"));
    }
    
    @Override
    public void update() {
        super.update();
        
        setWidth(getWidth() - speed);
        setHeight(getHeight() - speed);
        
        if(getWidth() <= 0 || getHeight() <= 0)
            damage(1);
    }
}
