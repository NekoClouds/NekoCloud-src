package me.nekocloud.core.io.packet.handshake;

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
public final class Handshake extends DefinedPacket {

    String name;
    String address;
    String password;
    int port;
    ConnectionType connectionType;

    @Override
    public void read(final ByteBuf buf) {
        name = readString(buf);
        address = readString(buf);
        password = readString(buf);
        port = readVarInt(buf);
        connectionType = readEnum(ConnectionType.class, buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeString(name, buf);
        writeString(address, buf);
        writeString(password, buf);
        writeVarInt(port, buf);
        writeEnum(connectionType, buf);
    }

    @Override
    public void handle(@NotNull PacketHandler handler) {
        handler.handle(this);
    }

    public enum ConnectionType {
        BUKKIT,
        BUNGEE;
    }
}
