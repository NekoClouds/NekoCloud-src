package me.nekocloud.core.connector.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.HandshakeHandler;
import me.nekocloud.core.io.decoder.PacketDecoder;
import me.nekocloud.core.io.encoder.PacketEncoder;
import me.nekocloud.core.io.handler.BossHandler;
import me.nekocloud.core.io.packet.PacketMapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

@AllArgsConstructor
public class NettyHelper extends ChannelInitializer<SocketChannel> {

    private final PacketMapper packetMapper;

    @Override
    @SneakyThrows
    protected void initChannel(final @NotNull SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new PacketDecoder(packetMapper));
        pipeline.addLast(new PacketEncoder(packetMapper));
        pipeline.addLast(new BossHandler().setHandler(new HandshakeHandler(CoreConnector.getInstance())));
    }

    public static @NotNull EventLoopGroup newEventLoopGroup(
            final int threads,
            final ThreadFactory threadFactory
    ) {
        return (Epoll.isAvailable() ? new EpollEventLoopGroup(threads, threadFactory)
                : new NioEventLoopGroup(threads, threadFactory));
    }

    public static Class<? extends Channel> newChannel() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}
