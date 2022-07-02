package me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer;

import lombok.Getter;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketPlayerInfo;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.types.PlayerInfoActionType;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityPlayerImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entity.DPacketEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;

@Getter
public class PacketPlayerInfoImpl extends DPacketEntityBase<PacketPlayOutPlayerInfo, DEntityPlayer>
        implements PacketPlayerInfo {

    private PlayerInfoActionType actionType;

    public PacketPlayerInfoImpl(DEntityPlayer entity, PlayerInfoActionType actionType) {
        super(entity);
        this.actionType = actionType;
    }

    @Override
    protected PacketPlayOutPlayerInfo init() {
        return new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.valueOf(actionType.name()),
                ((DEntityPlayerImpl)entity).getEntityNms());
    }

    @Override
    public void setPlayerInfoAction(PlayerInfoActionType actionType) {
        this.actionType = actionType;
        init();
    }

}
