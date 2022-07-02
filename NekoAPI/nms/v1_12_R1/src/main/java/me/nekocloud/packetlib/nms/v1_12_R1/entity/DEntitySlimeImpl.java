package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntitySlime;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.World;

public class DEntitySlimeImpl extends DEntityLivingBase<EntitySlime> implements DEntitySlime {

    public DEntitySlimeImpl(World world) {
        super(new EntitySlime(world));
    }

    @Override
    public int getSize() {
        return entity.getSize();
    }

    @Override
    public void setSize(int size) {
        entity.setSize(size, true);
    }
}
