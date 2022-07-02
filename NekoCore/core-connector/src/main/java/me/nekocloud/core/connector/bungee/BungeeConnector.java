package me.nekocloud.core.connector.bungee;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bungee.connection.BungeeHandler;
import me.nekocloud.core.connector.bungee.event.CoreConnectedEvent;
import me.nekocloud.core.connector.netty.NettyHelper;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.PacketProtocol;
import me.nekocloud.core.io.packet.handshake.Handshake;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class BungeeConnector extends CoreConnector {


    Plugin plugin;

    Map<String, LoginEvent> playersLogins = new HashMap<>();
    Set<String> registeredCommands        = new HashSet<>();

    @NonFinal EventLoopGroup workerGroup;
    @NonFinal Bootstrap bootstrap;
    @Setter @NonFinal
    ChannelWrapper channel;

    @NonFinal boolean enabled;

    public BungeeConnector(final @NotNull BungeeConnectorPlugin plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();

        connectionType = Handshake.ConnectionType.BUNGEE;

        address = ProxyServer.getInstance().getConfig()
                .getListeners().iterator().next().getHost()
                .getAddress().getHostAddress();

        port = ProxyServer.getInstance()
                .getConfig()
                .getListeners()
                .iterator().next().getHost().getPort();
    }

    @Override
    public void start() {
        workerGroup = NettyHelper.newEventLoopGroup(4,
                new ThreadFactoryBuilder().setNameFormat("Netty IO Thread #%1$d").build());

        bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NettyHelper.newChannel())
                .handler(new NettyHelper(PacketProtocol.HANDSHAKE.getMapper()))
                .remoteAddress(coreAddress, 1337);

        enabled = true;
        if (!connect()) reconnect();
    }

    @Override
    public boolean connect() {
        getLogger().log(Level.INFO, "Connecting to the Core... [{0}:{1}]",
                new Object[]{coreAddress, 1337});

        val future = bootstrap.connect().awaitUninterruptibly();
        if (future.isSuccess()) {
            getLogger().info("Connected!");
            return true;
        }

        getLogger().log(Level.SEVERE, "Could not connect to the Core");
        return false;
    }

    @Override
    public void onDisconnect() {
        clearCache();
        reconnect();
    }

    private void clearCache() {
        channel = null;
        online = 0;
        registeredCommands.clear();

        Iterator<Map.Entry<String, LoginEvent>> iterator = playersLogins.entrySet().iterator();
        while (iterator.hasNext()) {
            final LoginEvent event = iterator.next().getValue();
            event.setCancelled(true);
            event.setCancelReason("§cПотеряно соединение с кором\n§cСообщите администрации: §dvk.me/novitt");
            event.completeIntent(plugin);
            iterator.remove();
        }

        ProxyServer.getInstance().getServers()
                .entrySet()
                .removeIf(stringServerInfoEntry -> stringServerInfoEntry
                        .getValue()
                        .getMotd().equals("NekoCloud"));
    }

    @Override
    public void reconnect() {
        if (!enabled || isConnected())
            return;

        workerGroup.schedule(() -> {
            if (!connect()) {
                reconnect();
            }
        }, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        enabled = false;
        if (channel != null)
            channel.close();

        clearCache();
        workerGroup.shutdownGracefully();
    }

    @Override
    public boolean isConnected() {
        return channel != null && active;
    }

    @Override
    public void sendPacket(final @NotNull DefinedPacket packet) {
        if (!isConnected())
            return;

        channel.write(packet);
    }

    @Override
    public PacketHandler newPacketHandler(final @NotNull ChannelWrapper channel) {
        return new BungeeHandler(this, channel);
    }

    @Override
    public void coreConnected() {
        ProxyServer.getInstance().getPluginManager().callEvent(new CoreConnectedEvent());
    }

    public void registerPlayerLogin(final LoginEvent event) {
        playersLogins.put(event.getConnection().getName().toLowerCase(), event);
    }

    public LoginEvent getAndRemovePlayerLogin(final @NotNull String playerName) {
        return playersLogins.remove(playerName.toLowerCase());
    }

    public void registerCommand(final String command) {
        registeredCommands.add(command);
    }

    public void unregisterCommand(final String command) {
        registeredCommands.remove(command);
    }
}