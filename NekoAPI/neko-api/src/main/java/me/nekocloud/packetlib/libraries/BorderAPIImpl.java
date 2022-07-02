package me.nekocloud.packetlib.libraries;

import me.nekocloud.api.BorderAPI;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.DWorldBorder;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.types.WorldBorderActionType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BorderAPIImpl implements BorderAPI {

    private final NmsManager nmsManager = NmsAPI.getManager();
    private final PacketContainer container = NmsAPI.getManager().getPacketContainer();

    @Override
    public void sendRedScreen(Player player) {
        sendRedScreen(player, 5L);
    }

    @Override
    public void sendRedScreen(Player player, long time) {
        sendRedScreen(player, time, 20);
    }

    @Override
    public void sendRedScreen(Player player, long time, int percentage) {
        int dist = -10000 * percentage + 1300000;
        sendWorldBorderPacket(player, dist, 200000D, 0);
        sendWorldBorderPacket(player, 0, (double) dist, (long) 1000 * time + 4000);
    }

    @Override
    public void sendBoard(Player player, Location center, double size) {
        DWorldBorder border = nmsManager.createBorder(center.getWorld());
        border.setRadius(size);
        border.setCenter(center);

        container.sendWorldBorderPacket(player, border, WorldBorderActionType.INITIALIZE);
    }

    @Override
    public void removeBoard(Player player) {
        World world = player.getWorld();
        DWorldBorder border = nmsManager.createBorder(world);

        container.sendWorldBorderPacket(player, border, WorldBorderActionType.INITIALIZE);
    }

    private void sendWorldBorderPacket(Player player, int dist, double newRadius, long delay) {
        World world = player.getWorld();
        DWorldBorder border = nmsManager.createBorder(world);

        border.setWarningDistance(dist);
        border.setCenter(player.getLocation());
        border.setWarningTime(15);
        border.transitionSizeBetween(200000D, newRadius, delay);



        container.sendWorldBorderPacket(player, border, WorldBorderActionType.INITIALIZE);
    }
}
