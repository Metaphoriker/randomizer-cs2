package de.metaphoriker.model.config.keybind;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyBindNameTypeMapper {

  private final Map<String, NameType> descriptorToNameMap = new HashMap<>();

  public KeyBindNameTypeMapper() {
    initializeDescriptorMappings();
  }

  /**
   * Checks if the given descriptor exists in the descriptor-to-name map.
   *
   * @param descriptor the key to be checked in the map
   * @return true if the map contains the key; false otherwise
   */
  public boolean hasKey(String descriptor) {
    return descriptorToNameMap.containsKey(descriptor);
  }

  /**
   * Retrieves the key name associated with the given descriptor. If the descriptor does not have a
   * predefined key name, the descriptor itself is returned.
   *
   * @param descriptor The descriptor whose key name is to be retrieved.
   * @return The key name associated with the given descriptor, or the descriptor itself if no key
   *     name is found.
   */
  public String getKeyName(String descriptor) {
    if (!hasKey(descriptor)) {
      return descriptor;
    }
    NameType nameType = descriptorToNameMap.get(descriptor);
    return nameType.name();
  }

  /**
   * Retrieves the {@link Type} associated with the given descriptor.
   *
   * @param descriptor the key used to retrieve the associated Type
   * @return the Type associated with the given descriptor
   * @throws IllegalStateException if the given descriptor does not have an associated Type
   */
  public Type getType(String descriptor) {
    if (!hasKey(descriptor)) {
      return Type.MISCELLANEOUS;
    }
    NameType nameType = descriptorToNameMap.get(descriptor);
    return nameType.type();
  }

  private void initializeDescriptorMappings() {
    descriptorToNameMap.put("+jump", new NameType("Jump", Type.MOVEMENT));
    descriptorToNameMap.put("+left", new NameType("Move Left", Type.MOVEMENT));
    descriptorToNameMap.put("+right", new NameType("Move Right", Type.MOVEMENT));
    descriptorToNameMap.put("+back", new NameType("Move Backward", Type.MOVEMENT));
    descriptorToNameMap.put("+forward", new NameType("Move Forward", Type.MOVEMENT));
    descriptorToNameMap.put("+duck", new NameType("Crouch", Type.MOVEMENT));
    descriptorToNameMap.put("+sprint", new NameType("Sprint", Type.MOVEMENT));

    descriptorToNameMap.put("slot1", new NameType("Select Slot 1", Type.INVENTORY));
    descriptorToNameMap.put("slot2", new NameType("Select Slot 2", Type.INVENTORY));
    descriptorToNameMap.put("slot3", new NameType("Select Slot 3", Type.INVENTORY));
    descriptorToNameMap.put("slot4", new NameType("Select Slot 4", Type.INVENTORY));
    // descriptorToNameMap.put("slot5", new NameType("Select Slot 5", Type.INVENTORY));
    // descriptorToNameMap.put("slot6", new NameType("Select Slot 6", Type.INVENTORY));
    // descriptorToNameMap.put("slot7", new NameType("Select Slot 7", Type.INVENTORY));
    // descriptorToNameMap.put("slot8", new NameType("Select Slot 8", Type.INVENTORY));
    // descriptorToNameMap.put("slot9", new NameType("Select Slot 9", Type.INVENTORY));
    // descriptorToNameMap.put("slot10", new NameType("Select Slot 10", Type.INVENTORY));
    // descriptorToNameMap.put("slot12", new NameType("Select Slot 12", Type.INVENTORY));
    descriptorToNameMap.put("invnext", new NameType("Next Inventory Item", Type.INVENTORY));
    descriptorToNameMap.put("invprev", new NameType("Previous Inventory Item", Type.INVENTORY));
    descriptorToNameMap.put("lastinv", new NameType("Last Inventory Item", Type.INVENTORY));
    descriptorToNameMap.put("sellbackall", new NameType("Sell Back All Items", Type.INVENTORY));

    descriptorToNameMap.put("drop", new NameType("Drop Item", Type.WEAPON));
    descriptorToNameMap.put("+reload", new NameType("Reload", Type.WEAPON));
    descriptorToNameMap.put("+attack", new NameType("Primary Attack", Type.WEAPON));
    descriptorToNameMap.put("+attack2", new NameType("Secondary Attack", Type.WEAPON));
    descriptorToNameMap.put("+lookatweapon", new NameType("Inspect Weapon", Type.WEAPON));

    // descriptorToNameMap.put("buyammo1", new NameType("Buy Primary Ammo", Type.WEAPON_INVENTORY));
    // descriptorToNameMap.put("buyammo2", new NameType("Buy Secondary Ammo",
    // Type.WEAPON_INVENTORY));

    descriptorToNameMap.put("+use", new NameType("Use Object / Interact", Type.MISCELLANEOUS));
    descriptorToNameMap.put("messagemode", new NameType("Open Chat", Type.MISCELLANEOUS));
    descriptorToNameMap.put("player_ping", new NameType("Player Ping", Type.MISCELLANEOUS));
    descriptorToNameMap.put("jpeg", new NameType("Take Screenshot", Type.MISCELLANEOUS));
    descriptorToNameMap.put("switchhands", new NameType("Switch Hands", Type.MISCELLANEOUS));

    // descriptorToNameMap.put("messagemode2", new NameType("Team Chat",
    // Type.INTERACTION_COMMUNICATION));
    // descriptorToNameMap.put("radio", new NameType("Open Radio Menu",
    // Type.INTERACTION_COMMUNICATION));
    // descriptorToNameMap.put("buymenu", new NameType("Open Buy Menu",
    // Type.INTERACTION_COMMUNICATION));
    // descriptorToNameMap.put("teammenu", new NameType("Open Team Menu",
    // Type.INTERACTION_COMMUNICATION));
    // descriptorToNameMap.put("+radialradio", new NameType("Open Radio Commands",
    // Type.INTERACTION_COMMUNICATION));
    // descriptorToNameMap.put("+radialradio2", new NameType("Open Secondary Radio Commands",
    // Type.INTERACTION_COMMUNICATION));

    // descriptorToNameMap.put("show_loadout_toggle", new NameType("Show Loadout",
    // Type.VISUAL_INTERACTION));
    // descriptorToNameMap.put("+showscores", new NameType("Show Scores", Type.VISUAL_INTERACTION));
    // descriptorToNameMap.put("+spray_menu", new NameType("Spray Logo", Type.VISUAL_INTERACTION));

    // descriptorToNameMap.put("cancelselect", new NameType("Cancel Selection",
    // Type.MISCELLANEOUS));
    // descriptorToNameMap.put("toggleconsole", new NameType("Toggle Console", Type.MISCELLANEOUS));
    // descriptorToNameMap.put("autobuy", new NameType("Auto Buy", Type.MISCELLANEOUS));
    // descriptorToNameMap.put("rebuy", new NameType("Rebuy Last Purchase", Type.MISCELLANEOUS));
    // descriptorToNameMap.put("save quick", new NameType("Quick Save", Type.MISCELLANEOUS));
    // descriptorToNameMap.put("load quick", new NameType("Quick Load", Type.MISCELLANEOUS));
    // descriptorToNameMap.put("cs_quit_prompt", new NameType("Quit Game Prompt",
    // Type.MISCELLANEOUS));
    // descriptorToNameMap.put("+voicerecord", new NameType("Voice Record", Type.MISCELLANEOUS));

    log.info("{} Deskriptoren initialisiert.", descriptorToNameMap.size());
  }

  public enum Type {
    MOVEMENT,
    WEAPON,
    INVENTORY,
    MISCELLANEOUS
  }

  public record NameType(String name, Type type) {}
}
