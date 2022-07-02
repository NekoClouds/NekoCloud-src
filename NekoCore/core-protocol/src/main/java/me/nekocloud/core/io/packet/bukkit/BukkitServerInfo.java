package me.nekocloud.core.io.packet.bukkit;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.info.fields.FieldsUtil;
import me.nekocloud.core.io.info.fields.ServerField;
import me.nekocloud.core.io.packet.DefinedPacket;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BukkitServerInfo extends DefinedPacket {

    String serverName;
    int protocolVersion;
    Map<ServerField, Object> fields;

    @Override
    public void read(final ByteBuf buf) {
        serverName = readString(buf);
        protocolVersion = readVarInt(buf);
        fields = FieldsUtil.excludeFields(buf.readInt(), buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeString(serverName, buf);
        writeVarInt(protocolVersion, buf);
        writeVarInt(fields.size(), buf);
        FieldsUtil.includeFields(fields, buf);
    }

    @Override
    public void handle(final PacketHandler handler) {
        handler.handle(this);
    }
}
