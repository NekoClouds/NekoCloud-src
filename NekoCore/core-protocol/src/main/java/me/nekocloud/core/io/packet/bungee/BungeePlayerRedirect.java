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
public final class BungeePlayerRedirect extends DefinedPacket {

    int playerID;
    String server;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        server = readString(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeString(server, buf);
    }

    @Override
    public void handle(final PacketHandler handler)  {
        handler.handle(this);
    }
}
