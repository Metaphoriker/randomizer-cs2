package dev.luzifer.gui.swing.game;

import dev.luzifer.gui.swing.game.model.GameWorld;
import dev.luzifer.gui.swing.game.model.WorldGenerator;
import dev.luzifer.gui.swing.game.model.api.Player;
import dev.luzifer.gui.swing.game.model.api.World;
import dev.luzifer.gui.swing.game.model.impl.PlayerObject;
import dev.luzifer.gui.swing.game.model.location.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.CompletableFuture;

public class GameWindow extends JFrame {

    private final JPanel panel = new JPanel();

    private final transient World gameWorld = new GameWorld(this);
    private final transient GameSequence gameSequence = new GameSequence(this);

    public GameWindow() {
        super();

        setSize(1980, 1200);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel);

        setLocationRelativeTo(null);
        setResizable(false);

        panel.setBackground(Color.GRAY);

        setVisible(true);
    }

    public CompletableFuture initGame() {
        WorldGenerator worldGenerator = new WorldGenerator(gameWorld);
        return worldGenerator.prepareSpawnArea();
    }

    public void startGame() {

        createPlayer();

        Thread thread = new Thread(gameSequence);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> {

            System.out.println("Uncaught exception in thread " + t.getName());
            System.out.println("Exception: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Stacktrace: ");

            for (StackTraceElement stackTraceElement : e.getStackTrace())
                System.out.println(stackTraceElement);
        });
        thread.start();
    }

    public World getGameWorld() {
        return gameWorld;
    }

    private void createPlayer() {

        Player player = new PlayerObject(new Location(gameWorld,
                getGameWorld().getWidth() / 2,
                getGameWorld().getHeight() / 2));
        gameWorld.addEntity(player);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.onKeyPressed(e);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                player.onKeyReleased(e);
            }
        });
    }

}
