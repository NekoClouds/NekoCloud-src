package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketMount;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutMount;

public class PacketMountImpl extends DPacketEntityBase<PacketPlayOutMount, DEntity> implements PacketMount {

    public PacketMountImpl(DEntity entity) {
        super(entity);
    }

    @Override
    protected PacketPlayOutMount init() {
        return new PacketPlayOutMount(((DEntityBase)entity).getEntityNms());
    }
}
