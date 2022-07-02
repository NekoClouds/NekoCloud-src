package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityDragon;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.EnderDragonNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;

public class CraftEnderDragonNPC extends CraftLivingNPC implements EnderDragonNPC {

    public CraftEnderDragonNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public DEntityLiving createNMSEntity() {
        return NMS_MANAGER.createDEntity(DEntityDragon.class, location);
    }

    @Override
    public NpcType getType() {
        return NpcType.ENDER_DRAGON;
    }

    @Override
    public Phase getPhase() {
        return ((DEntityDragon)entity).getPhase();
    }

    @Override
    public void setPhase(Phase phase) {
        ((DEntityDragon)entity).setPhase(phase);
        sendPacketMetaData();
    }
}
