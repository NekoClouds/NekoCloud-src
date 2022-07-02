package me.nekocloud.core.io.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.packet.PacketMapper;
import me.nekocloud.core.io.packet.DefinedPacket;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Setter
public class PacketEncoder extends MessageToByteEncoder<DefinedPacket> {

    PacketMapper packetMapper;

    @Override
    protected void encode(ChannelHandlerContext ctx, DefinedPacket packet, ByteBuf buf) throws Exception {
        packetMapper.writePacket(packet, buf);
    }
}
