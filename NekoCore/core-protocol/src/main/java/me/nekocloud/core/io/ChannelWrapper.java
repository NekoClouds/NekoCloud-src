package me.nekocloud.core.io;

import com.google.common.base.Preconditions;
import io.netty.channel.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.core.io.compress.PacketCompressor;
import me.nekocloud.core.io.compress.PacketDecompressor;
import me.nekocloud.core.io.decoder.PacketDecoder;
import me.nekocloud.core.io.encoder.PacketEncoder;
import me.nekocloud.core.io.handler.BossHandler;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.PacketMapper;
import me.nekocloud.core.io.packet.PacketProtocol;
import me.nekocloud.core.io.pipeline.PipelineUtil;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChannelWrapper {

    Channel channel;
    InetSocketAddress remoteAddress;
    InetSocketAddress addressToConnect;
    @NonFinal boolean closed;

    public ChannelWrapper(final @NotNull ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        this.remoteAddress = (InetSocketAddress) this.channel.remoteAddress();
        this.addressToConnect = (InetSocketAddress) this.channel.localAddress();
    }

    public void setHandler(final PacketHandler handler) {
        this.channel.pipeline().get(BossHandler.class).setHandler(handler);
    }

    public void setProtocol(final @NotNull PacketProtocol protocol) {
        setMapper(protocol.getMapper());
    }

    public void setMapper(final @NotNull PacketMapper packetMapper) {
        final ChannelPipeline pipeline = channel.pipeline();

        pipeline.get(PacketDecoder.class).setPacketMapper(packetMapper);
        pipeline.get(PacketEncoder.class).setPacketMapper(packetMapper);
    }

    public ChannelFuture write(DefinedPacket packet) {
        if (!closed) {
            return channel.writeAndFlush(packet);
        }
        return null;
    }

    public ChannelFuture close() {
        if (!closed) {
            closed = true;
            return channel.close();
        }
        return null;
    }

    public void addBefore(String before, String after, ChannelHandler handler) {
        Preconditions.checkState(channel.eventLoop().inEventLoop(), "cannot add handler outside of event loop");

        channel.pipeline().flush();
        channel.pipeline().addBefore(before, after, handler);
    }

    public void setCompression(final int threshold) {
        if (channel.pipeline().get(PacketCompressor.class) == null && threshold != -1) {
            addBefore(PipelineUtil.PACKET_ENCODER, "compress", new PacketCompressor());
        }

        if (threshold != -1) {
            channel.pipeline().get(PacketCompressor.class).setThreshold(threshold);
        } else {
            channel.pipeline().remove("compress");
        }

        if (channel.pipeline().get(PacketDecompressor.class) == null && threshold != -1) {
            addBefore(PipelineUtil.PACKET_DECODER, "decompress", new PacketDecompressor());
        }
        if (threshold == -1) {
            channel.pipeline().remove("decompress");
        }
    }
}
