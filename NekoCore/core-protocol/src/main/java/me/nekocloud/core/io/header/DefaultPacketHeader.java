package me.nekocloud.core.io.header;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class DefaultPacketHeader implements PacketHeader {

    @Override
    public boolean isLengthVariable() {
        return true;
    }

    @Override
    public int getLengthSize() {
        return 3;
    }

    @Override
    public int getLengthSize(int length) {
        if((length & -128) == 0) {
            return 1;
        } else if((length & -16384) == 0) {
            return 2;
        } else if((length & -2097152) == 0) {
            return 3;
        } else if((length & -268435456) == 0) {
            return 4;
        } else {
            return 5;
        }
    }

    @Override
    public int readLength(@NotNull ByteBuf in, int available)  {
        return in.readInt();
    }

    @Override
    public void writeLength(@NotNull ByteBuf out, int length)  {
        out.writeInt(length);
    }

    @Override
    public int readPacketId(@NotNull ByteBuf in)  {
        return in.readInt();
    }

    @Override
    public void writePacketId(@NotNull ByteBuf out, int packetId)  {
        out.writeInt(packetId);
    }
}
