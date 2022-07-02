package me.nekocloud.packetlib.nms.interfaces.entity;

import me.nekocloud.api.entity.npc.types.EnderDragonNPC;

public interface DEntityDragon extends DEntityLiving {

    EnderDragonNPC.Phase getPhase();

    void setPhase(EnderDragonNPC.Phase phase);
}
