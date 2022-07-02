package me.nekocloud.core.rcon.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.net.SocketAddress;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class RconServer {

    ServerBootstrap bootstrap  = new ServerBootstrap();
    EventLoopGroup bossGroup   = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    public RconServer(String password) {

        bootstrap.group(bossGroup, workerGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                         ch.pipeline()
                                 .addLast(new FramingHandler())
                                 .addLast(new RconHandler(password));
                    }
                });
    }

    public ChannelFuture bind(final SocketAddress address) {
        return bootstrap.bind(address);
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
