package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntitySlime;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.SlimeNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;

public class CraftSlimeNPC extends CraftLivingNPC implements SlimeNPC {

    public CraftSlimeNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public DEntityLiving createNMSEntity() {
        DEntitySlime slime =  NMS_MANAGER.createDEntity(DEntitySlime.class, location);
        slime.setSize(1);
        return slime;
    }

    @Override
    public int getSize() {
        return ((DEntitySlime)entity).getSize();
    }

    @Override
    public void setSize(int size) {
        ((DEntitySlime)entity).setSize(size);
        sendPacketMetaData();
    }

    @Override
    public NpcType getType() {
        return NpcType.SLIME;
    }
}
