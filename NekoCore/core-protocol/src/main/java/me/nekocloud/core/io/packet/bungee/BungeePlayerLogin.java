package me.nekocloud.core.io.packet.bungee;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;

import java.net.InetSocketAddress;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BungeePlayerLogin extends DefinedPacket {

    String playerName;
    InetSocketAddress virtualHost;

    @Override
    public void read(final ByteBuf buf) {
        playerName = readString(buf);
        virtualHost = new InetSocketAddress(readString(buf), 0);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeString(playerName, buf);
        writeString(virtualHost.getHostName(), buf);
    }

    @Override
    public void handle(final PacketHandler handler) {
        handler.handle(this);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public static class Result extends DefinedPacket {

        String player;
        boolean allowed;
        String cancelReason;

        @Override
        public void read(final ByteBuf buf) {
            player = DefinedPacket.readString(buf);
            allowed = readBoolean(buf);

            if (!allowed)
                cancelReason = readString(buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeString(player, buf);
            writeBoolean(allowed, buf);

            if (!allowed)
                writeString(cancelReason, buf);
        }

        @Override
        public void handle(PacketHandler handler)  {
            handler.handle(this);
        }
    }
}
