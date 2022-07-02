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
public final class BukkitPlayerDispatchCommand extends DefinedPacket {
    int playerID;
    String command;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        command = readString(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeString(command, buf);
    }

    @Override
    public void handle(final PacketHandler handler)  {
        handler.handle(this);
    }
}
