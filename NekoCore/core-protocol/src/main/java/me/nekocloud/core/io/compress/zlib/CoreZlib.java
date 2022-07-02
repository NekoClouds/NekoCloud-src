package me.nekocloud.core.io.compress.zlib;

import io.netty.buffer.ByteBuf;

import java.util.zip.DataFormatException;

public interface CoreZlib {

    void init(boolean compress, int level);

    void free();

    void process(ByteBuf in, ByteBuf out) throws DataFormatException;
}
