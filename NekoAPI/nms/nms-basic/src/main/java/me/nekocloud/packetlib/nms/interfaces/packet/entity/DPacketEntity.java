package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;

public interface DPacketEntity<E extends DEntity> extends DPacket {

    E getEntity();

    void setEntity(E entity);
}
