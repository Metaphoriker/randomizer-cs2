package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;

import java.util.concurrent.ThreadLocalRandom;

public class ObstacleObject extends AbstractStaticObject {

    public ObstacleObject(Position position) {
        super(position);
        
        setWidth(ThreadLocalRandom.current().nextInt(50, 100));
        setHeight(ThreadLocalRandom.current().nextInt(20, 50));
        
        setFill(ImageUtil.getRawImagePattern("images/obstacle_icon.png"));
    }
}
