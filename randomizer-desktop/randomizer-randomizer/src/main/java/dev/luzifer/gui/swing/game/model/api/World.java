package dev.luzifer.gui.swing.game.model.api;

import java.util.List;

public interface World {

    void addEntity(Entity entity);

    void addEntityAtIndex(Entity entity, int index);

    /**
     * Returns the index of the entity in the list
     */
    int removeEntity(Entity entity);

    int getWidth();

    int getHeight();

    List<Entity> getEntities();
}
