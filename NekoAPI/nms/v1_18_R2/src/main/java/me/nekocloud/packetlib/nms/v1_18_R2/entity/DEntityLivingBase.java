package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import net.minecraft.server.v1_12_R1.EntityLiving;

public abstract class DEntityLivingBase<T extends EntityLiving> extends DEntityBase<T> implements DEntityLiving {

    protected DEntityLivingBase(T entity) {
        super(entity);
    }

    @Override
    public T getEntityNms() {
        return super.getEntityNms();
    }

    @Override
    public float getHeadPitch() {
        return entity.aP;
    }

    @Override
    public void setCollides(boolean collides) {
        entity.collides = collides;
    }
}
