package de.metaphoriker.view.game.objects.sup.entity;

import de.metaphoriker.view.game.objects.sup.ItemObject;

public interface Item extends LivingEntity {

  ItemObject.ItemType getItemType(); // TODO: Wtf, getting from Inheritant
}
