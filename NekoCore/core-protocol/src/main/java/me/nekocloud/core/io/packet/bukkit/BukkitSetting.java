package me.nekocloud.core.io.packet.bukkit;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public final class BukkitSetting extends DefinedPacket {
    int playerID;
    SettingsType settingsType;
    boolean flag;

    @Override
    public void read(final ByteBuf buf) {
        playerID = readVarInt(buf);
        settingsType = readEnum(SettingsType.class, buf);
        flag = readBoolean(buf);
    }

    @Override
    public void write(final ByteBuf buf) {
        writeVarInt(playerID, buf);
        writeEnum(settingsType, buf);
        writeBoolean(flag, buf);
    }

    @Override
    public void handle(final @NotNull PacketHandler handler) {
        handler.handle(this);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public final static class Lang extends DefinedPacket {
        int playerID;
        Language language;
        Language oldLanguage;

        @Override
        public void read(final ByteBuf buf) {
            playerID = readVarInt(buf);
            language = readEnum(Language.class, buf);
            oldLanguage = readEnum(Language.class, buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeVarInt(playerID, buf);
            writeEnum(language, buf);
            writeEnum(oldLanguage, buf);
        }

        @Override
        public void handle(final @NotNull PacketHandler handler) {
            handler.handle(this);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public static class Prefix extends DefinedPacket {
        int playerID;
        String prefix;

        @Override
        public void read(final ByteBuf buf) {
            playerID = readVarInt(buf);
            prefix = readString(buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeVarInt(playerID, buf);
            writeString(prefix, buf);
        }

        @Override
        public void handle(final @NotNull PacketHandler handler) {
            handler.handle(this);
        }
    }
}

