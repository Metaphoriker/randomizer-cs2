package dev.luzifer.gui.view.views.game.objects.entity;

import dev.luzifer.gui.view.views.game.objects.ItemObject;

public interface Item extends LivingEntity {
    
    ItemObject.ItemType getItemType(); // TODO: Wtf, getting from Inheritant
    
}
