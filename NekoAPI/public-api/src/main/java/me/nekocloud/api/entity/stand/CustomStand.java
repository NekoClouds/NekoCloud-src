package me.nekocloud.api.entity.stand;

import me.nekocloud.api.depend.CraftVector;
import me.nekocloud.api.entity.PacketEntityLiving;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public interface CustomStand extends PacketEntityLiving {
    boolean isSmall();
    boolean hasArms();
    boolean hasBasePlate();
    boolean isInvisible();

    boolean hasPassenger();
    void removePassenger();
    void setItemPassenger(ItemStack itemPassenger);

    void setSmall(boolean small);
    void setArms(boolean arms);
    void setBasePlate(boolean basePlate);
    void setInvisible(boolean invisible);

    void setCustomName(String name);

    /**
     * положение тела армор стенда
     * @param vector - вектор положения
     */
    void setHeadPose(CraftVector vector);
    void setBodyPose(CraftVector vector);
    void setLeftArmPose(CraftVector vector);
    void setRightArmPose(CraftVector vector);
    void setRightLegPose(CraftVector vector);
    void setLeftLegPose(CraftVector vector);

    void setHeadPose(EulerAngle pose);
    void setBodyPose(EulerAngle pose);
    void setLeftArmPose(EulerAngle pose);
    void setRightArmPose(EulerAngle pose);
    void setRightLegPose(EulerAngle pose);
    void setLeftLegPose(EulerAngle pose);

    EulerAngle getHeadPose();
    EulerAngle getBodyPose();
    EulerAngle getLeftArmPose();
    EulerAngle getRightArmPose();
    EulerAngle getRightLegPose();
    EulerAngle getLeftLegPose();
}
