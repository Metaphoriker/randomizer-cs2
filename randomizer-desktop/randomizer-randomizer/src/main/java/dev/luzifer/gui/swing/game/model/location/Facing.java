package dev.luzifer.gui.swing.game.model.location;

public enum Facing {

    UP(new Vector(0, -1)),
    DOWN(new Vector(0, 1)),
    LEFT(new Vector(-1, 0)),
    RIGHT(new Vector(1, 0)),
    TOP_LEFT(new Vector(-1, -1)),
    TOP_RIGHT(new Vector(1, -1)),
    BOTTOM_LEFT(new Vector(-1, 1)),
    BOTTOM_RIGHT(new Vector(1, 1));

    private final Vector vector;

    Facing(Vector vector) {
        this.vector = vector;
    }

    public Facing getOpposite() {
        return Facing.values()[(this.ordinal() + 4) % 8];
    }

    public Vector getOffset() {
        return vector;
    }
}
