package de.metaphoriker.view.viewmodel;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SidebarViewModel {

  private static final String DISCORD_LINK = "https://discord.gg/k448AWNVpk";
  private static final String WONDERFUL_LINK = "https://youtu.be/dQw4w9WgXcQ";
  private static final String WEBSITE_LINK = "http://randomizer-cs2.com";

  public void openDiscordLinkInBrowser() {
    try {
      Desktop.getDesktop().browse(new URI(DISCORD_LINK));
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public void openWebsiteLinkInBrowser() {
    try {
      Desktop.getDesktop().browse(new URI(WEBSITE_LINK));
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public void openLogoClickInBrowser() {
    try {
      Desktop.getDesktop().browse(new URI(WONDERFUL_LINK));
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
