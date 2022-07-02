package me.nekocloud.friends.packet;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.friends.bukkit.manager.FriendsGuiManager;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FriendsRequestListPacket extends DefinedPacket {

    final GamerManager gamerManager = NekoCloud.getGamerManager();

    int playerID;
    IntSet requests;

    @Override
    public void read(ByteBuf buf) {
        playerID = buf.readInt();
        val size = buf.readInt();

        requests = new IntOpenHashSet();
        for (int i = 0; i < size; i++)
            requests.add(buf.readInt());

        FriendsGuiManager.openFriendsRequestsGui(gamerManager.getGamer(this.playerID), this.requests);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.playerID);
        buf.writeInt(this.requests.size());

        requests.forEach(buf::writeInt);
    }

    @Override
    public void handle(PacketHandler handler) {
    }
}
