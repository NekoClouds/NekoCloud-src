package me.nekocloud.packetlib.nms.interfaces.entity;

public interface DEntitySlime extends DEntityLiving {

    int getSize();

    void setSize(int size);

    //void setTarget(DEntityLiving entityLiving); //супер странная хуита, хз зачем она в бакките
    //DEntityLiving getTarget();
}
