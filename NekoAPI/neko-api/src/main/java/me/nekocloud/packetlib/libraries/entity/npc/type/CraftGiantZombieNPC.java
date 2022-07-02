package me.nekocloud.packetlib.libraries.entity.npc.type;

import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.GiantNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityGiantZombie;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import org.bukkit.Location;

public class CraftGiantZombieNPC extends CraftLivingNPC implements GiantNPC {

	public CraftGiantZombieNPC(NPCManager npcManager, Location location) {
		super(npcManager, location);
	}

	@Override
	public DEntityLiving createNMSEntity() {
		return  NMS_MANAGER.createDEntity(DEntityGiantZombie.class, location);
	}

	@Override
	public NpcType getType() {
		return NpcType.GIANT_ZOMBIE;
	}
}
