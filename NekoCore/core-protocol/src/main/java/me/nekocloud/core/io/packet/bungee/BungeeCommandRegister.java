package me.nekocloud.core.io.packet.bungee;

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
public final class BungeeCommandRegister extends DefinedPacket {

    // чтобы не кидались постоянно пакеты с командами с банжи,
    // будут только те, которые нужны для модулей кора
    String command;
    String[] aliases;
    Action action;

    @Override
    public void read(final ByteBuf buf) {
        command = readString(buf);
        aliases = readArray(buf, String[]::new, DefinedPacket::readString);
        action = readEnum(Action.class, buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeString(command, buf);
        writeArray(aliases, buf, DefinedPacket::writeString);
        writeEnum(action, buf);
    }

    @Override
    public void handle(final PacketHandler handler)  {
        handler.handle(this);
    }

    public enum Action {
        REGISTER,
        UNREGISTER;
    }
}
