package me.nekocloud.core.io.header;

import io.netty.buffer.ByteBuf;

public interface PacketHeader {

    void writePacketId(ByteBuf buf, int id);

    int readPacketId(ByteBuf buf);

    boolean isLengthVariable();

    int getLengthSize();

    int getLengthSize(int length);

    int readLength(ByteBuf in, int available);

    void writeLength(ByteBuf out, int length);
}
