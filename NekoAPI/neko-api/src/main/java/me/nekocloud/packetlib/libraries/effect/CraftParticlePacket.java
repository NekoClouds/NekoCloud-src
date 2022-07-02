package me.nekocloud.packetlib.libraries.effect;

import me.nekocloud.packetlib.libraries.effect.data.CraftParticleColor;
import me.nekocloud.packetlib.libraries.effect.data.CraftParticleData;
import me.nekocloud.packetlib.libraries.effect.data.CraftParticleOrdinaryColor;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.interfaces.packet.world.PacketWorldParticles;
import me.nekocloud.api.effect.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class CraftParticlePacket {

    private static final PacketContainer PACKET_CONTAINER = NmsAPI.getManager().getPacketContainer();

    private final ParticleEffect effect;
    private float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final float speed;
    private final int amount;
    private final boolean longDistance;
    private final CraftParticleData data;
    private PacketWorldParticles packet;

    public CraftParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed,
                               int amount, boolean longDistance, CraftParticleData data) {
        this.effect = effect;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed < 0 ? 1 : speed;
        this.amount = amount < 0 ? 1 : amount;
        this.longDistance = longDistance;
        this.data = data;
    }

    public CraftParticlePacket(ParticleEffect effect, Vector direction, float speed, boolean longDistance, CraftParticleData data) {
        this(effect, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), speed, 0, longDistance, data);
    }

    public CraftParticlePacket(ParticleEffect effect, CraftParticleColor color, boolean longDistance) {
        this(effect, color.getValueX(), color.getValueY(), color.getValueZ(), 1, 0, longDistance, null);
        if (effect == ParticleEffect.REDSTONE
                && color instanceof CraftParticleOrdinaryColor
                && ((CraftParticleOrdinaryColor) color).getRed() == 0) {
            offsetX = Float.MIN_NORMAL;
        }
    }

    private void initPacket(Location center) {
        packet = PACKET_CONTAINER.getWorldParticlesPacket(effect, longDistance, center,
                offsetX, offsetY, offsetZ, speed, amount);
        if (data == null)
            return;

        int[] packetData = data.getPacketData();
        packet.setData(packetData);
    }

    public void sendTo(Location center, double range) {
        initPacket(center);
        if (range < 1)
            range = 1;

        String worldName = center.getWorld().getName();
        double squared = range * range;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().getName().equals(worldName)
                    || player.getLocation().distanceSquared(center) > squared)
                continue;
            sendTo(center, player);
        }
    }

    public void sendTo(Location center) {
        initPacket(center);
        Bukkit.getOnlinePlayers().forEach(pl -> sendTo(center, pl));
    }

    public void sendTo(Location center, Player player) {
        initPacket(center);
        packet.sendPacket(player);
    }

    public void sendTo(Location center, Player... players) {
        initPacket(center);
        packet.sendPacket(players);
    }

    public void sendTo(Location center, List<Player> players) {
        initPacket(center);
        players.forEach(player -> sendTo(center, player));
    }
}
