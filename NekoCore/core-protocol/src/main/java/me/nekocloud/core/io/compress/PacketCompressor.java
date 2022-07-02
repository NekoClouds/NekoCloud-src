package me.nekocloud.core.io.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import me.nekocloud.core.io.compress.zlib.CoreZlib;
import me.nekocloud.core.io.packet.DefinedPacket;

import java.util.zip.Deflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {

    private final CoreZlib zlib = CompressFactory.ZLIB.newInstance();

    @Setter
    private int threshold = 256;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        zlib.init(true, Deflater.DEFAULT_COMPRESSION);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        zlib.free();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int origSize = msg.readableBytes();
        
        if (origSize < threshold) {
            DefinedPacket.writeVarInt(0, out);
            out.writeBytes(msg);

        } else {

            DefinedPacket.writeVarInt(origSize, out);

            zlib.process(msg, out);
        }
    }
}
