package dev.luzifer.gui.view.views.game.objects.inventory;

import dev.luzifer.gui.view.views.game.Position;
import javafx.geometry.Point2D;

public interface Weapon {
    
    void shoot(Position start, Point2D velocity, double direction); // TODO: We don't want "start" here.
    
    void reload();
    
    int getAmmo();
    
}
