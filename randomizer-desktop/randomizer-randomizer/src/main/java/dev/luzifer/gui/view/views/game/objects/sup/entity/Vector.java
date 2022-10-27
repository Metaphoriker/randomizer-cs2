package dev.luzifer.gui.view.views.game.objects.sup.entity;

public class Vector {

    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector add(Vector vector) {
        return new Vector(x + vector.getX(), y + vector.getY());
    }

    public Vector subtract(Vector vector) {
        return new Vector(x - vector.getX(), y - vector.getY());
    }

    public Vector multiply(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    public Vector divide(double scalar) {
        return new Vector(x / scalar, y / scalar);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector normalize() {
        return divide(length());
    }

    public double dot(Vector vector) {
        return x * vector.getX() + y * vector.getY();
    }

    public double cross(Vector vector) {
        return x * vector.getY() - y * vector.getX();
    }

    public float angle(Vector vector) {
        return (float) Math.toDegrees(Math.atan2(cross(vector), dot(vector)));
    }

    public double distance(Vector vector) {
        return Math.sqrt(Math.pow(vector.getX() - x, 2) + Math.pow(vector.getY() - y, 2));
    }

    public Vector rotate(double angle) {

        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        return new Vector(x * cos - y * sin, x * sin + y * cos);
    }

    public Vector rotate(Vector center, double angle) {
        return subtract(center).rotate(angle).add(center);
    }

    public Vector lerp(Vector vector, double t) {
        return vector.subtract(this).multiply(t).add(this);
    }

    public Vector clone() {
        return new Vector(x, y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
