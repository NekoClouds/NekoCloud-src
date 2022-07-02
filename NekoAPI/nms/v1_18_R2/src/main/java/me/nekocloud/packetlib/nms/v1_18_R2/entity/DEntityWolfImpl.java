package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityWolf;
import net.minecraft.server.v1_12_R1.EntityWolf;
import net.minecraft.server.v1_12_R1.EnumColor;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.DyeColor;

public class DEntityWolfImpl extends DEntityLivingBase<EntityWolf> implements DEntityWolf {

    public DEntityWolfImpl(World world) {
        super(new EntityWolf(world));
    }

    @Override
    public DyeColor getCollarColor() {
        return DyeColor.getByWoolData((byte)entity.getCollarColor().getColorIndex());
    }

    @Override
    public void setCollarColor(DyeColor color) {
        entity.setTamed(true);
        entity.setCollarColor(EnumColor.fromColorIndex(color.getWoolData()));
    }

    @Override
    public boolean isAngry() {
        return entity.isAngry();
    }

    @Override
    public void setAngry(boolean angry) {
        entity.setAngry(angry);
    }

    @Override
    public boolean isSitting() {
        return entity.isSitting();
    }

    @Override
    public void setSitting(boolean sitting) {
        entity.setSitting(sitting);
        entity.getGoalSit().setSitting(sitting);
    }
}
