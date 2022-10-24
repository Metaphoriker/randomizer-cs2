package dev.luzifer.gui.view.views;

import dev.luzifer.Main;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.GameViewModel;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class GameView extends View<GameViewModel> {
    
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    
    private int score = 0;
    
    @FXML
    private Pane gameField;
    
    public GameView(GameViewModel viewModel) {
        super(viewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: red; -fx-font-weight: bold;");
        gameOverLabel.setVisible(false);
        gameOverLabel.setTranslateX(300);
        gameOverLabel.setTranslateY(300);
        gameField.getChildren().add(gameOverLabel);
        
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-font-weight: bold;");
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setVisible(true);
        gameField.getChildren().add(scoreLabel);
        
        Rectangle rectangle = new Rectangle(0, 0, 600, 600);
        rectangle.setFill(null);
        rectangle.setStroke(new Color(0.5, 0.5, 0.5, 1));
        
        gameField.getChildren().add(rectangle);
        
        Player player = new Player();
        Platform.runLater(() -> getScene().setOnKeyPressed(player::onMove));
        Platform.runLater(() -> getScene().setOnMouseClicked(player::onShoot));
    
        update(player);
    
        gameField.getChildren().add(player);
    }
    
    private AnimationTimer update(Player player) {
        
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                
                enemies.forEach(enemy -> {
                    
                    if (enemy.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        
                        player.setDead(true);
                        player.setRotate(0);
                        
                        gameField.getChildren().get(0).setVisible(true);
                        Main.getScheduler().schedule(() -> Platform.runLater(() -> close()), 2000);
                        
                        return;
                    }
                    
                    enemy.followPlayer();
                });
    
                for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
                    
                    Bullet bullet = it.next();
                    
                    bullet.update();
    
                    for(Iterator<Enemy> it2 = enemies.iterator(); it2.hasNext(); ) {
                        
                        Enemy enemy = it2.next();
                        
                        if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            
                            it2.remove();
                            gameField.getChildren().remove(enemy);
                            
                            it.remove();
                            gameField.getChildren().remove(bullet);
                            
                            score+=10;
                            ((Label) gameField.getChildren().get(1)).setText("Score: " + score);
                            
                            break;
                        }
                    }
                }
    
                if(ThreadLocalRandom.current().nextInt(100) <= 1)
                    gameField.getChildren().add(new Enemy(player));
                
                if(ThreadLocalRandom.current().nextDouble(100) <= 0.5)
                    gameField.getChildren().add(new Bomb());
            }
        };
        animationTimer.start();
        
        return animationTimer;
    }
    
    private class Enemy extends Rectangle {
    
        private final Player player;
        
        public Enemy(Player toChase) {
            super(100, 30);
            
            this.player = toChase;
            
            setTranslateX(150);
            setTranslateY(150);
            
            setFill(ImageUtil.getImagePattern("images/enemy_icon.png", ImageUtil.ImageResolution.ORIGINAL));
            
            enemies.add(this);
        }
        
        public void followPlayer() {
    
            if(player.isDead())
                return;
            
            Point2D.Double playerPosition = new Point2D.Double(player.getTranslateX(), player.getTranslateY());
            Point2D.Double enemyPosition = new Point2D.Double(getTranslateX(), getTranslateY());
    
            double distance = playerPosition.distance(enemyPosition);
    
            double x = (playerPosition.x - enemyPosition.x) / distance;
            double y = (playerPosition.y - enemyPosition.y) / distance;
    
            setTranslateX(getTranslateX() + x);
            setTranslateY(getTranslateY() + y);
        }
        
        public Point2D getLocation() {
            return new Point2D.Double(getX(), getY());
        }
    }
    
    private class Bomb extends Circle {
        
        public Bomb() {
            super(16);
            
            setFill(ImageUtil.getImagePattern("images/bomb_icon.png", ImageUtil.ImageResolution.ORIGINAL));
            
            setTranslateX(ThreadLocalRandom.current().nextInt(600));
            setTranslateY(ThreadLocalRandom.current().nextInt(600));
            
            Main.getScheduler().schedule(() -> Platform.runLater(() -> {
                gameField.getChildren().remove(this);
                gameField.getChildren().add(new BombExplosion(new Point2D.Double(getTranslateX(), getTranslateY())));
            }), 3000);
        }
    }
    
    private class BombExplosion extends Circle {
        
        public BombExplosion(Point2D location) {
            super(ThreadLocalRandom.current().nextInt(10, 101));
            
            setFill(ImageUtil.getImagePattern("images/bomb_boom_icon.png", ImageUtil.ImageResolution.ORIGINAL));
            
            setTranslateX(location.getX());
            setTranslateY(location.getY());
            
            if(gameField.getChildren().get(3).getBoundsInParent().intersects(getBoundsInParent())) {
                
                Player player = (Player) gameField.getChildren().get(3);
                
                player.setDead(true);
                player.setRotate(0);
    
                gameField.getChildren().get(0).setVisible(true);
                Main.getScheduler().schedule(() -> Platform.runLater(() -> close()), 2000);
            }
            
            for(Iterator<Enemy> it = enemies.iterator(); it.hasNext(); ) {
                
                Enemy enemy = it.next();
                
                if (getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    
                    it.remove();
                    gameField.getChildren().remove(enemy);
                    
                    score+=10;
                    ((Label) gameField.getChildren().get(1)).setText("Score: " + score);
                }
            }
            
            Main.getScheduler().schedule(() -> Platform.runLater(() -> gameField.getChildren().remove(this)), 1000);
        }
    }
    
    private class Bullet extends Rectangle {
        
        private final Point2D velocity;
        
        public Bullet(Point2D start, Point2D velocity, double rotate) {
            super(20, 20);
            
            this.velocity = velocity;
            
            setTranslateX(start.getX());
            setTranslateY(start.getY());
            
            setRotate(rotate);
            
            setFill(ImageUtil.getImagePattern("images/bullet_icon.png", ImageUtil.ImageResolution.ORIGINAL));
        }
        
        public void update() {
            setTranslateX(getTranslateX() + velocity.getX());
            setTranslateY(getTranslateY() + velocity.getY());
        }
    }
    
    private class Player extends Circle {
    
        private boolean dead;
        
        public Player() {
            super(25);
            
            setFill(ImageUtil.getImagePattern("images/figure_icon.png", ImageUtil.ImageResolution.MEDIUM));
            
            setTranslateX(300);
            setTranslateY(300);
        }
        
        public void onMove(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case W:
    
                    if(isBorderInThatDirection(keyEvent.getCode()) || isDead())
                        return;
                    
                    moveUp();
                    break;
                case A:
    
                    if(isBorderInThatDirection(keyEvent.getCode()) || isDead())
                        return;
                    
                    moveLeft();
                    break;
                case S:
    
                    if(isBorderInThatDirection(keyEvent.getCode()) || isDead())
                        return;
                    
                    moveDown();
                    break;
                case D:
    
                    if(isBorderInThatDirection(keyEvent.getCode()) || isDead())
                        return;
                    
                    moveRight();
                    break;
                case Q:
                    rotateLeft();
                    break;
                case R:
                    rotateRight();
                    break;
            }
        }
        
        public void onShoot(MouseEvent event) {
            
            if(isDead())
                return;
    
            double angle = Math.toRadians(getRotate());
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            
            Point2D velocity = new Point2D.Double(x, y);
            
            Bullet bullet = new Bullet(getLocation(), velocity, getRotate());
            bullets.add(bullet);
            
            gameField.getChildren().add(bullet);
        }
        
        public void moveRight() {
            setTranslateX(getTranslateX() + 10);
        }
        
        public void moveLeft() {
            setTranslateX(getTranslateX() - 10);
        }
        
        public void moveUp() {
            setTranslateY(getTranslateY() - 10);
        }
        
        public void moveDown() {
            setTranslateY(getTranslateY() + 10);
        }
        
        public void rotateLeft() {
            setRotate(getRotate() - 10);
        }
        
        public void rotateRight() {
            setRotate(getRotate() + 10);
        }
    
        public void setDead(boolean dead) {
            this.dead = dead;
        }
    
        public boolean isBorderInThatDirection(KeyCode keyCode) {
            switch (keyCode) {
                case W:
                    return getTranslateY() <= 15;
                case A:
                    return getTranslateX() <= 15;
                case S:
                    return getTranslateY() >= 585;
                case D:
                    return getTranslateX() >= 585;
            }
            
            return false;
        }
    
        public boolean isDead() {
            return dead;
        }
    
        public Point2D getLocation() {
            return new Point2D.Double(getTranslateX(), getTranslateY());
        }
    }
}
