package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketEntityTeleport;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.util.ReflectionUtils;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PacketEntityTeleportImpl extends DPacketEntityBase<PacketPlayOutEntityTeleport, DEntity>
        implements PacketEntityTeleport {

    private static final double Y = 0.8;

    private Version versionSender = Version.EMPTY;

    public PacketEntityTeleportImpl(DEntity entity) {
        super(entity);
    }

    @Override
    protected PacketPlayOutEntityTeleport init() {
        Location location = entity.getLocation();
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(((DEntityBase)entity).getEntityNms());

        if (this.versionSender.getProtocol() >= Version.V_1_11.getProtocol() - 1 && entity.hasPassenger())
            ReflectionUtils.setFieldValue(packet, "c", location.getY() + Y);
        if (this.versionSender.getProtocol() >= Version.V_1_9.getProtocol()
                && this.versionSender.getProtocol() <= Version.V_1_10.getProtocol()
                && !entity.hasPassenger())
            ReflectionUtils.setFieldValue(packet, "c", location.getY() - Y - 0.1);

        return packet;
    }

    @Override
    public void sendPacket(Player player) {
        fixLocation(player);
        super.sendPacket(player);
    }

    private void fixLocation(Player player) {
        //проверять с какой версии, фиксим баги мультиверсий
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        this.versionSender = Version.EMPTY;
        if (gamer == null)
            return;

        this.versionSender = gamer.getVersion();
    }
}
