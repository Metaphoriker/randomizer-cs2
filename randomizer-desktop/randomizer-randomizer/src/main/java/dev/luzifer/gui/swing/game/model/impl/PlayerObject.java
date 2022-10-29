package dev.luzifer.gui.swing.game.model.impl;

import dev.luzifer.gui.swing.game.model.api.Player;
import dev.luzifer.gui.swing.game.model.api.objects.LivingGameObject;
import dev.luzifer.gui.swing.game.model.location.Facing;
import dev.luzifer.gui.swing.game.model.location.Location;
import dev.luzifer.gui.swing.game.model.util.ImageHelper;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class PlayerObject extends LivingGameObject implements Player {

    private final Set<Integer> pressedKeys = new HashSet<>();

    public PlayerObject(Location location) {
        super(location, 64, 64, 100);

        setIcon(ImageHelper.getIcon("/figure_up"));
    }

    @Override
    public void tick() {
        pressedKeys.forEach(key -> {
            switch (key) {
                case KeyEvent.VK_W:
                    moveUp();
                    break;
                case KeyEvent.VK_S:
                    moveDown();
                    break;
                case KeyEvent.VK_A:
                    moveLeft();
                    break;
                case KeyEvent.VK_D:
                    moveRight();
                    break;
            }
        });
    }

    @Override
    public void moveTowardsFacing(Facing facing) {
        getGameLocation().getWorld().getEntities().stream()
                .filter(entity -> entity != this)
                .forEach(entity -> entity.teleport(new Location(entity.getGameLocation().getWorld(),
                        entity.getGameLocation().getX() - (int) facing.getOffset().getX()*movementSpeed(),
                        entity.getGameLocation().getY() - (int) facing.getOffset().getY()*movementSpeed())));
    }

    @Override
    public void onKeyPressed(KeyEvent keyEvent) {
        pressedKeys.add(keyEvent.getKeyCode());

        Icon newIcon = null;
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_W:
                newIcon = ImageHelper.getIcon("/figure_up");
                break;
            case KeyEvent.VK_S:
                newIcon = ImageHelper.getIcon("/figure_down");
                break;
            case KeyEvent.VK_A:
                newIcon = ImageHelper.getIcon("/figure_left");
                break;
            case KeyEvent.VK_D:
                newIcon = ImageHelper.getIcon("/figure_right");
                break;
        }
        if(newIcon != null)
            updateIcon(newIcon);
    }

    @Override
    public void onKeyReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getKeyCode());
    }

    @Override
    protected int movementSpeed() {
        return 5;
    }
}
