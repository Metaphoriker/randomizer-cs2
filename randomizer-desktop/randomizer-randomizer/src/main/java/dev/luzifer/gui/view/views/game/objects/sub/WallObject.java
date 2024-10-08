package dev.luzifer.gui.view.views.game.objects.sub;

import dev.luzifer.gui.view.views.game.Position;
import dev.luzifer.gui.view.views.game.objects.sup.ObstacleObject;
import java.util.concurrent.ThreadLocalRandom;

public class WallObject extends ObstacleObject {

  public WallObject(Position position, WallType wallType) {
    super(
        position,
        ThreadLocalRandom.current().nextInt(10, 51) * wallType.getWidth(),
        ThreadLocalRandom.current().nextInt(10, 51) * wallType.getHeight());
  }

  public enum WallType {
    VERTICAL(1, 5),
    HORIZONTAL(5, 1);

    private final int width;
    private final int height;

    WallType(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }
  }
}
