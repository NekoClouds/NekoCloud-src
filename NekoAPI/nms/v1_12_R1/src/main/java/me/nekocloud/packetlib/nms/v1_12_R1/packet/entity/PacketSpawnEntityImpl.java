package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import lombok.Getter;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketSpawnEntity;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.types.EntitySpawnType;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity;

@Getter
public class PacketSpawnEntityImpl extends DPacketEntityBase<PacketPlayOutSpawnEntity, DEntity>
        implements PacketSpawnEntity {

    private EntitySpawnType entitySpawnType;
    private int objectData;

    public PacketSpawnEntityImpl(DEntity entity, EntitySpawnType entitySpawnType, int objectData) {
        super(entity);
        this.entitySpawnType = entitySpawnType;
        this.objectData = objectData;
    }

    @Override
    public void setEntitySpawnType(EntitySpawnType entitySpawnType) {
        this.entitySpawnType = entitySpawnType;
        init();
    }

    @Override
    public void setObjectData(int objectData) {
        this.objectData = objectData;
        init();
    }

    @Override
    protected PacketPlayOutSpawnEntity init() {
        return new PacketPlayOutSpawnEntity(((DEntityBase)entity).getEntityNms(), entitySpawnType.getId(), objectData);
    }
}
