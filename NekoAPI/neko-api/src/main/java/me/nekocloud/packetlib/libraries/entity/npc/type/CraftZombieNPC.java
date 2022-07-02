package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityZombie;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.ZombieNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import org.bukkit.Location;

public class CraftZombieNPC extends CraftLivingNPC implements ZombieNPC {

    public CraftZombieNPC(NPCManager npcManager, Location location) {
        super(npcManager, location);
    }

    @Override
    public DEntityLiving createNMSEntity() {
        return NMS_MANAGER.createDEntity(DEntityZombie.class, location);
    }

    @Override
    public NpcType getType() {
        return NpcType.ZOMBIE;
    }

    @Override
    public boolean isBaby() {
        return ((DEntityZombie)entity).isBaby();
    }

    @Override
    public void setBaby(boolean baby) {
        ((DEntityZombie)entity).setBaby(baby);
        sendPacketMetaData();
    }
}
