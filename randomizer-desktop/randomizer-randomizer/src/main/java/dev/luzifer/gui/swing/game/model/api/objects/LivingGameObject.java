package dev.luzifer.gui.swing.game.model.api.objects;

import dev.luzifer.gui.swing.game.model.api.LivingEntity;
import dev.luzifer.gui.swing.game.model.api.Moveable;
import dev.luzifer.gui.swing.game.model.location.Facing;
import dev.luzifer.gui.swing.game.model.location.Location;

public abstract class LivingGameObject extends GameObject implements LivingEntity, Moveable {

    protected final int maxHealth;

    protected LivingGameObject(Location location,  int width, int height, int maxHealth) {
        super(location, width, height);
        this.maxHealth = maxHealth;
    }

    @Override
    public void moveUp() {
        moveTowardsFacing(Facing.UP);
    }

    @Override
    public void moveDown() {
        moveTowardsFacing(Facing.DOWN);
    }

    @Override
    public void moveRight() {
        moveTowardsFacing(Facing.RIGHT);
    }

    @Override
    public void moveLeft() {
        moveTowardsFacing(Facing.LEFT);
    }

    @Override
    public void moveTowardsFacing(Facing facing) {
        teleport(new Location(location.getWorld(),
                location.getX() + (int) facing.getOffset().getX()*movementSpeed(),
                location.getY() + (int) facing.getOffset().getY()*movementSpeed()));
    }

    @Override
    public void moveAwayFromFacing(Facing facing) {
        teleport(new Location(location.getWorld(),
                location.getX() - (int) facing.getOffset().getX()*movementSpeed(),
                location.getY() - (int) facing.getOffset().getY()*movementSpeed()));
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    protected abstract int movementSpeed();
}
