package dev.luzifer.gui.swing.game.model.impl;

import dev.luzifer.gui.swing.game.model.api.Aggressive;
import dev.luzifer.gui.swing.game.model.api.LivingEntity;
import dev.luzifer.gui.swing.game.model.api.objects.LivingGameObject;
import dev.luzifer.gui.swing.game.model.location.Location;

public class EnemyObject extends LivingGameObject implements Aggressive {

    private LivingEntity target;

    public EnemyObject(Location location, int maxHealth) {
        super(location, 50, 50, maxHealth);

        setText("Enemy");
    }

    @Override
    public void tick() {
        attackBehaviour();
    }

    @Override
    protected int movementSpeed() {
        return 2;
    }

    @Override
    public void attack(LivingEntity entity) {
        entity.damage(getDamage());
    }

    @Override
    public void setTarget(LivingEntity entity) {
        this.target = entity;
    }

    @Override
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public int getDamage() {
        return 5;
    }

    @Override
    public int getRange() {
        return 5;
    }

    private void attackBehaviour() {

        if(target != null) {

            Location targetLocation = target.getGameLocation();

            if(targetLocation.getX() > location.getX())
                moveRight();
            else if(targetLocation.getX() < location.getX())
                moveLeft();

            if(targetLocation.getY() > location.getY())
                moveDown();
            else if(targetLocation.getY() < location.getY())
                moveUp();

            if(getGameLocation().distance(targetLocation) < getRange())
                attack(target);
        }
    }
}
