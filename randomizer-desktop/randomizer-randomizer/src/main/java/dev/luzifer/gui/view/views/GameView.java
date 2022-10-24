/*
 * xD
 * D
 * D
 * D
 * D
 * D
 * D
 * D
 * D
 * D
 * D
 * D
 * D
 * xxdd
 * xD
 *
 *
 * lmao
 */


package dev.luzifer.gui.view.views;

import dev.luzifer.Main;
import dev.luzifer.gui.util.ImageUtil;
import dev.luzifer.gui.view.View;
import dev.luzifer.gui.view.models.GameViewModel;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
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
    private final List<Weapon> weapons = new ArrayList<>();
    private final List<AmmoBox> ammoBoxes = new ArrayList<>();
    private final List<Molotov> molotovs = new ArrayList<>();
    private final List<Molotov> flyingMolotov = new ArrayList<>();
    private final List<Fire> fires = new ArrayList<>();
    
    private final List<Obstacle> obstacles = new ArrayList<>();
    
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
        Platform.runLater(() -> {
            getScene().setOnKeyPressed(player::onMove);
            getScene().setOnKeyReleased(player::onUnMove);
            getScene().setOnMouseClicked(player::onClick);
        });
    
        update(player);
    
        gameField.getChildren().add(player);
        
        Label molotovLabel = new Label();
        molotovLabel.setTranslateX(500);
        molotovLabel.setTranslateY(550);
        molotovLabel.setVisible(true);
        molotovLabel.setText("x" + player.getMolotovs().size());
        molotovLabel.setFont(molotovLabel.getFont().font(24));
        molotovLabel.setGraphic(ImageUtil.getImageView("images/molotov_icon.png"));
        molotovLabel.setContentDisplay(ContentDisplay.RIGHT);
        gameField.getChildren().add(molotovLabel);
        
        for(int i = 0; i < ThreadLocalRandom.current().nextInt(5); i++) {
            
            Obstacle obstacle = new Obstacle();
            
            obstacles.add(obstacle);
            gameField.getChildren().add(obstacle);
        }
    }
    
    private AnimationTimer update(Player player) {
        
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                
                player.update();
                
                enemies.forEach(enemy -> {
                    
                    if (enemy.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        
                        player.damage(2);
                        
                        if(player.isDead()) {
                            
                            player.setRotate(0);
    
                            gameField.getChildren().get(0).setVisible(true);
                            Main.getScheduler().schedule(() -> Platform.runLater(() -> close()), 2000);
                            
                            return;
                        }
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
                
                for(Iterator<Weapon> it = weapons.iterator(); it.hasNext(); ) {
                    
                    Weapon weapon = it.next();
                    
                    if(weapon.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        
                        player.setWeapon(weapon);
                        
                        it.remove();
                        gameField.getChildren().remove(weapon);
                    }
                }
                
                for(Iterator<AmmoBox> it = ammoBoxes.iterator(); it.hasNext(); ) {
                    
                    AmmoBox ammoBox = it.next();
                    
                    if(ammoBox.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        
                        if(player.hasWeapon())
                            player.getWeapon().refill();
                        
                        it.remove();
                        gameField.getChildren().remove(ammoBox);
                    }
                }
                
                for(Iterator<Molotov> it = molotovs.iterator(); it.hasNext(); ) {
                    
                    Molotov molotov = it.next();
                    
                    if(molotov.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        
                        player.addMolotov(molotov);
                        
                        it.remove();
                        gameField.getChildren().remove(molotov);
                    }
                }
                
                for(Iterator<Molotov> it = flyingMolotov.iterator(); it.hasNext(); ) {
                    
                    Molotov molotov = it.next();
                    molotov.update();
                    
                    for(Iterator<Enemy> it2 = enemies.iterator(); it2.hasNext(); ) {
                        
                        Enemy enemy = it2.next();
                        
                        if (molotov.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            
                            it.remove();
                            gameField.getChildren().remove(molotov);
                            
                            Fire fire = new Fire(enemy.getLocation());
                            fires.add(fire);
                            
                            gameField.getChildren().add(fire);
                            
                            break;
                        }
                    }
                }
                
                for(Iterator<Fire> it = fires.iterator(); it.hasNext(); ) {
                    
                    Fire fire = it.next();
                    fire.update();
                    
                    if(fire.isExpired()) {
    
                        it.remove();
                        gameField.getChildren().remove(fire);
                        
                        return;
                    }
                    
                    for(Iterator<Enemy> it2 = enemies.iterator(); it2.hasNext(); ) {
                        
                        Enemy enemy = it2.next();
                        
                        if (fire.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            
                            it2.remove();
                            gameField.getChildren().remove(enemy);
                            
                            score+=10;
                            ((Label) gameField.getChildren().get(1)).setText("Score: " + score);
                            
                            break;
                        }
                    }
                }
    
                if(ThreadLocalRandom.current().nextInt(100) <= 1)
                    gameField.getChildren().add(new Enemy(player));
                
                if(ThreadLocalRandom.current().nextDouble(100) <= 0.25)
                    gameField.getChildren().add(new Bomb());
                
                if(ThreadLocalRandom.current().nextDouble(100) <= 0.5) {
                    
                    AmmoBox ammoBox = new AmmoBox();
                    
                    gameField.getChildren().add(ammoBox);
                    ammoBoxes.add(ammoBox);
                }
                
                if(ThreadLocalRandom.current().nextDouble(100) <= 0.1) {
                    
                    Molotov molotov = new Molotov();
                    
                    gameField.getChildren().add(molotov);
                    molotovs.add(molotov);
                }
                
                if(ThreadLocalRandom.current().nextDouble(100) <= 1) {
                    if(weapons.isEmpty() && !player.hasWeapon()) {
                        
                        Weapon weapon = new Weapon();
    
                        gameField.getChildren().add(weapon);
                        weapons.add(weapon);
                    }
                }
            }
        };
        animationTimer.start();
        
        return animationTimer;
    }
    
    private class Fire extends Circle {
        
        private final double speed = 0.25;
        
        private boolean expired = false;
        
        public Fire(Point2D point2D) {
            super(50);
            
            setTranslateX(point2D.getX());
            setTranslateY(point2D.getY());
            
            setFill(ImageUtil.getImagePattern("images/fire_icon.gif"));
        }
        
        public void update() {
            
            setRadius(getRadius() - speed);
            
            if(getRadius() <= 0)
                expired = true;
        }
    
        public boolean isExpired() {
            return expired;
        }
    }
    
    private class Enemy extends Rectangle {
    
        private final Player player;
        
        public Enemy(Player toChase) {
            super(30, 30);
            
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
            return new Point2D.Double(getTranslateX(), getTranslateY());
        }
    }
    
    private class Obstacle extends Rectangle {
        
        public Obstacle() {
            super(ThreadLocalRandom.current().nextInt(50, 151), ThreadLocalRandom.current().nextInt(50, 76));
            
            setTranslateX(ThreadLocalRandom.current().nextInt(600));
            setTranslateY(ThreadLocalRandom.current().nextInt(600));
            
            setFill(ImageUtil.getImagePattern("images/obstacle_icon.png", ImageUtil.ImageResolution.ORIGINAL));
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
    
    private class AmmoBox extends Rectangle {
        
        public AmmoBox() {
            super(30, 30);
            
            setFill(ImageUtil.getImagePattern("images/ammo_box_icon.png", ImageUtil.ImageResolution.ORIGINAL));
            
            setTranslateX(ThreadLocalRandom.current().nextInt(600));
            setTranslateY(ThreadLocalRandom.current().nextInt(600));
        }
    }
    
    private class Weapon extends Rectangle {
        
        private final Label ammoLabel;
        
        private int ammo = 30;
        
        public Weapon() {
            super(100, 100);
            
            setFill(ImageUtil.getImagePattern("images/weapon_icon.png", ImageUtil.ImageResolution.ORIGINAL));
            
            setTranslateX(ThreadLocalRandom.current().nextInt(600));
            setTranslateY(ThreadLocalRandom.current().nextInt(600));
    
            ammoLabel = new Label();
            ammoLabel.setTranslateX(500);
            ammoLabel.setTranslateY(530);
            ammoLabel.setVisible(true);
            ammoLabel.setText("x" + ammo);
            ammoLabel.setFont(ammoLabel.getFont().font(24));
            ammoLabel.setGraphic(ImageUtil.getImageView("images/ammo_box_icon.png"));
            ammoLabel.setContentDisplay(ContentDisplay.RIGHT);
            gameField.getChildren().add(ammoLabel);
        }
        
        public void refill() {
            ammo = 30;
            ammoLabel.setText("x" + ammo);
        }
        
        public void shoot(Point2D location, double rotate) {
            
            if(ammo == 0)
                return;
            
            double angle = Math.toRadians(rotate);
            double x = Math.cos(angle);
            double y = Math.sin(angle);
    
            Point2D velocity = new Point2D.Double(x, y);
    
            Bullet bullet = new Bullet(location, velocity, rotate);
            bullets.add(bullet);
    
            gameField.getChildren().add(bullet);
            
            ammo--;
            ammoLabel.setText("x" + ammo);
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
                
                player.damage(2);
                if(player.isDead()) {
                    
                    player.setRotate(0);
    
                    gameField.getChildren().get(0).setVisible(true);
                    Main.getScheduler().schedule(() -> Platform.runLater(() -> close()), 2000);
                    
                    return;
                }
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
    
    private class Molotov extends Rectangle {
        
        private Point2D velocity;
        
        public Molotov() {
            super(20, 20);
            
            setFill(ImageUtil.getImagePattern("images/molotov_icon.png", ImageUtil.ImageResolution.ORIGINAL));
            
            setTranslateX(ThreadLocalRandom.current().nextInt(600));
            setTranslateY(ThreadLocalRandom.current().nextInt(600));
        }
        
        public void fly(Point2D start, Point2D velocity, double rotate) {
            
            this.velocity = velocity;
            
            setTranslateX(start.getX());
            setTranslateY(start.getY());
            
            setRotate(rotate);
            
            flyingMolotov.add(this);
        }
        
        public void update() {
            setTranslateX(getTranslateX() + velocity.getX());
            setTranslateY(getTranslateY() + velocity.getY());
        }
    }
    
    private class Player extends Rectangle {
        
        private final List<KeyCode> pressingKeys = new ArrayList<>();
        
        private final List<Molotov> molotovs = new ArrayList<>();
        
        private final HealthBar healthBar = new HealthBar();
        
        private int health = 20;
        private Weapon weapon;
        
        private boolean animationBoolean = false;
        
        public Player() {
            super(50, 50);
            
            setFill(ImageUtil.getImagePattern("images/figure_icon.png", ImageUtil.ImageResolution.MEDIUM));
    
            healthBar.translateXProperty().bind(translateXProperty());
            healthBar.translateYProperty().bind(translateYProperty().subtract(10));
            
            setTranslateX(300);
            setTranslateY(300);
            
            gameField.getChildren().add(healthBar);
        }
        
        public void addMolotov(Molotov molotov) {
            molotovs.add(molotov);
            ((Label) gameField.getChildren().get(5)).setText("x" + molotovs.size());
        }
        
        public void setWeapon(Weapon weapon) {
            this.weapon = weapon;
        }
    
        public void damage(int amount) {
            
            health -= amount;
            healthBar.updateHealth(health);
            
            if(health <= 0)
                setFill(ImageUtil.getImagePattern("images/figure_dead_icon.png", ImageUtil.ImageResolution.ORIGINAL));
        }
        
        public void update() {
            
/*
            if(!pressingKeys.isEmpty()) {
                if(animationBoolean) {
                    playerRepresentation.setRadius(playerRepresentation.getRadius() + 2);
                    animationBoolean = false;
                } else {
                    playerRepresentation.setRadius(playerRepresentation.getRadius() - 2);
                    animationBoolean = true;
                }
            }
 */
            
            pressingKeys.forEach(key -> {
                
                if(isBorderInThatDirection(key) || isObstacleInThatDirection(key))
                    return;
                
                switch (key) {
                    case W:
                        moveUp();
                        break;
                    case S:
                        moveDown();
                        break;
                    case A:
                        moveLeft();
                        break;
                    case D:
                        moveRight();
                        break;
                    case Q:
                        rotateLeft();
                        break;
                    case R:
                        rotateRight();
                        break;
                    case SPACE:
                        shoot();
                        break;
                }
            });
        }
        
        public void onMove(KeyEvent keyEvent) {
    
            if(isBorderInThatDirection(keyEvent.getCode()) || isDead())
                return;
            
            if(!pressingKeys.contains(keyEvent.getCode())) // Check for only-accept keycodes
                pressingKeys.add(keyEvent.getCode());
        }
        
        public void onUnMove(KeyEvent keyEvent) {
            pressingKeys.remove(keyEvent.getCode());
        }
        
        public void onClick(MouseEvent mouseEvent) {
            
            if(isDead())
                return;
            
            if(!molotovs.isEmpty()) {
                
                Molotov molotov = molotovs.remove(0);
                ((Label) gameField.getChildren().get(5)).setText("x" + molotovs.size());
    
                double angle = Math.toRadians(getRotate());
                double x = Math.cos(angle);
                double y = Math.sin(angle);
    
                Point2D velocity = new Point2D.Double(x, y);
                molotov.fly(new Point2D.Double(getTranslateX(), getTranslateY()), velocity, getRotate());
                
                gameField.getChildren().add(molotov);
            }
        }
        
        public void shoot() {
            if(hasWeapon())
                weapon.shoot(getLocation(), getRotate());
        }
        
        public void moveRight() {
            setTranslateX(getTranslateX() + 5);
        }
        
        public void moveLeft() {
            setTranslateX(getTranslateX() - 5);
        }
        
        public void moveUp() {
            setTranslateY(getTranslateY() - 5);
        }
        
        public void moveDown() {
            setTranslateY(getTranslateY() + 5);
        }
        
        public void rotateLeft() {
            setRotate(getRotate() - 5);
        }
        
        public void rotateRight() {
            setRotate(getRotate() + 5);
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
        
        public boolean isObstacleInThatDirection(KeyCode keyCode) {
            
            double predictedX = getTranslateX();
            double predictedY = getTranslateY();
            
            switch (keyCode) {
                case W:
                    predictedY -= 5;
                    break;
                case A:
                    predictedX -= 5;
                    break;
                case S:
                    predictedY += 5;
                    break;
                case D:
                    predictedX += 5;
                    break;
            }
            
            for(Obstacle obstacle : obstacles)
                if(obstacle.getBoundsInParent().contains(predictedX, predictedY))
                    return true;
            
            return false;
        }
    
        public int getHealth() {
            return health;
        }
    
        public boolean isDead() {
            return health <= 0;
        }
        
        public boolean hasWeapon() {
            return weapon != null;
        }
    
        public Point2D getLocation() {
            return new Point2D.Double(getTranslateX(), getTranslateY());
        }
    
        public List<Molotov> getMolotovs() {
            return molotovs;
        }
    
        public Weapon getWeapon() {
            return weapon;
        }
    }
    
    private class HealthBar extends Pane {
        
        private final Rectangle healthBar = new Rectangle();
        private final Rectangle healthBarBackground = new Rectangle();
        
        private final Label healthLabel = new Label();
        
        public HealthBar() {
            
            healthBarBackground.setWidth(100);
            healthBarBackground.setHeight(15);
            healthBarBackground.setFill(Color.RED);
            
            healthBar.setWidth(100);
            healthBar.setHeight(15);
            healthBar.setFill(Color.GREEN);
            
            healthLabel.setText("Health: 20");
            healthLabel.setTextFill(Color.WHITE);
            
            getChildren().addAll(healthBarBackground, healthBar, healthLabel);
        }
        
        public void updateHealth(int health) {
            healthBar.setWidth(health * 5);
            healthLabel.setText("Health: " + health);
        }
    }
}
