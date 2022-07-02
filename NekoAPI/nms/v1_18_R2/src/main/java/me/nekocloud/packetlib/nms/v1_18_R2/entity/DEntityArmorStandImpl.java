package me.nekocloud.packetlib.nms.v1_12_R1.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntityArmorStand;
import me.nekocloud.api.depend.CraftVector;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.Vector3f;
import net.minecraft.server.v1_12_R1.World;

public class DEntityArmorStandImpl extends DEntityLivingBase<EntityArmorStand> implements DEntityArmorStand {

    public DEntityArmorStandImpl(World world) {
        super(new EntityArmorStand(world));
    }

    @Override
    public void setLeftLegPose(CraftVector vector) {
        entity.setLeftLegPose(new Vector3f(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public void setRightLegPose(CraftVector vector) {
        entity.setRightLegPose(new Vector3f(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public void setRightArmPose(CraftVector vector) {
        entity.setRightArmPose(new Vector3f(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public void setLeftArmPose(CraftVector vector) {
        entity.setLeftArmPose(new Vector3f(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public void setBodyPose(CraftVector vector) {
        entity.setBodyPose(new Vector3f(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public void setHeadPose(CraftVector vector) {
        entity.setHeadPose(new Vector3f(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public CraftVector getHeadPose() {
        return new CraftVector(entity.headPose.getX(), entity.headPose.getY(), entity.headPose.getZ());
    }

    @Override
    public CraftVector getBodyPose() {
        return new CraftVector(entity.bodyPose.getX(), entity.bodyPose.getY(), entity.bodyPose.getZ());
    }

    @Override
    public CraftVector getLeftArmPose() {
        return new CraftVector(entity.leftArmPose.getX(), entity.leftArmPose.getY(), entity.leftArmPose.getZ());
    }

    @Override
    public CraftVector getRightArmPose() {
        return new CraftVector(entity.rightArmPose.getX(), entity.rightArmPose.getY(), entity.rightArmPose.getZ());
    }

    @Override
    public CraftVector getLeftLegPose() {
        return new CraftVector(entity.leftLegPose.getX(), entity.leftLegPose.getY(), entity.leftLegPose.getZ());
    }

    @Override
    public CraftVector getRightLegPose() {
        return new CraftVector(entity.rightLegPose.getX(), entity.rightLegPose.getY(), entity.rightLegPose.getZ());
    }

    @Override
    public void setBasePlate(boolean plate) {
        entity.setBasePlate(plate);
    }

    @Override
    public void setInvisible(boolean invisible) {
        entity.setInvisible(invisible);
    }

    @Override
    public void setArms(boolean arms) {
        entity.setArms(arms);
    }

    @Override
    public void setSmall(boolean small) {
        entity.setSmall(small);
    }

    @Override
    public boolean hasBasePlate() {
        return entity.hasBasePlate();
    }

    @Override
    public boolean hasArms() {
        return entity.hasArms();
    }

    @Override
    public boolean isSmall() {
        return entity.isSmall();
    }

    @Override
    public void setMarker(boolean maker) {
        entity.setMarker(maker);
    }
}
