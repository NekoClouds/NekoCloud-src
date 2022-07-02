package me.nekocloud.core.connector;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.PacketProtocol;
import me.nekocloud.core.io.packet.handshake.Handshake;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HandshakeHandler extends PacketHandler {

    final CoreConnector connector;
    ChannelWrapper channel;

    @Override
    public void onConnect(final @NotNull ChannelWrapper channel) {
        this.channel = channel;
        this.connector.getLogger().info(channel.getRemoteAddress().getHostString() + " has connected.");
        this.channel.write(new Handshake(
                connector.getServerName(),
                connector.getAddress(),
                "ya_lublu_tvouy_MaMu_CloWn",
                connector.getPort(),
                connector.getConnectionType()));
    }

    @Override
    public void onDisconnect(final @NotNull ChannelWrapper channel) {
        connector.getLogger().info(this + " has disconnected.");
        connector.onDisconnect();
    }

    @Override
    public void onExceptionCaught(final ChannelWrapper channel, @NotNull Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void handle(final @NotNull Handshake handshake) {
        channel.setProtocol(connector.getConnectionType() == Handshake.ConnectionType.BUNGEE
                ? PacketProtocol.BUNGEE
                : PacketProtocol.BUKKIT
        );
        channel.setHandler(connector.newPacketHandler(channel));

        connector.coreConnected();
    }

    @Override
    public String toString() {
        return "[" + channel.getRemoteAddress().getAddress().getHostAddress() + "] <-> HandshakeHandler";
    }
}
