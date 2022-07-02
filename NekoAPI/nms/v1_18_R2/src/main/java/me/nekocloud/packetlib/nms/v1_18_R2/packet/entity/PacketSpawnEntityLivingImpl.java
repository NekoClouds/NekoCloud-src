package me.nekocloud.packetlib.nms.v1_12_R1.packet.entity;

import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketSpawnEntityLiving;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityArmorStand;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.packetlib.nms.util.ReflectionUtils;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.DEntityLivingBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PacketSpawnEntityLivingImpl extends DPacketEntityBase<PacketPlayOutSpawnEntityLiving, DEntityLiving>
        implements PacketSpawnEntityLiving {

    private static final double Y = 0.8;

    private Version versionSender = Version.EMPTY;

    public PacketSpawnEntityLivingImpl(DEntityLiving entity) {
        super(entity);
    }

    @Override
    protected PacketPlayOutSpawnEntityLiving init() {
        Location location = entity.getLocation();

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(
                ((DEntityLivingBase)entity).getEntityNms());

        if (this.versionSender.getProtocol() >= Version.V_1_11.getProtocol() - 1 && entity.hasPassenger())
            ReflectionUtils.setFieldValue(packet, "e", location.getY() + Y);
        if (this.versionSender.getProtocol() >= Version.V_1_9.getProtocol()
                && this.versionSender.getProtocol() <= Version.V_1_10.getProtocol()
                && !entity.hasPassenger()) {
            if (!(entity instanceof DEntityArmorStand))
                ReflectionUtils.setFieldValue(packet, "e", location.getY() - Y - 0.1);

            DEntityArmorStand entityArmorStand = (DEntityArmorStand) entity;
            if (entityArmorStand.isInvisible())
                ReflectionUtils.setFieldValue(packet, "e", location.getY() - Y - 0.1);
        }


        return packet;
    }

    @Override
    public void sendPacket(Player player) {
        fixLocation(player);
        super.sendPacket(player);
    }

    private void fixLocation(Player player) {
        //проверять с какой версии
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        this.versionSender = Version.EMPTY;
        if (gamer == null)
            return;

        this.versionSender = gamer.getVersion();
    }
}
