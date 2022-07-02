package me.nekocloud.packetlib.nms.v1_12_R1.entity;


import me.nekocloud.packetlib.nms.interfaces.entity.DEntityBlaze;
import net.minecraft.server.v1_12_R1.EntityBlaze;
import net.minecraft.server.v1_12_R1.World;

public class DEntityBlazeImpl extends DEntityLivingBase<EntityBlaze> implements DEntityBlaze {

    public DEntityBlazeImpl(World world) {
        super(new EntityBlaze(world));
    }
}
