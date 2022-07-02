package me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer;

import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketBed;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityPlayerImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entity.DPacketEntityBase;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBed;
import org.bukkit.Location;

public class PacketBedImpl extends DPacketEntityBase<PacketPlayOutBed, DEntityPlayer>
        implements PacketBed {

    private Location bedLocation;

    public PacketBedImpl(DEntityPlayer entity, Location bedLocation) {
        super(entity);
        this.bedLocation = bedLocation;
    }

    @Override
    protected PacketPlayOutBed init() {
        return new PacketPlayOutBed(((DEntityPlayerImpl)entity).getEntityNms(),
                new BlockPosition(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ()));
    }
}
