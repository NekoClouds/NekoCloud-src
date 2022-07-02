package me.nekocloud.packetlib.libraries;

import me.nekocloud.api.JSONMessageAPI;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.types.ChatMessageType;
import me.nekocloud.base.util.JsonBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public class JSONMessageAPIImpl implements JSONMessageAPI {

    private final PacketContainer container = NmsAPI.getManager().getPacketContainer();

    @Override
    public void send(Player player, String json) {
        container.sendChatPacket(player, json, ChatMessageType.SYSTEM);
    }

    @Override
    public void send(Player player, JsonBuilder jsonBuilder) {
        send(player, jsonBuilder.toString());
    }

    @Override
    public void send(Player player, BaseComponent... components) {
        player.spigot().sendMessage(components);
    }
}
