package dev.luzifer.gui.swing.game;

import dev.luzifer.gui.swing.game.model.api.Entity;
import dev.luzifer.gui.swing.game.model.api.World;
import dev.luzifer.gui.swing.game.model.impl.TreeObject;
import dev.luzifer.gui.swing.game.model.location.Location;

import java.util.concurrent.ThreadLocalRandom;

public class GameSequence implements Runnable{

    private final GameWindow gameWindow;
    private final World gameWorld;

    public GameSequence(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.gameWorld = gameWindow.getGameWorld();
    }

    @Override
    public void run() {

        while (true) {

            if(ThreadLocalRandom.current().nextInt(100) <= 2) {

                Location location = new Location(gameWorld,
                        ThreadLocalRandom.current().nextInt(0, gameWorld.getWidth()),
                        ThreadLocalRandom.current().nextInt(0, gameWorld.getHeight()));

                if(gameWorld.getEntities().stream()
                        .filter(TreeObject.class::isInstance)
                        .noneMatch(entity -> entity.getGameLocation().distance(location) <= 50)) {

                    TreeObject treeObject = new TreeObject(location);
                    gameWorld.addEntity(treeObject);
                }

            }

            gameWorld.getEntities().forEach(Entity::tick);
            gameWindow.repaint();

            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
