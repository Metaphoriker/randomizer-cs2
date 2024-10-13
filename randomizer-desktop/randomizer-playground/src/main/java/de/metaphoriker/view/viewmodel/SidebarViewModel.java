package de.metaphoriker.view.viewmodel;

import java.awt.*;

public class SidebarViewModel {

  private static final String DISCORD_LINK = "https://discord.gg/k448AWNVpk";
  private static final String WEBSITE_LINK = "http://randomizer-cs2.com";

  public void openDiscordLinkInBrowser() {
    try {
      Desktop.getDesktop().browse(new java.net.URI(DISCORD_LINK));
    } catch (java.io.IOException | java.net.URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public void openWebsiteLinkInBrowser() {
    try {
      Desktop.getDesktop().browse(new java.net.URI(WEBSITE_LINK));
    } catch (java.io.IOException | java.net.URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
