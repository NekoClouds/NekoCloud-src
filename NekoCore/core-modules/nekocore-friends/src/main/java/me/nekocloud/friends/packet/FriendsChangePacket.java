package me.nekocloud.friends.packet;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.friends.Friend;
import me.nekocloud.base.gamer.friends.FriendAction;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.entity.BukkitFriend;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FriendsChangePacket extends DefinedPacket {

    final GamerManager gamerManager = NekoCloud.getGamerManager();

    int playerID;
    int friendID;
    FriendAction friendAction;

    @Override
    public void read(ByteBuf buf) {
        playerID = buf.readInt();
        friendID = buf.readInt();
        friendAction = readEnum(FriendAction.class, buf);

        val gamer = gamerManager.getGamer(playerID);
        if (gamer != null && gamer.isOnline())
            gamer.changeFriend(friendAction, new BukkitFriend(friendID));
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(playerID);
        buf.writeInt(friendID);

        writeEnum(friendAction, buf);
    }

    @Override
    public void handle(PacketHandler handler) {
    }
}
