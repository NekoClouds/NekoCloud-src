package me.nekocloud.friends.packet;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.util.pair.Pair;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.friends.bukkit.manager.FriendsGuiManager;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FriendsListPacket extends DefinedPacket {

    final GamerManager gamerManager = NekoCloud.getGamerManager();

    int playerID;
    IntSet friends;

    @Override
    public void read(ByteBuf buf) {
        this.playerID = buf.readInt();
        val size = buf.readInt();

        Int2ObjectMap<Pair<Long, String>> data = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < size; i++)
            data.put(buf.readInt(), Pair.of(buf.readLong(), readString(buf)));

        FriendsGuiManager.openFriendsGui(gamerManager.getGamer(this.playerID), data);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.playerID);
        buf.writeInt(this.friends.size());

        friends.forEach((id) -> {
            val player = NekoCore.getInstance().getOfflinePlayer(id);

            buf.writeInt(id);
            buf.writeLong(player.isOnline() ? -1 : player.getLastOnline());

            writeString(player.isOnline() && player.getBukkit()
                    != null ? player.getBukkit().getName() :
                    player.getOfflineData().getLastServer().getName(), buf);
        });
    }

    @Override
    public void handle(PacketHandler handler) {
    }
}
