package me.nekocloud.friends.bukkit;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.friends.bukkit.gui.FriendsGui;
import me.nekocloud.friends.bukkit.gui.FriendsRequestsGui;
import me.nekocloud.friends.packet.FriendsChangePacket;
import me.nekocloud.friends.packet.FriendsListPacket;
import me.nekocloud.friends.packet.FriendsRequestListPacket;
import me.nekocloud.core.io.packet.PacketProtocol;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitFriends extends JavaPlugin {

    private final GuiManager<AbstractGui<?>> guiManager = NekoCloud.getGuiManager();

    @Override
    public void onEnable() {
        val packetMapper = PacketProtocol.BUKKIT.getPacketMapper();
        packetMapper.registerPacket(0x14, FriendsListPacket.class);
        packetMapper.registerPacket(0x15, FriendsRequestListPacket.class);
        packetMapper.registerPacket(0x16, FriendsChangePacket.class);

        guiManager.createGui(FriendsGui.class);
        guiManager.createGui(FriendsRequestsGui.class);
    }

    @Override
    public void onDisable() {
        val packetMapper = PacketProtocol.BUKKIT.getPacketMapper();
        packetMapper.unregisterPacket(0x14);
        packetMapper.unregisterPacket(0x15);
        packetMapper.unregisterPacket(0x16);

        guiManager.removeGui(FriendsGui.class);
        guiManager.removeGui(FriendsRequestsGui.class);
    }
}
