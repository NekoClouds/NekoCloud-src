package me.nekocloud.core.rcon.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.val;

import java.util.List;

public final class FramingHandler extends ByteToMessageCodec<ByteBuf> {

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx,
                          final ByteBuf msg,
                          final ByteBuf out) {
        out.writeIntLE(msg.readableBytes());
        out.writeBytes(msg);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx,
                          final ByteBuf in,
                          final List<Object> out
    ) {
        if (in.readableBytes() < 4)
            return;

        in.markReaderIndex();
        val length = in.readIntLE();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        val buf = ctx.alloc().buffer(length);
        in.readBytes(buf, length);
        out.add(buf);
    }
}