package dev.luzifer.gui.swing.game.model.api.objects;


import dev.luzifer.gui.swing.game.model.api.Entity;
import dev.luzifer.gui.swing.game.model.location.Location;

import javax.swing.*;

public abstract class GameObject extends JLabel implements Entity {

    protected Location location;
    protected int health;

    protected GameObject(Location location, int width, int height) {
        this.location = location;
        this.setBounds(location.getX(), location.getY(), width, height);
    }

    /*
     * We have to do this, since otherwise the repainting would be done on the wrong thread
     */
    public void updateIcon(Icon icon) {
        int index = location.getWorld().removeEntity(this);
        this.setIcon(icon);
        location.getWorld().addEntityAtIndex(this, index);
    }

    @Override
    public void teleport(Location location) {
        this.location = location;
        setLocation(location.getX(), location.getY());
    }

    @Override
    public void damage(int amount) {
        this.health -= amount;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public Location getGameLocation() {
        return location;
    }

}
