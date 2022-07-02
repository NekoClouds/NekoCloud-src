package me.nekocloud.core.io.packet.bungee;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BungeeServerAction extends DefinedPacket {

    Action action;
    String server;
    String ip;
    int port;

    @Override
    public void read(final ByteBuf buf) {
        action = readEnum(Action.class, buf);
        server = readString(buf);
        ip = readString(buf);
        port = readVarInt(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeEnum(action, buf);
        writeString(server, buf);
        writeString(ip, buf);
        writeVarInt(port, buf);
    }

    @Override
    public void handle(final @NotNull PacketHandler handler)  {
        handler.handle(this);
    }

    public enum Action {
        ADD,
        REMOVE;
    }
}
