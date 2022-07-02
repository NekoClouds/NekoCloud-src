package me.nekocloud.core.io.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.core.io.packet.bukkit.*;
import me.nekocloud.core.io.packet.bungee.*;
import me.nekocloud.core.io.packet.handshake.Handshake;

@AllArgsConstructor
@Getter
public enum PacketProtocol {

    HANDSHAKE() {{
        mapper.registerPacket(0x00, Handshake.class);
    }},
    BUNGEE() {{
        mapper.registerPacket(0, BungeeServerAction.class);
        mapper.registerPacket(1, BungeeOnlineUpdate.class);
        mapper.registerPacket(2, BungeeCommandRegister.class);
        mapper.registerPacket(3, BungeeCommandExecute.class);
        mapper.registerPacket(4, BungeePlayerLogin.class);
        mapper.registerPacket(5, BungeePlayerLogin.Result.class);
        mapper.registerPacket(6, BungeePlayerJoin.class);
        mapper.registerPacket(7, BungeePlayerDisconnect.class);
        mapper.registerPacket(8, BungeePlayerSwitchServer.class);
        mapper.registerPacket(9, BungeePlayerMessage.class);
        mapper.registerPacket(10, BungeePlayerMessage.Announce.class);
        mapper.registerPacket(11, BungeePlayerTitle.class);
        mapper.registerPacket(12, BungeePlayerActionBar.class);
        mapper.registerPacket(13, BungeePlayerActionBar.Announce.class);
        mapper.registerPacket(14, BungeePlayerDispatchCommand.class);
        mapper.registerPacket(15, BungeePlayerKick.class);
        mapper.registerPacket(16, BungeePlayerRedirect.class);
        // 17, 18, 19 заняты модулями
    }},
    BUKKIT() {{
        mapper.registerPacket(0, BukkitCommandExecute.class);
        mapper.registerPacket(1, BukkitGroupPacket.class);
        mapper.registerPacket(2, BukkitNetworking.class);
        mapper.registerPacket(3, BukkitOnlineFetch.class);
        mapper.registerPacket(4, BukkitOnlineFetch.Response.class);
        mapper.registerPacket(5, BukkitPlaySound.class);
        mapper.registerPacket(6, BukkitSetting.class);
        mapper.registerPacket(7, BukkitPlayerDispatchCommand.class);
        mapper.registerPacket(8, BukkitPlayerRedirect.class);
        mapper.registerPacket(9, BukkitPlayerRedirect.Error.class);
        mapper.registerPacket(10, BukkitServerInfo.class);
        mapper.registerPacket(11, BukkitServerInfoFilter.Response.class);
        mapper.registerPacket(12, BukkitServerInfoFilter.Request.class);
        mapper.registerPacket(13, BukkitServerAction.class);
        // 14 15 16 17 заняты модулями
    }};

    final PacketMapper mapper = new PacketMapper();
}
