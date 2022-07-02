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
public final class BungeeOnlineUpdate extends DefinedPacket {

    int online;

    @Override
    public void read(final ByteBuf buf) {
        online = readVarInt(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(online, buf);
    }

    @Override
    public void handle(final PacketHandler handler)  {
        handler.handle(this);
    }
}
