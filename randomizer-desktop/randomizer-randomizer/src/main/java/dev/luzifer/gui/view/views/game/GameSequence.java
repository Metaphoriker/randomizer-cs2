package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.view.views.game.objects.sub.BombObject;
import dev.luzifer.gui.view.views.game.objects.sub.EnemyObject;
import dev.luzifer.gui.view.views.game.objects.sup.ItemObject;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class GameSequence extends Thread {

  public static final Stack<Runnable> RUN_SHIT = new Stack<>(); // don't do this kids

  private final GameField gameField;

  private long lastUpdated = System.currentTimeMillis();

  public GameSequence(GameField gameField) {
    this.gameField = gameField;
  }

  @Override
  public void run() {
    while (!isInterrupted()) {

      long now = System.currentTimeMillis();
      long delta = now - lastUpdated;

      if (delta <= 5) {

        /*
         * hehe, im so dirty.
         * But actually this shit reduces some lags since we use the 15ms we would just waste anyways
         *
         * NOTE: This doesnt always work, since we just send the runnable to the UI thread
         * and it will be executed when it can. So if it needs more time to execute than 15ms,
         * it will catch up and lag anyways
         */
        if (!RUN_SHIT.isEmpty()) {

          Runnable toRun = RUN_SHIT.pop();
          Platform.runLater(toRun);

          continue;
        }
      }

      if (delta <= 15) {

        try {
          Thread.sleep(15 - delta);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        continue;
      }

      lastUpdated = now;

      Platform.runLater(
          () -> {
            if (ThreadLocalRandom.current().nextDouble(100) <= 1.5) {

              EnemyObject enemy =
                  new EnemyObject(
                      new Position(
                          gameField,
                          new Point2D(
                              ThreadLocalRandom.current()
                                  .nextDouble(0, gameField.getBorder().getWidth()),
                              ThreadLocalRandom.current()
                                  .nextDouble(0, gameField.getBorder().getHeight()))));

              enemy.setTarget((LivingEntity) gameField.getEntities().get(1)); // Player

              gameField.getEntities().add(enemy);
            }

            if (ThreadLocalRandom.current().nextDouble(100) <= 0.025) {

              EnemyObject enemy =
                  new EnemyObject(
                      new Position(
                          gameField,
                          new Point2D(
                              ThreadLocalRandom.current()
                                  .nextDouble(0, gameField.getBorder().getWidth()),
                              ThreadLocalRandom.current()
                                  .nextDouble(0, gameField.getBorder().getHeight()))));

              enemy.setWidth(200);
              enemy.setHeight(200);
              enemy.setHealth(420);
              enemy.setRange(100);

              enemy.setTarget((LivingEntity) gameField.getEntities().get(1)); // Player

              gameField.getEntities().add(enemy);
            }

            if (ThreadLocalRandom.current().nextDouble(100) <= 0.25)
              gameField
                  .getEntities()
                  .add(
                      new BombObject(
                          new Position(
                              gameField,
                              new Point2D(
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getWidth()),
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getHeight())))));

            if (ThreadLocalRandom.current().nextDouble(100) <= 0.1)
              gameField
                  .getEntities()
                  .add(
                      new ItemObject(
                          new Position(
                              gameField,
                              new Point2D(
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getWidth()),
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getHeight()))),
                          ItemObject.ItemType.AMMO,
                          32,
                          32));

            if (ThreadLocalRandom.current().nextDouble(100) <= 0.25)
              gameField
                  .getEntities()
                  .add(
                      new ItemObject(
                          new Position(
                              gameField,
                              new Point2D(
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getWidth()),
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getHeight()))),
                          ItemObject.ItemType.MOLOTOV,
                          32,
                          32));

            if (ThreadLocalRandom.current().nextDouble(100) <= 0.1)
              gameField
                  .getEntities()
                  .add(
                      new ItemObject(
                          new Position(
                              gameField,
                              new Point2D(
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getWidth()),
                                  ThreadLocalRandom.current()
                                      .nextDouble(0, gameField.getBorder().getHeight()))),
                          ItemObject.ItemType.WEAPON,
                          32,
                          32));

            for (int i = 0; i < gameField.getEntities().size(); i++) {

              Entity entity = gameField.getEntities().get(i);
              entity.update();

              if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.isDead()) {
                  if (entity instanceof Player) gameField.gameOver();
                  else gameField.getEntities().remove(i);
                }
              }
            }
          });
    }
  }
}
