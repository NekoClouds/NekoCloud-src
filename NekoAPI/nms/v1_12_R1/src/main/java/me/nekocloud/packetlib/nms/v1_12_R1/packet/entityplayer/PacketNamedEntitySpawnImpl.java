package me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer;

import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketNamedEntitySpawn;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityPlayerImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entity.DPacketEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;

public class PacketNamedEntitySpawnImpl extends DPacketEntityBase<PacketPlayOutNamedEntitySpawn, DEntityPlayer>
        implements PacketNamedEntitySpawn {

    public PacketNamedEntitySpawnImpl(DEntityPlayer entity) {
        super(entity);
    }

    @Override
    protected PacketPlayOutNamedEntitySpawn init() {
        return new PacketPlayOutNamedEntitySpawn(((DEntityPlayerImpl)entity).getEntityNms());
    }
}
