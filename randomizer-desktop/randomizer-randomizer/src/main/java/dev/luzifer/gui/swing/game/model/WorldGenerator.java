package dev.luzifer.gui.swing.game.model;

import dev.luzifer.gui.swing.game.model.api.World;
import dev.luzifer.gui.swing.game.model.impl.MeadowObject;
import dev.luzifer.gui.swing.game.model.location.Location;

import java.util.concurrent.CompletableFuture;

public class WorldGenerator {

    private final World world;

    public WorldGenerator(World world) {
        this.world = world;
    }

    public CompletableFuture prepareSpawnArea() {
        return CompletableFuture.runAsync(() -> {
            for (int x = 0; x < world.getWidth(); x += 200) {
                for (int y = 0; y < world.getHeight(); y += 200) {
                    world.addEntity(new MeadowObject(new Location(world, x, y)));
                }
            }
        });
    }

    public void generateChunk(Location at) {
        CompletableFuture.runAsync(() -> {
            for (int x = at.getX(); x < at.getX() + 50; x += 50) {
                for (int y = at.getY(); y < at.getY() + 50; y += 50) {

                    int finalX = x;
                    int finalY = y;
                    if(world.getEntities().stream()
                            .anyMatch(entity -> entity.getGameLocation().getX() == finalX
                                    && entity.getGameLocation().getY() == finalY))
                        continue;

                    world.addEntity(new MeadowObject(new Location(world, x, y)));
                }
            }
        });
    }

}
