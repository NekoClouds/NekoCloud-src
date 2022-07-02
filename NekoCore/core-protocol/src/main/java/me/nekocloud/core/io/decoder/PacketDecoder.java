package me.nekocloud.core.io.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.PacketMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
public class PacketDecoder extends ReplayingDecoder<DefinedPacket> {

    PacketMapper packetMapper;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, @NotNull List<Object> out) {
        out.add(packetMapper.readPacket(buf));
    }
}
