package dev.luzifer.gui.swing.game.model.location;

import dev.luzifer.gui.swing.game.model.api.World;

public class Location {

    private final World world;

    private int x;
    private int y;

    public Location(World world, int x, int y) {

        this.world = world;

        this.x = x;
        this.y = y;
    }

    public double distance(Location other) {
        return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector toVector() {
        return new Vector(x, y);
    }

    public World getWorld() {
        return world;
    }

}
