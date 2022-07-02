package me.nekocloud.packetlib.nms.interfaces.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.types.EntitySpawnType;

public interface PacketSpawnEntity extends DPacketEntity<DEntity> {

    EntitySpawnType getEntitySpawnType();

    void setEntitySpawnType(EntitySpawnType entitySpawnType);

    int getObjectData();

    void setObjectData(int objectData);
}
