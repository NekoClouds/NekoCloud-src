package me.nekocloud.core.io.packet;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.exception.OverflowPacketException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@RequiredArgsConstructor
public abstract class DefinedPacket {

    public static void writeString(final @NotNull String s, final ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format(
                    "Cannot send string longer than Short.MAX_VALUE (got %s characters)",
                    s.length()));
        }

        final byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static <T> void writeArray(
            final @NotNull T @NotNull [] array,
            final @NotNull ByteBuf buf,
            final @NotNull BiConsumer<T, ByteBuf> dataWriter
    ) {
        writeVarInt(array.length, buf);
        for (final T value : array)
            dataWriter.accept(value, buf);
    }

    public static void writeBoolean(final boolean b, final ByteBuf buf) {
        writeVarInt(b ? 1 : 0, buf);
    }

    public static boolean readBoolean(final ByteBuf buf) {
        return readVarInt(buf) == 1;
    }

    public static <T> T[] readArray(
            final @NotNull ByteBuf buf,
            final @NotNull Function<Integer, T[]> arrayCreator,
            final @NotNull Function<ByteBuf, T> dataReader
    ) {
        final int length = readVarInt(buf);
        final T[] array = arrayCreator.apply(length);
        for (int i = 0; i < length; i++)
            array[i] = dataReader.apply(buf);

        return array;
    }

    @Contract("_, _ -> new")
    public static @NotNull String readString(ByteBuf buf, @NotNull ToIntFunction<ByteBuf> sizeSupplier) {
        int len = sizeSupplier.applyAsInt(buf);
        if (len > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format(
                    "Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        final byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    @Contract("_ -> new")
    public static @NotNull String readString(ByteBuf buf) {
        return readString(buf, DefinedPacket::readVarInt);
    }

    public static void writeArray(byte @NotNull [] b, ByteBuf buf) {
        if (b.length > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format(
                    "Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)", b.length));
        }
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static byte @NotNull [] toArray(@NotNull ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);

        return ret;
    }

    public static byte @NotNull [] readArray(ByteBuf buf) {
        return readArray(buf, buf.readableBytes());
    }

    public static byte @NotNull [] readArray(ByteBuf buf, int limit) {
        int len = readVarInt(buf);
        if (len > limit) {
            throw new OverflowPacketException(
                    String.format("Cannot receive byte array longer than %s (got %s bytes)", limit,
                            len));
        }
        byte[] ret = new byte[len];
        buf.readBytes(ret);
        return ret;
    }

    public static void writeStringArray(@NotNull List<String> s, ByteBuf buf) {
        writeVarInt(s.size(), buf);
        for (String str : s) {
            writeString(str, buf);
        }
    }

    public static @NotNull List<String> readStringArray(final ByteBuf buf) {
        final int len = readVarInt(buf);
        final List<String> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            ret.add(readString(buf));
        }

        return ret;
    }

    public static int readVarInt(final ByteBuf input) {
        return readVarInt(input, 5);
    }

    public static int readVarInt(final @NotNull ByteBuf input, final int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static void writeVarInt(int value, final ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeEnum(@NotNull Enum<?> en, ByteBuf buf) {
        writeVarInt(en.ordinal(), buf);
    }

    public static <T> T readEnum(@NotNull Class<T> clazz, ByteBuf buf) {
        return clazz.getEnumConstants()[readVarInt(buf)];
    }

    public static int readVarShort(@NotNull ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0) {
            buf.writeByte(high);
        }
    }

    public static void writeVarLong(long value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    public static long readVarLong(@NotNull ByteBuf buf) {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= ((long) value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void writeUUID(final @NotNull UUID value, final @NotNull ByteBuf output) {
        output.writeLong(value.getMostSignificantBits());
        output.writeLong(value.getLeastSignificantBits());
    }

    @Contract("_ -> new")
    public static @NotNull UUID readUUID(final @NotNull ByteBuf input) {
        return new UUID(input.readLong(), input.readLong());
    }

    public void read(final ByteBuf buf) {
        throw new UnsupportedOperationException("Packet must implement read method");
    }

    public void write(final ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Packet must implement write method");
    }
    public void handle(final PacketHandler handler) {}

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
