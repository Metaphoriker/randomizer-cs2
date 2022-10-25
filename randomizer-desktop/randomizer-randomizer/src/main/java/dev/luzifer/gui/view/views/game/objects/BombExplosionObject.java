package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;

public class BombExplosionObject extends AbstractStaticObject {

    private static final int STAY_TICKS = 100;
    
    private int ticksAlive = 0;

    public BombExplosionObject(Position position) {
        super(position);
        
        // TODO: Randomize?
        setWidth(50);
        setHeight(50);
        
        setFill(ImageUtil.getRawImagePattern("images/bomb_boom_icon.png"));
    }

    @Override
    public void update() {
        
        ticksAlive++;
        getPosition().getGameField().getEntities().stream()
                .filter(AbstractLivingGameObject.class::isInstance)
                .map(AbstractLivingGameObject.class::cast)
                .filter(entity -> entity.getBoundsInParent().intersects(getBoundsInParent()))
                .forEach(entity -> entity.damage(24));
        
        if(ticksAlive >= STAY_TICKS)
            damage(1);
    }

}
