package me.nekocloud.core.io.packet.bukkit;

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
public final class BukkitOnlineFetch extends DefinedPacket {
    String regex;

    @Override
    public void read(final ByteBuf buf) {
        regex = readString(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeString(regex, buf);
    }

    @Override
    public void handle(final PacketHandler handler)  {
        handler.handle(this);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public static class Response extends DefinedPacket {

        String regex;
        int online;

        @Override
        public void read(final ByteBuf buf) {
            regex = readString(buf);
            online = readVarInt(buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeString(regex, buf);
            writeVarInt(online, buf);
        }

        @Override
        public void handle(final PacketHandler handler)  {
            handler.handle(this);
        }
    }
}
