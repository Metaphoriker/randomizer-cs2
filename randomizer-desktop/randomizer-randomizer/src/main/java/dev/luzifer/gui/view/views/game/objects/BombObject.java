package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;

public class BombObject extends AbstractStaticObject {
    
    private static final int TICKS = 100;
    
    private int ticksAlive = 0;
    
    public BombObject(Position position) {
        super(position);
        
        setFill(ImageUtil.getRawImagePattern("images/bomb_icon.png"));
    }
    
    @Override
    public void update() {
    
        ticksAlive++;
    
        if(ticksAlive >= TICKS) {
            damage(1);
            getPosition().getGameField().getEntities().add(new BombExplosionObject(getPosition()));
        }
    }
    
}
