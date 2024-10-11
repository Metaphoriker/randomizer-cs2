package de.metaphoriker.view.game.objects.sup.inventory;

import de.metaphoriker.view.game.Position;
import javafx.geometry.Point2D;

public interface Weapon {

  void shoot(
          Position start, Point2D velocity, double direction); // TODO: We don't want "start" here.

  void reload();

  int getAmmo();
}
