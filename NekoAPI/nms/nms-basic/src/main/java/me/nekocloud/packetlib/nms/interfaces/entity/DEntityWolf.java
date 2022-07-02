package me.nekocloud.packetlib.nms.interfaces.entity;

import org.bukkit.DyeColor;

public interface DEntityWolf extends DEntityLiving {

    DyeColor getCollarColor();

    void setCollarColor(DyeColor color);

    boolean isAngry();

    void setAngry(boolean angry);

    boolean isSitting();

    void setSitting(boolean sitting);
}
