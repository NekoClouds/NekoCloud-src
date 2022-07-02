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
public final class BungeePlayerKick extends DefinedPacket {

    int playerID;
    String reason;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        reason = readString(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeString(reason, buf);
    }

    @Override
    public void handle(PacketHandler handler)  {
        handler.handle(this);
    }
}
