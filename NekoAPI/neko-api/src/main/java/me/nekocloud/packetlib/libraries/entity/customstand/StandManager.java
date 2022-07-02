package me.nekocloud.packetlib.libraries.entity.customstand;

import me.nekocloud.api.entity.stand.CustomStand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandManager {

    private final Map<Integer, CraftStand> stands = new ConcurrentHashMap<>();

    public void addStand(CraftStand stand) {
        if (stands.containsKey(stand.getEntityID()))
            return;

        stands.put(stand.getEntityID(), stand);
    }

    public void removeStand(CustomStand stand) {
        stands.remove(stand.getEntityID());
    }

    public Map<Integer, CraftStand> getStands() {
        return stands;
    }

    CustomStand getStand(int entityID) {
        return stands.get(entityID);
    }
}
