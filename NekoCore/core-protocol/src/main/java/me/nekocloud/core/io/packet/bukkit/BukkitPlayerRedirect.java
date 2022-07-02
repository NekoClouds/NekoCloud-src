package me.nekocloud.core.io.packet.bukkit;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.info.filter.ServerFilter;
import me.nekocloud.core.io.packet.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BukkitPlayerRedirect extends DefinedPacket {
    int playerID;
    String server;
    ServerFilter filter;

    //если фильтр - MAP_NAME
    String mapName;

    public BukkitPlayerRedirect(
            final int playerID,
            final String server,
            final ServerFilter filter
    ) {
        this.playerID = playerID;
        this.server = server;
        this.filter = filter;
    }

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);

        server = readString(buf);
        filter = readEnum(ServerFilter.class, buf);

        if (filter == ServerFilter.MAP_NAME)
            mapName = readString(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);

        writeString(server, buf);
        writeEnum(filter, buf);

        if (filter == ServerFilter.MAP_NAME)
            writeString(mapName, buf);
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
    public static class Error extends DefinedPacket {
        int playerID;
        String server;
        String reason;

        @Override
        public void read(final ByteBuf buf) {
            playerID = readVarInt(buf);

            server = readString(buf);
            reason = readString(buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeVarInt(playerID, buf);

            writeString(server, buf);
            writeString(reason, buf);
        }

        @Override
        public void handle(final PacketHandler handler) {
            handler.handle(this);
        }
    }
}
