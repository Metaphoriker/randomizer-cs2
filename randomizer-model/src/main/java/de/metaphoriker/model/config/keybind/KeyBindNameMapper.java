package de.metaphoriker.model.config.keybind;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyBindNameMapper {

  private final Map<String, String> descriptorToNameMap = new HashMap<>();

  public KeyBindNameMapper() {
    initializeDescriptorMappings();
  }

  public boolean hasKey(String descriptor) {
    return descriptorToNameMap.containsKey(descriptor);
  }

  public String getKeyName(String descriptor) {
    return descriptorToNameMap.getOrDefault(descriptor, descriptor);
  }

  private void initializeDescriptorMappings() {
    // descriptorToNameMap.put("cancelselect", "Cancel Selection");
    // descriptorToNameMap.put("toggleconsole", "Toggle Console");
    // descriptorToNameMap.put("+showscores", "Show Scores");
    descriptorToNameMap.put("+jump", "Jump");
    // descriptorToNameMap.put("buyammo1", "Buy Primary Ammo");
    // descriptorToNameMap.put("buyammo2", "Buy Secondary Ammo");
    // descriptorToNameMap.put("slot10", "Select Slot 10");
    descriptorToNameMap.put("slot1", "Select Slot 1");
    descriptorToNameMap.put("slot2", "Select Slot 2");
    descriptorToNameMap.put("slot3", "Select Slot 3");
    descriptorToNameMap.put("slot4", "Select Slot 4");
    // descriptorToNameMap.put("slot5", "Select Slot 5");
    // descriptorToNameMap.put("slot6", "Select Slot 6");
    // descriptorToNameMap.put("slot7", "Select Slot 7");
    // descriptorToNameMap.put("slot8", "Select Slot 8");
    // descriptorToNameMap.put("slot9", "Select Slot 9");
    descriptorToNameMap.put("+left", "Move Left");
    // descriptorToNameMap.put("buymenu", "Open Buy Menu");
    // descriptorToNameMap.put("+radialradio", "Open Radio Commands");
    descriptorToNameMap.put("+right", "Move Right");
    descriptorToNameMap.put("+use", "Use Object / Interact");
    descriptorToNameMap.put("+lookatweapon", "Inspect Weapon");
    descriptorToNameMap.put("drop", "Drop Weapon");
    descriptorToNameMap.put("switchhands", "Switch Hands");
    // descriptorToNameMap.put("show_loadout_toggle", "Show Loadout");
    // descriptorToNameMap.put("teammenu", "Open Team Menu");
    // descriptorToNameMap.put("lastinv", "Last Inventory");
    descriptorToNameMap.put("+reload", "Reload");
    descriptorToNameMap.put("+back", "Move Backward");
    // descriptorToNameMap.put("+spray_menu", "Spray Logo");
    // descriptorToNameMap.put("messagemode2", "Team Chat");
    // descriptorToNameMap.put("+radialradio2", "Open Secondary Radio Commands");
    descriptorToNameMap.put("+forward", "Move Forward");
    // descriptorToNameMap.put("slot12", "Select Slot 12");
    descriptorToNameMap.put("messagemode", "Open Chat");
    // descriptorToNameMap.put("radio", "Open Radio Menu");
    descriptorToNameMap.put("+duck", "Crouch");
    descriptorToNameMap.put("+sprint", "Sprint");
    // descriptorToNameMap.put("autobuy", "Auto Buy");
    // descriptorToNameMap.put("rebuy", "Rebuy Last Purchase");
    descriptorToNameMap.put("jpeg", "Take Screenshot");
    // descriptorToNameMap.put("save quick", "Quick Save");
    // descriptorToNameMap.put("load quick", "Quick Load");
    // descriptorToNameMap.put("cs_quit_prompt", "Quit Game Prompt");
    descriptorToNameMap.put("invnext", "Next Inventory Item");
    descriptorToNameMap.put("invprev", "Previous Inventory Item");
    descriptorToNameMap.put("+attack", "Primary Attack");
    descriptorToNameMap.put("+attack2", "Secondary Attack");
    descriptorToNameMap.put("player_ping", "Player Ping");
    // descriptorToNameMap.put("+voicerecord", "Voice Record");
    descriptorToNameMap.put("sellbackall", "Sell Back All Items");

    log.info("{} Deskriptoren initialisiert.", descriptorToNameMap.size());
  }
}
