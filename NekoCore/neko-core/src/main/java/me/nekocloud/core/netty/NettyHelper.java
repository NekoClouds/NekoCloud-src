package me.nekocloud.core.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import me.nekocloud.core.connection.HandshakeHandler;
import me.nekocloud.core.io.decoder.PacketDecoder;
import me.nekocloud.core.io.encoder.PacketEncoder;
import me.nekocloud.core.io.handler.BossHandler;
import me.nekocloud.core.io.packet.PacketMapper;
import me.nekocloud.core.io.packet.PacketProtocol;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

@AllArgsConstructor
public class NettyHelper extends ChannelInitializer<SocketChannel> {

    private final PacketMapper packetMapper;

    @Override
    public void initChannel(final @NotNull SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        try {
            channel.config().setOption(ChannelOption.IP_TOS, 24);
        } catch (final ChannelException ex) {
            ex.printStackTrace();
        }

        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);

        pipeline.addLast(new PacketDecoder(packetMapper));
        pipeline.addLast(new PacketEncoder(packetMapper));
        pipeline.addLast(new BossHandler().setHandler(
                new HandshakeHandler("ya_lublu_tvouy_MaMu_CloWn")));
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        return Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static @NotNull EventLoopGroup newEventLoopGroup(final int threads, final ThreadFactory threadFactory) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(threads, threadFactory) :
                new NioEventLoopGroup(threads, threadFactory);
    }

    public static ServerBootstrap newServerBootstrap() {
        return new ServerBootstrap()
                .channel(getServerChannel())
                .childHandler(new NettyHelper(PacketProtocol.HANDSHAKE.getMapper()));
    }
}
