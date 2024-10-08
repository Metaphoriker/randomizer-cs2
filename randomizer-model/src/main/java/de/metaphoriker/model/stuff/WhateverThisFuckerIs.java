package de.metaphoriker.model.stuff;

import de.metaphoriker.model.ApplicationState;
import java.io.File;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is for whatever the fuck this is. I don't know. I don't care. It just works. Don't
 * touch it. But to be honest, I think it's for the appdata folder.
 *
 * @author GitHub-Copilot
 *     <p>But for real now, this class is for stuff idk where to put yet.
 */
public class WhateverThisFuckerIs {

  private static final File APPDATA_FOLDER =
      new File(System.getenv("APPDATA") + File.separator + "randomizer");

  @Getter @Setter
  private static volatile ApplicationState applicationState = ApplicationState.IDLING;

  private WhateverThisFuckerIs() {}

  public static File getAppdataFolder() {
    return APPDATA_FOLDER;
  }
}
