package dev.luzifer.gui.view.views.game.objects.sup;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.AbstractStaticObject;

/**
 * More or less for identification purposes
 */
public abstract class ObstacleObject extends AbstractStaticObject {

    protected ObstacleObject(Position position, int width, int height) {
        super(position, width, height);
    }
}
