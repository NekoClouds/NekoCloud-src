package me.nekocloud.core.io.packet.bukkit;

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
public final class BukkitGroupPacket extends DefinedPacket {
    int playerID;
    int groupLevel;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        groupLevel = readVarInt(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeVarInt(groupLevel, buf);
    }

    @Override
    public void handle(final @NotNull PacketHandler handler)  {
        handler.handle(this);
    }
}
