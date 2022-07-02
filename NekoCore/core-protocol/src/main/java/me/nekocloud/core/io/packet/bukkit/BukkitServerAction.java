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
public final class BukkitServerAction extends DefinedPacket {

    Action action;

    @Override
    public void read(final ByteBuf buf) {
        action = readEnum(Action.class, buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeEnum(action, buf);
    }

    @Override
    public void handle(final @NotNull PacketHandler handler)  {
        handler.handle(this);
    }

    public enum Action {
        RESTART,
        LANG_RELOAD
    }
}
