//package me.nekocloud.chat.bukkit;
//
//import me.nekocloud.chat.packet.IgnoreListPacket;
//import me.nekocloud.core.io.packet.PacketProtocol;
//import org.bukkit.plugin.java.JavaPlugin;
//
//public class BukkitCoreChat extends JavaPlugin {
//
//	@Override
//	public void onEnable() {
//		PacketProtocol.BUKKIT.getPacketMapper().registerPacket(0x17, IgnoreListPacket.class);
//	}
//
//	@Override
//	public void onDisable() {
//		PacketProtocol.BUKKIT.getPacketMapper().unregisterPacket(0x17);
//	}
//}
