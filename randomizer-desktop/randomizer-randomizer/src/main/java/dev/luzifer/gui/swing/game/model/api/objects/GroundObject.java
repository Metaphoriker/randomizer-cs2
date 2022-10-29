package dev.luzifer.gui.swing.game.model.api.objects;


import dev.luzifer.gui.swing.game.model.location.Location;

public abstract class GroundObject extends GameObject {

    protected GroundObject(Location location, int width, int height) {
        super(location, width, height);
    }

    @Override
    public void tick() {
        // Ground objects don't tick
    }
}
