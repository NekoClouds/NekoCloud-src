package me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import me.nekocloud.packetlib.libraries.entity.newpacketentity.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class WrapperPlayServerEntityMetadata extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    public WrapperPlayServerEntityMetadata() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityMetadata(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public List<WrappedWatchableObject> getMetadata() {
        return handle.getWatchableCollectionModifier().read(0);
    }

    public void setMetadata(List<WrappedWatchableObject> value) {
        handle.getWatchableCollectionModifier().write(0, value);
    }
}
