package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;

public interface PacketAttachEntity extends DPacketEntity<DEntity> {

    void setVehicle(DEntity vehicle);

    DEntity getVehicle();
}
