package dev.luzifer.gui.swing.game.model;

import dev.luzifer.gui.swing.game.GameWindow;
import dev.luzifer.gui.swing.game.model.api.Entity;
import dev.luzifer.gui.swing.game.model.api.World;
import dev.luzifer.gui.swing.game.model.api.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

// TODO: Distribute world into chunks
// NOTE: A chunk could be the size of the window size (800x600)
public class GameWorld implements World {

    // TODO: This belongs into the World Generator
    private static final int WIDTH = 1980;
    private static final int HEIGHT = 1200;

    private final List<Entity> entities = new ArrayList<>();

    private final GameWindow gameWindow; // This is really dirty

    /*
     * TODO:
     *  - Add a "first location" Location, which will be the spawn location (WIDTH/2, HEIGHT/2)
     *      - The first location will be the center of the world
     *  - Every other location will be relative to the first location so that the world is centered
     *      - And locations will be calculated by the first location and are up to date
     *
     * So for example: If the first location is (400, 300) and the second location is (500, 300)
     *     - The second location will be (100, 0) relative to the first location
     *
     * Moving would be adding/subtracting the offset to/from the first location
     * 
     * TODO: Chunks
     *  - Locate their center in the world by nearby already loaded chunks
     *      - If there are no chunks loaded, the center will be the first location
     *      - So a nearby chunk would be calculated by: (loaded chunk center + loaded chunk center)
     *          or - since it could be in any direction á la Facing
     */
    public GameWorld(GameWindow gameWindow) { // TODO: A world cant be instantiated without the WorldGenerator
        this.gameWindow = gameWindow;
    }

    @Override
    public void addEntity(Entity entity) {
        addEntityAtIndex(entity, 0);
    }

    @Override
    public void addEntityAtIndex(Entity entity, int index) {

        synchronized (entities) {
            entities.add(index, entity);
        }

        if(entity instanceof GameObject)
            gameWindow.getContentPane().add((GameObject) entity, index);
    }

    @Override
    public int removeEntity(Entity entity) {

        synchronized (entities) {
            entities.remove(entity);
        }

        int index = gameWindow.getContentPane().getComponentZOrder((GameObject) entity);
        if(entity instanceof GameObject)
            gameWindow.getContentPane().remove((GameObject) entity);

        return index;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }
}
