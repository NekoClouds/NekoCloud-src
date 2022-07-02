package me.nekocloud.packetlib.libraries.entity.newpacketentity.base;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity.WrapperPlayServerEntityHeadRotation;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity.WrapperPlayServerEntityLook;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity.WrapperPlayServerSpawnEntityLiving;
import me.nekocloud.api.entity.PacketEntityLiving;
import me.nekocloud.api.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class DPacketEntityLivingBase extends DPacketEntityBase implements PacketEntityLiving {

    private final UUID uuid = UUID.randomUUID();

    @Getter
    protected final EntityEquipImpl entityEquip;

    protected DPacketEntityLivingBase(Location location) {
        super(location);

        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, BYTE_SERIALIZER), (byte)0);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, FLOAT_SERIALIZER), (float)20.0); //Health
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(8, INTEGER_SERIALIZER), 0); //Potion effect color
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(9, BOOLEAN_SERIALIZER), false); //Is potion effect ambient
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(10, INTEGER_SERIALIZER), 0); //Number of arrows in entity

        entityEquip = new EntityEquipImpl(this);
    }

    @Override
    public void spawn(Player player) {
        if (canSee(player)) {
            return;
        }

        playersCanSee.put(player.getName().toLowerCase(), player);

        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(entityID);
        packet.setUniqueId(uuid);
        packet.setType(getEntityType());
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        //todo тут еще какие-то переменные в пакете (хз, надо или нет)
        packet.setMetadata(watcher);
        packet.sendPacket(player);

        entityEquip.sendAllItems(player);
    }

    @Override
    public void setLook(float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);

        WrapperPlayServerEntityLook look = new WrapperPlayServerEntityLook();
        look.setEntityID(entityID);
        look.setYaw(LocationUtil.getFixRotation(yaw));
        look.setPitch(LocationUtil.getFixRotation(pitch));
        look.setOnGround(true);
        sendPacket(look);

        WrapperPlayServerEntityHeadRotation headRotation = new WrapperPlayServerEntityHeadRotation();
        headRotation.setEntityID(entityID);
        headRotation.setHeadYaw(LocationUtil.getFixRotation(yaw));
        sendPacket(headRotation);
    }
}
