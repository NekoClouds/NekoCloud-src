package me.nekocloud.core.io.packet.bungee;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BungeePlayerBossBar {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Create extends DefinedPacket {

        int playerId;
        /**
         * номер строки боссбара (с 0 по 4 (т.е. 5 боссбаров одновременно))
         */
        int row;
        String title;
//        private BossBarColor color;
//        private BossBarStyle style;
        float health;

        @Override
        public void read(final ByteBuf buf) {
            this.playerId = readVarInt(buf);
            this.row = readVarInt(buf);
            this.title = readString(buf);
//            this.color = readEnum(BossBarColor.class, buf);
//            this.style = readEnum(BossBarStyle.class, buf);
            this.health = buf.readFloat();
        }

        @Override
        public void write(final ByteBuf buf) {
            writeVarInt(playerId, buf);
            writeVarInt(row, buf);
            writeString(title, buf);
//            writeEnum(this.color, buf);
//            writeEnum(this.style, buf);
            buf.writeFloat(health);
        }

        @Override
        public void handle(final @NotNull PacketHandler handler)  {
            handler.handle(this);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    public final static class Remove extends DefinedPacket {
        int playerId;
        int row;

        @Override
        public void read(final ByteBuf buf) {
            playerId = readVarInt(buf);
            row = readVarInt(buf);
        }

        @Override
        public void write(final ByteBuf buf) {
            writeVarInt(playerId, buf);
            writeVarInt(row, buf);
        }

        @Override
        public void handle(final @NotNull PacketHandler handler)  {
            handler.handle(this);
        }
    }
}
