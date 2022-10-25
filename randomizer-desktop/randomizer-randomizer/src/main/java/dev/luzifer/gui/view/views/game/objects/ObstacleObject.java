package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.views.game.Position;

public class ObstacleObject extends AbstractStaticObject {

    public ObstacleObject(Position position) {
        super(position);
        
        setFill(ImageUtil.getRawImagePattern("images/obstacle_icon.png"));
    }
}
