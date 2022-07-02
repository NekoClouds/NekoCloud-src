package me.nekocloud.core.io.compress;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.nekocloud.core.io.compress.zlib.CoreZlib;
import me.nekocloud.core.io.packet.DefinedPacket;

import java.util.List;

public class PacketDecompressor extends MessageToMessageDecoder<ByteBuf> {

    private final CoreZlib zlib = CompressFactory.ZLIB.newInstance();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        zlib.init(false, 0);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        zlib.free();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = DefinedPacket.readVarInt(in);

        if (size == 0) {
            out.add(in.slice().retain());
            in.skipBytes(in.readableBytes());

        } else {
            ByteBuf decompressed = ctx.alloc().directBuffer();

            try {
                zlib.process(in, decompressed);
                Preconditions.checkState(decompressed.readableBytes() == size, "Decompressed packet size mismatch");

                out.add(decompressed);
                decompressed = null;

            } finally {

                if (decompressed != null) {
                    decompressed.release();
                }
            }
        }
    }
}
