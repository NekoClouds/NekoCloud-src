package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.libraries.entity.npc.CraftNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class CraftLivingNPC extends CraftNPC {

    protected CraftLivingNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public void spawnEntity(Player player) {
        if (entity == null)
            return;

        PACKET_CONTAINER.getSpawnEntityLivingPacket(entity).sendPacket(player);
    }

    void sendPacketMetaData() {
        sendNearby(PACKET_CONTAINER.getEntityMetadataPacket(entity));
    }
}
