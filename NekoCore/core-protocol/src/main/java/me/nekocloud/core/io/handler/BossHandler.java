package me.nekocloud.core.io.handler;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.packet.DefinedPacket;

public class BossHandler extends SimpleChannelInboundHandler<DefinedPacket> {

    @Getter
    private ChannelWrapper wrapper;
    private PacketHandler handler;

    public BossHandler setHandler(PacketHandler handler) {
        Preconditions.checkNotNull(handler, "PacketHandler is null!");
        this.handler = handler;
        return this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (this.handler != null) {
            this.wrapper = new ChannelWrapper(ctx);
            this.handler.onConnect(this.wrapper);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (this.handler != null) {
            this.handler.onDisconnect(this.wrapper);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DefinedPacket packet) throws Exception {
        if (this.handler != null) {
            packet.handle(this.handler);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (this.handler != null) {
            this.handler.onExceptionCaught(this.wrapper, cause);
        } // 01.07.22 - мускулов чмо
    }
}

