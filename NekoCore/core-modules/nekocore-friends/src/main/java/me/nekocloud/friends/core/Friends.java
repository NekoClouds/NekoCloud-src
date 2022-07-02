package me.nekocloud.friends.core;

import lombok.Getter;
import me.nekocloud.base.gamer.sections.FriendsSection;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.friends.core.commands.FriendsCommand;
import me.nekocloud.friends.core.listeners.PlayerListener;
import me.nekocloud.friends.core.request.RequestManager;
import me.nekocloud.friends.packet.FriendsChangePacket;
import me.nekocloud.friends.packet.FriendsListPacket;
import me.nekocloud.friends.packet.FriendsRequestListPacket;
import me.nekocloud.core.io.packet.PacketMapper;
import me.nekocloud.core.io.packet.PacketProtocol;

@CoreModuleInfo(name = "CoreFriends", author = "_Novit_")
public class Friends extends CoreModule {

    @Getter
    private RequestManager requestManager;

    @Override
    public void onEnable() {
        this.requestManager = new RequestManager(this);
        PacketMapper packetMapper = PacketProtocol.BUKKIT.getPacketMapper();
        packetMapper.registerPacket(0x14, FriendsListPacket.class);
        packetMapper.registerPacket(0x15, FriendsRequestListPacket.class);
        packetMapper.registerPacket(0x16, FriendsChangePacket.class);

        getCore().getEventManager().register(new PlayerListener(this));
        getCore().getCommandManager().registerCommand(new FriendsCommand(this));
        getCore().getTaskScheduler().runAsync(this, () ->
                getCore().getPlayerManager().getCorePlayerMap().valueCollection().forEach(player -> {
                    if (player.getSection(FriendsSection.class) == null) {
                        player.getSection(FriendsSection.class).loadData();
                    }
        }));
    }

    @Override
    public void onDisable() {
        PacketMapper packetMapper = PacketProtocol.BUKKIT.getPacketMapper();
        packetMapper.unregisterPacket(0x14);
        packetMapper.unregisterPacket(0x15);
        packetMapper.unregisterPacket(0x16);
    }
}
