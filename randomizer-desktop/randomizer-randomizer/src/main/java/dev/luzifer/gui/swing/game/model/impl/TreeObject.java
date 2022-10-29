package dev.luzifer.gui.swing.game.model.impl;

import dev.luzifer.gui.swing.game.model.api.objects.GroundObject;
import dev.luzifer.gui.swing.game.model.location.Location;
import dev.luzifer.gui.swing.game.model.util.ImageHelper;

public class TreeObject extends GroundObject {

    public TreeObject(Location location) {
        super(location, 150, 300);

        setIcon(ImageHelper.getIcon("/tree"));
    }
}
