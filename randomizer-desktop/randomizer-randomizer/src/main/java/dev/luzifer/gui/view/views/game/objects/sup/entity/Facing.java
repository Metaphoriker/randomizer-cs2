package dev.luzifer.gui.view.views.game.objects.sup.entity;

public enum Facing {
  NORTH(0, new Vector(0, -1)),
  NORTH_EAST(45, new Vector(1, -1)),
  EAST(90, new Vector(1, 0)),
  SOUTH_EAST(135, new Vector(1, 1)),
  SOUTH(180, new Vector(0, 1)),
  SOUTH_WEST(225, new Vector(-1, 1)),
  WEST(270, new Vector(-1, 0)),
  NORTH_WEST(315, new Vector(-1, -1));

  private final int angle;
  private final Vector vector;

  Facing(int angle, Vector vector) {
    this.angle = angle;
    this.vector = vector;
  }

  public static Facing getClosest(double angle) {

    if (angle < 0) angle = 360 + angle;
    else if (angle > 360) angle = angle - 360;

    for (int i = 0; i < values().length; i++) {

      Facing facing = values()[i];

      int currentDiff = facing.getAngle() + facing.getAngle() / 2;

      int currentFacingReachStart = facing.getAngle() - currentDiff;
      int currentFacingReachEnd = facing.getAngle() + currentDiff;

      if (angle >= currentFacingReachStart && angle <= currentFacingReachEnd) return facing;
    }

    return NORTH;
  }

  public static Facing getDirection(Vector from, Vector to) {
    return getClosest(from.angle(to));
  }

  public static Facing invert(Facing facing) {
    return getClosest(facing.getAngle() + 180);
  }

  public int getAngle() {
    return angle;
  }

  public Vector getVector() {
    return vector;
  }

  @Override
  public String toString() {
    return "Facing{" + "angle=" + angle + ", vector=" + vector + '}';
  }
}
