package me.nekocloud.core.connection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.connection.server.BukkitServer;
import me.nekocloud.core.connection.server.BungeeServer;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.PacketProtocol;
import me.nekocloud.core.io.packet.handshake.Handshake;
import org.jetbrains.annotations.NotNull;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HandshakeHandler extends PacketHandler {

    String password;
    @NonFinal ChannelWrapper channel;

    @Override
    public void onConnect(final ChannelWrapper channel) {
        this.channel = channel;
    }

    @Override
    public void onDisconnect(final ChannelWrapper channel) {
        log.info(this + " has disconnected.");
    }

    @Override
    public void onExceptionCaught(final @NotNull ChannelWrapper channel, final Throwable cause) {
        log.info(this + " FAIL CONNECT TO: " + channel.getAddressToConnect());
        log.info(this + " has exception:", cause);
        channel.close();
    }

    @Override
    public void handle(final @NotNull Handshake handshake) {
        assert channel != null;

        if (!handshake.getPassword().equals(password)) {
            log.info("Failed connection from " + channel.getRemoteAddress().toString() + ": password is invalid");
            channel.close();

            return;
//            throw new InvalidPasswordException(handshake.getPassword());
        }

        val bungee = handshake.getConnectionType() == Handshake.ConnectionType.BUNGEE;
        val packetHandler = bungee ?
                new BungeeServer(handshake.getName(), handshake.getPort()) :
                new BukkitServer(handshake.getName(), handshake.getPort());

        val remoteAddress = channel.getRemoteAddress();
        boolean result = bungee ?
                NekoCore.getInstance().handleBungee((Bungee) packetHandler, remoteAddress) :
                NekoCore.getInstance().handleBukkit((Bukkit) packetHandler, remoteAddress);

        if (result) {
            channel.write(handshake).addListener(future -> {
                assert future.isSuccess();

                channel.setProtocol(bungee ? PacketProtocol.BUNGEE : PacketProtocol.BUKKIT);

                //packetHandler.onConnect(channel);
            });

            channel.write(handshake);
            packetHandler.onConnect(channel); //todo this

        } else {
            channel.close();
        }
    }

    @Override
    public String toString() {
        return "[" + channel.getRemoteAddress().getAddress().getHostAddress() + "] <-> HandshakeHandler";
    }
}
