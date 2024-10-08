package de.metaphoriker.gui.view.views.game.objects;

import de.metaphoriker.gui.view.views.game.Position;
import de.metaphoriker.gui.view.views.game.objects.sub.BulletProjectileObject;
import de.metaphoriker.gui.view.views.game.objects.sup.inventory.Weapon;
import javafx.geometry.Point2D;

public class WeaponImpl implements Weapon {

  private int ammo = 30;

  @Override
  public void shoot(Position start, Point2D velocity, double direction) {

    if (ammo <= 0) return;

    BulletProjectileObject projectile = new BulletProjectileObject(start, velocity);

    start.getGameField().getEntities().add(projectile);

    ammo--;
  }

  @Override
  public void reload() {
    ammo = 30;
  }

  @Override
  public int getAmmo() {
    return ammo;
  }
}
