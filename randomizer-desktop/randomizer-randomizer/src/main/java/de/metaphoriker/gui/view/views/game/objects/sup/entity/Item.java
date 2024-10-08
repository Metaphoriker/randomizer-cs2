package de.metaphoriker.gui.view.views.game.objects.sup.entity;

import de.metaphoriker.gui.view.views.game.objects.sup.ItemObject;

public interface Item extends LivingEntity {

  ItemObject.ItemType getItemType(); // TODO: Wtf, getting from Inheritant
}
