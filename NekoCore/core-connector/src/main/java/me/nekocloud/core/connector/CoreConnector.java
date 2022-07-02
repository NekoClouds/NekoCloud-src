package me.nekocloud.core.connector;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.handshake.Handshake;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
public abstract class CoreConnector {

    @Getter
    private static CoreConnector instance;

    Logger logger;
    ChannelWrapper channelWrapper;
    Handshake.ConnectionType connectionType;

    String serverName;
    String coreAddress = "127.0.0.1";
    String address;

    int corePort;
    int port;
    @Setter int online;
    @Setter boolean active;

    public abstract void start();

    public abstract boolean connect();

    public abstract void reconnect();

    public abstract void onDisconnect();

    public abstract void shutdown();

    public abstract boolean isConnected();

    public abstract void setChannel(final @NotNull ChannelWrapper channel);

    public abstract void sendPacket(final @NotNull DefinedPacket packet);

    public abstract PacketHandler newPacketHandler(final @NotNull ChannelWrapper channel);

    public abstract void coreConnected();

    @NotNull
    public String getServerName() {
        if (serverName == null) {
            try {
                serverName = new File("").getCanonicalFile().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return serverName;
    }

    public static void setInstance(final @NonNull CoreConnector coreConnector) {
        if (instance != null) {
            throw new UnsupportedOperationException("CoreConnector already initialized");
        }
        instance = coreConnector;
    }
}
