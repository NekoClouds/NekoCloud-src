package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityCow;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.CowNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;

public class CraftCowNPC extends CraftLivingNPC implements CowNPC {

    public CraftCowNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public DEntityLiving createNMSEntity() {
        return NMS_MANAGER.createDEntity(DEntityCow.class, location);
    }

    @Override
    public NpcType getType() {
        return NpcType.COW;
    }
}
