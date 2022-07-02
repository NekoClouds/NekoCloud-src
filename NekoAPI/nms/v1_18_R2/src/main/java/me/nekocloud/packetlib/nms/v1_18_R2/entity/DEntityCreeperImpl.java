package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityCreeper;
import net.minecraft.server.v1_12_R1.EntityCreeper;
import net.minecraft.server.v1_12_R1.World;

public class DEntityCreeperImpl extends DEntityLivingBase<EntityCreeper> implements DEntityCreeper {

    public DEntityCreeperImpl(World world) {
        super(new EntityCreeper(world));
    }

    @Override
    public boolean isPowered() {
        return entity.isPowered();
    }

    @Override
    public void setPowered(boolean powered) {
        entity.setPowered(powered);
    }
}
