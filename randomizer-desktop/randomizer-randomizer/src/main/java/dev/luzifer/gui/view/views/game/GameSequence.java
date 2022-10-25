package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.view.views.game.objects.BombObject;
import dev.luzifer.gui.view.views.game.objects.EnemyObject;
import dev.luzifer.gui.view.views.game.objects.ItemObject;
import dev.luzifer.gui.view.views.game.objects.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.entity.Player;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

import java.util.concurrent.ThreadLocalRandom;

public class GameSequence extends AnimationTimer {
    
    private final GameField gameField;
    
    public GameSequence(GameField gameField) {
        this.gameField = gameField;
    }
    
    @Override
    public void handle(long now) {
    
        if(ThreadLocalRandom.current().nextDouble(100) <= 1.5) {
            EnemyObject enemy = new EnemyObject(2, new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight()))));
            enemy.setTarget((LivingEntity) gameField.getEntities().get(1)); // Player
            gameField.getEntities().add(enemy);
        }
    
        if(ThreadLocalRandom.current().nextDouble(100) <= 0.25)
            gameField.getEntities().add(new BombObject(new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight())))));
    
        if(ThreadLocalRandom.current().nextDouble(100) <= 0.1)
            gameField.getEntities().add(new ItemObject(new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight()))), ItemObject.ItemType.AMMO));
    
        if(ThreadLocalRandom.current().nextDouble(100) <= 0.5)
            gameField.getEntities().add(new ItemObject(new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight()))), ItemObject.ItemType.MOLOTOV));
    
        if(ThreadLocalRandom.current().nextDouble(100) <= 0.1)
            gameField.getEntities().add(new ItemObject(new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight()))), ItemObject.ItemType.WEAPON));
        
        // TODO: Obstacle + Movement & Projectile block by them
        
        for(int i = 0; i < gameField.getEntities().size(); i++) {
            
            Entity entity = gameField.getEntities().get(i);
            entity.update();
            
            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if(livingEntity.isDead()) {
                    if(entity instanceof Player)
                        gameField.gameOver();
                    else
                        gameField.getEntities().remove(i);
                }
            }
        }
    }
}
