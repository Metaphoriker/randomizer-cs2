package dev.luzifer.gui.view.views.game;

import dev.luzifer.gui.view.views.game.objects.sub.BombObject;
import dev.luzifer.gui.view.views.game.objects.sub.EnemyObject;
import dev.luzifer.gui.view.views.game.objects.sup.ItemObject;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Entity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.LivingEntity;
import dev.luzifer.gui.view.views.game.objects.sup.entity.Player;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;

import java.util.concurrent.ThreadLocalRandom;

public class GameSequence extends AnimationTimer {
    
    private final IntegerProperty fpsProperty = new SimpleIntegerProperty();
    
    private final GameField gameField;
    
    private long lastUpdate;
    
    public GameSequence(GameField gameField) {
        this.gameField = gameField;
    }
    
    @Override
    public void handle(long now) {
    
        // Limit the game to 60 FPS
        if(now - lastUpdate < 16_666_666/2)
            return;
    
        fpsProperty.set((int) (1_000_000_000.0 / (now - lastUpdate)));
        lastUpdate = now;
        
        if(ThreadLocalRandom.current().nextDouble(100) <= 1.5) {
            
            EnemyObject enemy = new EnemyObject(new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight()))));
            
            enemy.setTarget((LivingEntity) gameField.getEntities().get(1)); // Player
            
            gameField.getEntities().add(enemy);
        }
        
        if(ThreadLocalRandom.current().nextDouble(100) <= 0.025) {
            
            EnemyObject enemy = new EnemyObject(new Position(gameField, new Point2D(
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getWidth()),
                    ThreadLocalRandom.current().nextDouble(0, gameField.getBorder().getHeight()))));
            
            enemy.setWidth(200);
            enemy.setHeight(200);
            enemy.setHealth(420);
            enemy.setRange(100);
            
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
    
    public ReadOnlyIntegerProperty getFpsProperty() {
        return fpsProperty; // TODO: ReadOnlyIntegerWrapper
    }
}
