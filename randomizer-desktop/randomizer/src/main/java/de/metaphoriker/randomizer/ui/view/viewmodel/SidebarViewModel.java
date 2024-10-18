package de.metaphoriker.randomizer.ui.view.viewmodel;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class SidebarViewModel {

  private static final String DISCORD_LINK = "https://discord.gg/k448AWNVpk";
  private static final String WONDERFUL_LINK = "https://youtu.be/dQw4w9WgXcQ";
  private static final String WEBSITE_LINK = "http://randomizer-cs2.com";

  private final ObjectProperty<Class<?>> selectedView = new SimpleObjectProperty<>();

  public void openDiscordLinkInBrowser() {
    openLinkInBrowser(DISCORD_LINK);
  }

  public void openWebsiteLinkInBrowser() {
    openLinkInBrowser(WEBSITE_LINK);
  }

  public void openLogoClickInBrowser() {
    openLinkInBrowser(WONDERFUL_LINK);
  }

  private void openLinkInBrowser(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException e) {
      log.error("An error occurred while browsing link: {}", url, e);
    }
  }

  public void setSelectedView(Class<?> viewClass) {
    this.selectedView.set(viewClass);
  }
}
