package de.metaphoriker.randomizer.ui.util;

import de.metaphoriker.randomizer.Main;
import java.net.URL;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.ImagePattern;
import lombok.Getter;

public final class ImageUtil {

  private static final ImageResolution DEFAULT_RESOLUTION = ImageResolution.MEDIUM;

  private ImageUtil() {}

  private static URL fetchResourceAsURL(String path) {
    URL url = Main.class.getResource(path);
    if (url == null) {
      throw new IllegalArgumentException("Resource not found: " + path);
    }
    return url;
  }

  public static Image getRawImage(String path) {
    return new Image(fetchResourceAsURL(path).toExternalForm());
  }

  public static Image getImage(String path) {
    return getImage(path, DEFAULT_RESOLUTION);
  }

  public static Image getImage(String path, ImageResolution resolution) {
    return new Image(
        fetchResourceAsURL(path).toExternalForm(),
        resolution.getWidth(),
        resolution.getHeight(),
        true,
        true);
  }

  public static ImageView getRawImageView(String path) {
    return new ImageView(getRawImage(path));
  }

  public static ImageView getImageView(String path) {
    return getImageView(path, DEFAULT_RESOLUTION);
  }

  public static ImageView getImageView(String path, ImageResolution resolution) {
    return new ImageView(getImage(path, resolution));
  }

  public static ImagePattern getRawImagePattern(String path) {
    return new ImagePattern(getRawImage(path));
  }

  public static ImagePattern getImagePattern(String path) {
    return getImagePattern(path, DEFAULT_RESOLUTION);
  }

  public static ImagePattern getImagePattern(String path, ImageResolution resolution) {
    return new ImagePattern(getImage(path, resolution));
  }

  public static Background getBackground(String imageName) {
    return new Background(convertPNGToBackgroundImage(imageName));
  }

  private static BackgroundImage convertPNGToBackgroundImage(String name) {
    Image image = getRawImage(name);

    return new BackgroundImage(
        image,
        BackgroundRepeat.REPEAT,
        BackgroundRepeat.NO_REPEAT,
        new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
  }

  @Getter
  public enum ImageResolution {
    SMALL(16, 16),
    OKAY(24, 24),
    MEDIUM(32, 32),
    LARGE(64, 64),
    ORIGINAL(-1, -1);

    private final int width;
    private final int height;

    ImageResolution(int width, int height) {
      this.width = width;
      this.height = height;
    }
  }
}
