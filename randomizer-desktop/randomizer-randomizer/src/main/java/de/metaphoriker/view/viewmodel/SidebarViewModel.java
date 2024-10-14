package de.metaphoriker.view.viewmodel;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class SidebarViewModel {

  private static final String DISCORD_LINK = "https://discord.gg/k448AWNVpk";
  private static final String WONDERFUL_LINK = "https://youtu.be/dQw4w9WgXcQ";
  private static final String WEBSITE_LINK = "http://randomizer-cs2.com";

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
}
