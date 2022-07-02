package me.nekocloud.packetlib.nms.interfaces.entity;

import me.nekocloud.api.entity.npc.types.VillagerNPC;

public interface DEntityVillager extends DEntityLiving {

    VillagerNPC.Profession getProfession();

    void setVillagerProfession(VillagerNPC.Profession profession);
}
