package dev.luzifer.gui.view.views.game.objects.sup.entity;

public enum Facing {

    NORTH(0),
    EAST(90),
    SOUTH(180),
    WEST(270),
    NORTH_EAST(45),
    NORTH_WEST(315),
    SOUTH_EAST(135),
    SOUTH_WEST(225);

    public static Facing getByAngle(double angle) {

        if(angle < 0) angle += 360;
        if(angle > 360) angle -= 360;

        if(angle >= 0 && angle < 22.5) return NORTH;
        if(angle >= 22.5 && angle < 67.5) return NORTH_EAST;
        if(angle >= 67.5 && angle < 112.5) return EAST;
        if(angle >= 112.5 && angle < 157.5) return SOUTH_EAST;
        if(angle >= 157.5 && angle < 202.5) return SOUTH;
        if(angle >= 202.5 && angle < 247.5) return SOUTH_WEST;
        if(angle >= 247.5 && angle < 292.5) return WEST;
        if(angle >= 292.5 && angle < 337.5) return NORTH_WEST;
        if(angle >= 337.5 && angle < 360) return NORTH;

        return NORTH;
    }

    private final int angle;

    Facing(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

}
