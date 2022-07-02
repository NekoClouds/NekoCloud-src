package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityGiantZombie;
import net.minecraft.server.v1_12_R1.EntityGiantZombie;
import net.minecraft.server.v1_12_R1.World;

public class DEntityGiantZombieImpl extends DEntityLivingBase<EntityGiantZombie> implements DEntityGiantZombie {

	public DEntityGiantZombieImpl(World world) {
		super(new EntityGiantZombie(world));
	}

}
