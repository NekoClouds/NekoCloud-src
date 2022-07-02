package me.nekocloud.packetlib.libraries.entity.newpacketentity;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.nekocloud.packetlib.libraries.entity.customstand.StandManager;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.base.DPacketEntityBase;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.base.DPacketEntityLivingBase;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.base.PacketEntityUtil;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity.WrapperPlayServerEntityDestroy;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity.WrapperPlayServerMount;
import me.nekocloud.api.depend.CraftVector;
import me.nekocloud.api.entity.stand.CustomStand;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public final class DPacketArmorStand extends DPacketEntityLivingBase implements CustomStand {

    private final StandManager standManager;

    private DPacketEntityBase passenger;

    public DPacketArmorStand(StandManager standManager, Location location) {
        super(location);

        this.standManager = standManager;

        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, BYTE_SERIALIZER), (byte)0);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(12, VECTOR_3F_SERIALIZER), new Vector3F(0.0F, 0.0F, 0.0F));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(13, VECTOR_3F_SERIALIZER), new Vector3F(0.0F, 0.0F, 0.0F));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, VECTOR_3F_SERIALIZER), new Vector3F(-10.0F, 0.0F, -10.0F));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, VECTOR_3F_SERIALIZER), new Vector3F(-15.0F, 0.0F, 10.0F));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(16, VECTOR_3F_SERIALIZER), new Vector3F(-1.0F, 0.0F, -1.0F));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, VECTOR_3F_SERIALIZER), new Vector3F(1.0F, 0.0F, 1.0F));

        setNoGravity(true);

        passenger = null;

        //standManager.addStand(this);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public boolean isSmall() {
        return getStandFlag(1);
    }

    @Override
    public boolean hasArms() {
        return getStandFlag(4);
    }

    @Override
    public boolean hasBasePlate() {
        return getStandFlag(8);
    }

    public boolean isMarker() {
        return getStandFlag(16);
    }

    @Override
    public void setSmall(boolean small) {
        if (isSmall() == small) {
            return;
        }

        setStandFlag(1, small);
        //todo setSize зачем-то (как-то связано с пакетом спавна этого моба, я уверен)
    }

    @Override
    public void setArms(boolean arms) {
        if (hasArms() == arms) {
            return;
        }

        setStandFlag(4, arms);
    }

    @Override
    public void setBasePlate(boolean basePlate) {
        if (hasBasePlate() == basePlate) {
            return;
        }

        setStandFlag(8, basePlate);
    }

    public void setMarket(boolean market) {
        if (isMarker() == market) {
            return;
        }

        setStandFlag(16, market);
        //todo setSize зачем-то (как-то связано с пакетом спавна этого моба, я уверен)
    }

    @Override
    public void spawn(Player player) {
        super.spawn(player);

        if (hasPassenger()) {
            passenger.spawn(player);

            WrapperPlayServerMount mount = new WrapperPlayServerMount();
            mount.setEntityID(entityID);
            mount.setPassengerIds(new int[]{passenger.getEntityID()});
            mount.sendPacket(player);
        }
    }

    private boolean getStandFlag(int id) {
        return (((Byte)watcher.getWatchableObject(11).getValue()) & id) != 0;
    }

    private void setStandFlag(int id, boolean flag) {
        watcher.getWatchableObjects().forEach(wrappedWatchableObject -> wrappedWatchableObject.setDirtyState(false));
        byte oldValue = (byte) watcher.getWatchableObject(11).getValue();
        byte value = (byte)(oldValue | id);
        if (!flag) {
            value = (byte)(oldValue & ~id);
        }
        watcher.setObject(11, value, true);

        sendMetaData();
    }

    private void setDirection(int index, CraftVector vector) {
        watcher.getWatchableObjects().forEach(wrappedWatchableObject -> wrappedWatchableObject.setDirtyState(false));
        watcher.setObject(index, vector.toVector3f(), true);

        sendMetaData();
    }

    private CraftVector getDirection(int index) {
        return CraftVector.fromVector3f((Vector3F) watcher.getWatchableObject(index).getValue());
    }

    @Override
    public void setHeadPose(CraftVector vector) {
        setDirection(12, vector);
    }

    @Override
    public void setBodyPose(CraftVector vector) {
        setDirection(13, vector);
    }

    @Override
    public void setLeftArmPose(CraftVector vector) {
        setDirection(14, vector);
    }

    @Override
    public void setRightArmPose(CraftVector vector) {
        setDirection(15, vector);
    }

    @Override
    public void setRightLegPose(CraftVector vector) {
        setDirection(17, vector);
    }

    @Override
    public void setLeftLegPose(CraftVector vector) {
        setDirection(16, vector);
    }

    @Override
    public void setHeadPose(EulerAngle pose) {
        setHeadPose(PacketEntityUtil.toNMS(pose));
    }

    @Override
    public void setBodyPose(EulerAngle pose) {
        setBodyPose(PacketEntityUtil.toNMS(pose));
    }

    @Override
    public void setLeftArmPose(EulerAngle pose) {
        setLeftArmPose(PacketEntityUtil.toNMS(pose));
    }

    @Override
    public void setRightArmPose(EulerAngle pose) {
        setRightArmPose(PacketEntityUtil.toNMS(pose));
    }

    @Override
    public void setRightLegPose(EulerAngle pose) {
        setRightLegPose(PacketEntityUtil.toNMS(pose));
    }

    @Override
    public void setLeftLegPose(EulerAngle pose) {
        setLeftLegPose(PacketEntityUtil.toNMS(pose));
    }

    @Override
    public EulerAngle getHeadPose() {
        return PacketEntityUtil.fromNMS(getDirection(12));
    }

    @Override
    public EulerAngle getBodyPose() {
        return PacketEntityUtil.fromNMS(getDirection(13));
    }

    @Override
    public EulerAngle getLeftArmPose() {
        return PacketEntityUtil.fromNMS(getDirection(14));
    }

    @Override
    public EulerAngle getRightArmPose() {
        return PacketEntityUtil.fromNMS(getDirection(15));
    }

    @Override
    public EulerAngle getRightLegPose() {
        return PacketEntityUtil.fromNMS(getDirection(17));
    }

    @Override
    public EulerAngle getLeftLegPose() {
        return PacketEntityUtil.fromNMS(getDirection(16));
    }

    @Override
    public void destroy(Player player) {
        if (hasPassenger()) {
            int entityID = passenger.getEntityID();
            WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
            destroy.setEntityIds(new int[]{entityID, this.entityID});
            destroy.sendPacket(player);

            return;
        }

        super.destroy(player);
    }

    @Override
    public boolean hasPassenger() {
        return passenger != null;
    }

    @Override
    public void removePassenger() {
        if (!hasPassenger())
            return;

        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[] {passenger.getEntityID()});
        sendPacket(destroy);

        passenger = null;
    }

    @Override
    public void setItemPassenger(ItemStack itemPassenger) {
        if (hasPassenger()) {
            removePassenger();
        }

        passenger = new DPacketEntityItem(location, itemPassenger);
        playersCanSee.values().forEach(player -> passenger.spawn(player));

        WrapperPlayServerMount mount = new WrapperPlayServerMount();
        mount.setEntityID(entityID);
        mount.setPassengerIds(new int[]{passenger.getEntityID()});
        playersCanSee.values().forEach(mount::sendPacket);

    }

    @Override
    public void remove() {
        hideAll();
        standManager.removeStand(this);
    }
}
