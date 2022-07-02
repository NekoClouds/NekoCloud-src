package me.nekocloud.core.io.packet.bukkit;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.game.GameState;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.info.ServerInfo;
import me.nekocloud.core.io.info.ServerInfoType;
import me.nekocloud.core.io.info.fields.FieldsUtil;
import me.nekocloud.core.io.info.fields.ServerField;
import me.nekocloud.core.io.info.filter.ServerFilter;
import me.nekocloud.core.io.info.types.DefaultServerInfo;
import me.nekocloud.core.io.packet.DefinedPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//сука блять это не пакет, а летающий туда сюда костыль нахуй
public final class BukkitServerInfoFilter {

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public final static class Request extends DefinedPacket {

        UUID requestId;
        String regex;
        ServerFilter filter;
        //невсегда нужны эти два поля. В случае если чек по онлайну,
        //то возращает только 1 подходящий сервер или не возвращает вообще
        int limit;
        String mapName;
        ServerInfoType serverType;
        GameState state;

        @Override
        public void read(final ByteBuf buf) {
            requestId = readUUID(buf);

            regex = readString(buf);
            filter = readEnum(ServerFilter.class, buf);

            limit = readVarInt(buf);

            if (filter == ServerFilter.MAP_NAME)
                mapName = readString(buf);

            serverType = readEnum(ServerInfoType.class, buf);

            if (serverType == ServerInfoType.GAME)
                state = readEnum(GameState.class, buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeUUID(requestId, buf);

            writeString(regex, buf);
            writeEnum(filter, buf);

            writeVarInt(limit, buf);

            if (filter == ServerFilter.MAP_NAME)
                writeString(mapName, buf);

            writeEnum(serverType, buf);

            if (serverType == ServerInfoType.GAME)
                writeEnum(state, buf);
        }

        @Override
        public void handle(final @NotNull PacketHandler handler) {
            handler.handle(this);
        }
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public final static class Response extends DefinedPacket {

        UUID responseId;
        String regex;
        ServerFilter filter;
        List<ServerInfo> serversInfos;

        @Override
        public void read(final ByteBuf buf) {
            responseId = readUUID(buf);
            regex = readString(buf);
            filter = readEnum(ServerFilter.class, buf);

            serversInfos = new ArrayList<>();

            final int serversInfosSize = readVarInt(buf);
            for (int i = 0; i < serversInfosSize; i++) {
                val serverInfo = new DefaultServerInfo();
                serverInfo.setProtocolVersion(readVarInt(buf));

                final Map<ServerField, Object> fields = FieldsUtil.excludeFields(readVarInt(buf), buf);
                for (val entry : fields.entrySet()) {
                    serverInfo.addFieldValue(entry.getKey(), entry.getValue());
                }

                serversInfos.add(serverInfo);
            }
        }

        @Override
        public void write(final ByteBuf buf) {
            writeUUID(responseId, buf);
            writeString(regex, buf);
            writeEnum(filter, buf);


            for (ServerInfo serverInfo : serversInfos) {
                writeVarInt(serverInfo.getProtocolVersion(), buf);
                writeVarInt(serversInfos.size(), buf);
                FieldsUtil.includeFields(serverInfo.getFieldsValues(), buf);
            }
        }

        @Override
        public void handle(final @NotNull PacketHandler handler) {
            handler.handle(this);
        }
    }
}
