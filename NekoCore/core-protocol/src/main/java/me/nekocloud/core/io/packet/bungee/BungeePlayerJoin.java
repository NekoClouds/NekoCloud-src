package me.nekocloud.core.io.packet.bungee;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BungeePlayerJoin extends DefinedPacket {

    int playerID;
    String player;
    String ip;
    String server;
    int protocolVersion;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        player = readString(buf);
        ip = readString(buf);
        server = readString(buf);
        protocolVersion = readVarInt(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeString(player, buf);
        writeString(ip, buf);
        writeString(server, buf);
        writeVarInt(protocolVersion, buf);
    }

    @Override
    public void handle(final PacketHandler handler) {
        handler.handle(this);
    }
}
