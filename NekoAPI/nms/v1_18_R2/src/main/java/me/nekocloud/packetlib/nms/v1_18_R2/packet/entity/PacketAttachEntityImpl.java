package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import lombok.Getter;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketAttachEntity;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutAttachEntity;

@Getter
public class PacketAttachEntityImpl extends DPacketEntityBase<PacketPlayOutAttachEntity, DEntity> implements PacketAttachEntity {

    private DEntity vehicle;

    public PacketAttachEntityImpl(DEntity entity, DEntity vehicle) {
        super(entity);
        this.vehicle = vehicle;
    }

    @Override
    public void setVehicle(DEntity vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    protected PacketPlayOutAttachEntity init() {
        return new PacketPlayOutAttachEntity(((DEntityBase)entity).getEntityNms(),
                ((DEntityBase)vehicle).getEntityNms());
    }
}
