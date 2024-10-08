package de.metaphoriker.util;

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

public final class ImageUtil {

  private static final ImageResolution DEFAULT_RESOLUTION = ImageResolution.MEDIUM;

  private ImageUtil() {}

  public static Image getRawImage(String path) {
    URL imageUrl = ImageUtil.class.getResource(path);
    if (imageUrl == null) {
      throw new IllegalArgumentException("Image not found: " + path);
    }
    return new Image(imageUrl.toExternalForm());
  }

  public static ImageView getImageView(String path) {
    return getImageView(path, DEFAULT_RESOLUTION);
  }

  public static ImageView getImageView(String path, ImageResolution resolution) {
    URL imageUrl = ImageUtil.class.getResource(path);
    if (imageUrl == null) {
      throw new IllegalArgumentException("Image not found: " + path);
    }
    return new ImageView(
        new Image(
            imageUrl.toExternalForm(), resolution.getWidth(), resolution.getHeight(), true, true));
  }

  public static ImagePattern getImagePattern(String path) {
    return new ImagePattern(getRawImage(path));
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

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }
  }
}
