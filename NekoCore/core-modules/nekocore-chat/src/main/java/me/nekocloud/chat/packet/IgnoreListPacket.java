package me.nekocloud.chat.packet;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.*;
import lombok.experimental.FieldDefaults;
//import me.nekocloud.api.NekoCloud;
//import me.nekocloud.api.player.GamerManager;
//import me.nekocloud.chat.bukkit.CoreChatGuiManager;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class IgnoreListPacket extends DefinedPacket {

//    final GamerManager gamerManager = NekoCloud.getGamerManager();

    int playerID;
    IntList ignoreList;

    @Override
    public void read(ByteBuf buf) {
        playerID = buf.readInt();
        ignoreList = new IntArrayList();

        val size = buf.readInt();
        for (int i = 0; i < size; i++) ignoreList.add(buf.readInt());

//        CoreChatGuiManager.openIgnoreGui(gamerManager.getGamer(playerID), ignoreList);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(playerID);
        buf.writeInt(ignoreList.size());

        //return true;
        ignoreList.forEach(buf::writeInt);
    }

    @Override
    public void handle(PacketHandler handler) { }
}
