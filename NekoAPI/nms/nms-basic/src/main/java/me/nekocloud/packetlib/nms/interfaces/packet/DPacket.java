package me.nekocloud.packetlib.nms.interfaces.packet;

import org.bukkit.entity.Player;

import java.util.Arrays;

public interface DPacket {

    void sendPacket(Player player);

    default void sendPacket(Player... players) {
        Arrays.asList(players).forEach(this::sendPacket);
    }

    @Override
    String toString();
}
