package me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer;

import lombok.Getter;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketCamera;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.DPacketBase;
import net.minecraft.server.v1_12_R1.PacketPlayOutCamera;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
public class PacketCameraImpl extends DPacketBase<PacketPlayOutCamera> implements PacketCamera {

    private Player player;

    public PacketCameraImpl(Player player) {
        this.player = player;
    }

    @Override
    protected PacketPlayOutCamera init() {
        return new PacketPlayOutCamera(((CraftPlayer)player).getHandle());
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
        init();
    }
}
