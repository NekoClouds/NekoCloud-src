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
public final class BukkitNetworking extends DefinedPacket {
    int playerID;
    Type networkingType;
    int typeValue;
    int value;

    public BukkitNetworking(
            final int playerID,
            final Type networkingType,
            final int value
    ) {
        this.playerID = playerID;
        this.networkingType = networkingType;
        this.value = value;
    }

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        networkingType = readEnum(Type.class, buf);

        if (networkingType == Type.KEYS)
            typeValue = readVarInt(buf);

        value = readVarInt(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeEnum(networkingType, buf);

        if (networkingType == Type.KEYS)
            writeVarInt(typeValue, buf);

        writeVarInt(value, buf);
    }

    @Override
    public void handle(final @NotNull PacketHandler handler)  {
        handler.handle(this);
    }

    public enum Type {
        KEYS,
        MONEY,
        EXP
    }
}
