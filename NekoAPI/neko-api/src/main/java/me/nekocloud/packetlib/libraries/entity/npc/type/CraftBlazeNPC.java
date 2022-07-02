package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityBlaze;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.BlazeNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;

public class CraftBlazeNPC extends CraftLivingNPC implements BlazeNPC {

    public CraftBlazeNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public DEntityLiving createNMSEntity() {
        return NMS_MANAGER.createDEntity(DEntityBlaze.class, location);
    }

    @Override
    public NpcType getType() {
        return NpcType.BLAZE;
    }
}
