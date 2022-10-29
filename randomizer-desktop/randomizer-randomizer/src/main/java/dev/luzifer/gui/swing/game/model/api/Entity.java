package dev.luzifer.gui.swing.game.model.api;

import dev.luzifer.gui.swing.game.model.location.Location;

public interface Entity {

    void tick();

    void teleport(Location location);

    void damage(int amount);

    int getHealth();

    Location getGameLocation();

    default boolean isDead() {
        return getHealth() <= 0;
    }
}
