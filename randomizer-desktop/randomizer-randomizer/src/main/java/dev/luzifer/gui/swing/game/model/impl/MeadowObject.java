package dev.luzifer.gui.swing.game.model.impl;


import dev.luzifer.gui.swing.game.model.api.objects.GroundObject;
import dev.luzifer.gui.swing.game.model.location.Location;
import dev.luzifer.gui.swing.game.model.util.ImageHelper;

public class MeadowObject extends GroundObject {

    public MeadowObject(Location location) {
        super(location, 200, 200);

        setIcon(ImageHelper.getIcon("/meadow"));
    }
}
