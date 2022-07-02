package me.nekocloud.core.io.packet.bukkit;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.SoundType;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BukkitPlaySound extends DefinedPacket {

    boolean targeted;

    int playerID;
    SoundType soundType;
    float volume;
    float pitch;

    @Override
    public void read(final ByteBuf buf) {
        targeted = readBoolean(buf);
        if (targeted)
            playerID = readVarInt(buf);

        soundType = readEnum(SoundType.class, buf);
        volume = buf.readFloat();
        pitch = buf.readFloat();
    }

    @Override
    public void write(final ByteBuf buf) {
        writeBoolean(targeted, buf);
        if (targeted)
            writeVarInt(playerID, buf);
        writeEnum(soundType, buf);

        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    public void handle(final @NotNull PacketHandler handler)  {
        handler.handle(this);
    }
}
