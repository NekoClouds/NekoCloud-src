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
public final class BungeePlayerTitle extends DefinedPacket {

    int playerID;

    Action action;

    // TITLE & SUBTITLE
    String text;

    // TIMES
    int fadeIn;
    int stay;
    int fadeOut;


    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);

        writeEnum(action, buf);
        switch (action) {
            case TITLE, SUBTITLE, ACTIONBAR -> writeString(text, buf);
            case TIMES -> {
                writeVarInt(fadeIn, buf);
                writeVarInt(stay, buf);
                writeVarInt(fadeOut, buf);
            }
        }
    }

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);

        action = readEnum(Action.class, buf);

        switch (action) {
            case TITLE, SUBTITLE, ACTIONBAR -> text = readString(buf);
            case TIMES -> {
                fadeIn = readVarInt(buf);
                stay = readVarInt(buf);
                fadeOut = readVarInt(buf);
            }
        }
    }

    @Override
    public void handle(final PacketHandler handler) {
        handler.handle(this);
    }

    public enum Action {

        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET
    }
}
