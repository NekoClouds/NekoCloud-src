package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityCreeper;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.CreeperNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;

public class CraftCreeperNPC extends CraftLivingNPC implements CreeperNPC {

    public CraftCreeperNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public DEntityLiving createNMSEntity() {
        return NMS_MANAGER.createDEntity(DEntityCreeper.class, location);
    }


    @Override
    public boolean isPowered() {
        return ((DEntityCreeper)entity).isPowered();
    }

    @Override
    public void setPowered(boolean flag) {
        if (this.isPowered() == flag)
            return;

        ((DEntityCreeper)entity).setPowered(flag);
        sendPacketMetaData();
    }

    @Override
    public NpcType getType() {
        return NpcType.CREEPER;
    }
}
