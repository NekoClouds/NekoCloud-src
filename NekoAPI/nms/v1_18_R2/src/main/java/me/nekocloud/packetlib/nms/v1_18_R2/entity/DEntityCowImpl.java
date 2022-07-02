package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityCow;
import net.minecraft.server.v1_12_R1.EntityCow;
import net.minecraft.server.v1_12_R1.World;

public class DEntityCowImpl extends DEntityLivingBase<EntityCow> implements DEntityCow {

    public DEntityCowImpl(World world) {
        super(new EntityCow(world));
    }
}
