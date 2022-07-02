package me.nekocloud.packetlib.nms.interfaces.entity;

import me.nekocloud.api.depend.CraftVector;

public interface DEntityArmorStand extends DEntityLiving {

    void setLeftLegPose(CraftVector vector);
    void setRightLegPose(CraftVector vector);
    void setRightArmPose(CraftVector vector);
    void setLeftArmPose(CraftVector vector);
    void setBodyPose(CraftVector vector);
    void setHeadPose(CraftVector vector);

    CraftVector getHeadPose();
    CraftVector getBodyPose();
    CraftVector getLeftArmPose();
    CraftVector getRightArmPose();
    CraftVector getLeftLegPose();
    CraftVector getRightLegPose();

    void setBasePlate(boolean plate);
    void setInvisible(boolean invisible);

    void setArms(boolean arms);
    void setSmall(boolean small);

    boolean hasBasePlate();
    boolean hasArms();
    boolean isSmall();

    void setMarker(boolean maker);
}
