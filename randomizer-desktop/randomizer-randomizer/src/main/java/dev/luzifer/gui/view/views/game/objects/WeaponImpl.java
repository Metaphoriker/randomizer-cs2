package dev.luzifer.gui.view.views.game.objects;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.inventory.Weapon;
import dev.luzifer.gui.view.views.game.objects.sub.BulletProjectileObject;
import javafx.geometry.Point2D;

public class WeaponImpl implements Weapon {
    
    private int ammo = 30;
    
    @Override
    public void shoot(Position start, Point2D velocity, double direction) {
        
        if(ammo <= 0)
            return;
        
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
