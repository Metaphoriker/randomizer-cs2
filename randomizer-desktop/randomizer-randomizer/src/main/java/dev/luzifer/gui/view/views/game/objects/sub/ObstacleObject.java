package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.AbstractStaticObject;

import java.util.concurrent.ThreadLocalRandom;

public class ObstacleObject extends AbstractStaticObject {

    public ObstacleObject(Position position) {
        super(position, ThreadLocalRandom.current().nextInt(50, 100), ThreadLocalRandom.current().nextInt(20, 50));
        
        setFill(ImageUtil.getRawImagePattern("images/game/obstacle_icon.png"));
    }
}
