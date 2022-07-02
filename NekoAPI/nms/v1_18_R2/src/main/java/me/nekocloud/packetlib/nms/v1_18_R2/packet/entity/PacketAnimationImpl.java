package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import lombok.Getter;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketAnimation;
import me.nekocloud.api.entity.npc.AnimationNpcType;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;

@Getter
public class PacketAnimationImpl extends DPacketEntityBase<PacketPlayOutAnimation, DEntity> implements PacketAnimation {

    private AnimationNpcType animation;

    public PacketAnimationImpl(DEntity entity, AnimationNpcType animation) {
        super(entity);
        this.animation = animation;
    }

    @Override
    public void setAnimation(AnimationNpcType animation) {
        this.animation = animation;
        init();
    }

    @Override
    protected PacketPlayOutAnimation init() {
        return new PacketPlayOutAnimation(((DEntityBase)entity).getEntityNms(), animation.ordinal());
    }
}
