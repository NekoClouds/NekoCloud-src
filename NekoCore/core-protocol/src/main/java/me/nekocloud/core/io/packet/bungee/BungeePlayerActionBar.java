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
public final class BungeePlayerActionBar extends DefinedPacket {

    int playerID;
    String message;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        message = readString(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeString(message, buf);
    }

    @Override
    public void handle(PacketHandler handler)  {
        handler.handle(this);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public final static class Announce extends DefinedPacket {

        String message;

        @Override
        public void read(final ByteBuf buf) {
            message = readString(buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeString(message, buf);
        }

        @Override
        public void handle(final PacketHandler handler)  {
            handler.handle(this);
        }
    }
}
