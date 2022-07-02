package me.nekocloud.core.connector.bungee.connection;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.connector.bungee.BungeeConnector;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.bungee.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.net.InetSocketAddress;
import java.util.Arrays;

@Log4j2
@SuppressWarnings("deprecation")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public final class BungeeHandler extends PacketHandler {

    BungeeConnector connector;
    ChannelWrapper channel;

    public BungeeHandler(
            final BungeeConnector connector,
            final ChannelWrapper channel
    ) {
        this.connector = connector;
        this.channel = channel;
        this.connector.setChannel(channel);
        this.connector.setActive(true);

        ProxyServer.getInstance().getPlayers().stream()
                .filter(ProxiedPlayer::isConnected)
                .forEach(player -> {
                    val gamer = BungeeGamer.getGamer(player.getName());
                    if (gamer != null)
                        channel.write(new BungeePlayerJoin(
                                gamer.getPlayerID(),
                                player.getName(),
                                player.getAddress().getAddress().getHostAddress(),
                                player.getServer() == null ? null : player.getServer().getInfo().getName(),
                                player.getPendingConnection().getVersion()));
                });
    }

    @Override
    public void onDisconnect(final ChannelWrapper wrapper) {
        connector.getLogger().info(this + " | DISCONNECTED");

        connector.setActive(false);
        connector.onDisconnect();
    }

    @Override
    public void onExceptionCaught(final ChannelWrapper wrapper, final @NotNull Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void handle(final BungeePlayerLogin.@NotNull Result packet) {
        val loginEvent = connector.getAndRemovePlayerLogin(packet.getPlayer());
        if (loginEvent == null)
            return;

        if (!packet.isAllowed()) {
            loginEvent.setCancelled(true);
            loginEvent.setCancelReason(new TextComponent(packet.getCancelReason()));
        }

        loginEvent.completeIntent(connector.getPlugin());
    }

    @Override
    public void handle(final @NotNull BungeeOnlineUpdate packet) {
        connector.setOnline(packet.getOnline());
    }

    @Override
    public void handle(final @NotNull BungeePlayerTitle packet) {
        val gamer = BungeeGamer.getGamer(packet.getPlayerID());
        switch (packet.getAction()) {
            case TITLE -> gamer.sendTitle(packet.getText(), "",
                    packet.getFadeIn(), packet.getStay(), packet.getFadeOut());
            case SUBTITLE -> gamer.sendTitle("", packet.getText());
            case ACTIONBAR -> gamer.sendActionBar(packet.getText());
            case CLEAR -> {
                gamer.sendActionBar("");
                gamer.sendTitle("", "");
            }

        }
    }

    @Override
    public void handle(final @NotNull BungeePlayerActionBar packet) {
        val gamer = BungeeGamer.getGamer(packet.getPlayerID());
        if (gamer == null)
            return;

        gamer.sendActionBar(packet.getMessage());
    }

    @Override
    public void handle(final BungeePlayerActionBar.Announce packet) {
        for (val gamer : BungeeGamer.getGamers().values())
            gamer.sendActionBar(packet.getMessage());
    }

    @Override
    public void handle(final @NotNull BungeePlayerMessage packet) {
        val gamer = BungeeGamer.getGamer(packet.getPlayerID());
        if (gamer == null)
            return;

        Arrays.stream(packet.getMessage())
                .forEach(message -> gamer.sendMessage(ComponentSerializer.parse(message)));
    }

    @Override
    public void handle(final BungeePlayerMessage.Announce packet) {
        for (val gamer : BungeeGamer.getGamers().values())
            for (String message : packet.getMessage())
                gamer.sendMessage(message);
    }

    @Override
    public void handle(final @NotNull BungeeCommandExecute packet) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(
                ProxyServer.getInstance().getConsole(), packet.getCommand());
    }

    @Override
    public void handle(final @NotNull BungeePlayerRedirect packet) {
        val gamer = BungeeGamer.getGamer(packet.getPlayerID());
        if (gamer == null)
            return;

        val player = gamer.getPlayer();
        val serverInfo = ProxyServer.getInstance().getServerInfo(packet.getServer());
        if (player != null && serverInfo != null)
            player.connect(serverInfo);
    }

    @Override
    public void handle(final @NotNull BungeeCommandRegister packet) {
        switch (packet.getAction()) {
            case REGISTER -> {
                Arrays.stream(packet.getAliases())
                        .forEach(connector::registerCommand);
                // Костыль ебаный
                connector.registerCommand(packet.getCommand());
            }
            case UNREGISTER -> {
                Arrays.stream(packet.getAliases())
                        .forEach(connector::unregisterCommand);
                // Костыль ебаный
                connector.unregisterCommand(packet.getCommand());
            }

        }
    }

    @Override
    public void handle(final @NotNull BungeePlayerKick packet) {
        val gamer = BungeeGamer.getGamer(packet.getPlayerID());
        if (gamer == null)
            return;

        val player = gamer.getPlayer();
        player.disconnect(ComponentSerializer.parse(packet.getReason()));
    }

    @Override
    public void handle(final @NotNull BungeeServerAction packet) {
        val proxyServer = ProxyServer.getInstance();
        switch (packet.getAction()) {
            case ADD -> {
                proxyServer.getServers().put(
                        packet.getServer(),
                        proxyServer.constructServerInfo(packet.getServer(),
                                new InetSocketAddress(packet.getIp(), packet.getPort()),
                                "NekoCore", false
                        )
                );
                connector.getLogger().info("[" + packet.getServer() + new InetSocketAddress(packet.getIp(), packet.getPort()) + "] <-> ServerInfoHandler | CONNECTED");
            }

            case REMOVE -> {
                val server = proxyServer.getServers().get(packet.getServer());
                if (server != null && proxyServer.getServers().get(packet.getServer()).getMotd().equals("NekoCore")) {
                    proxyServer.getServers().remove(packet.getServer());
                    connector.getLogger().info("[" + packet.getServer() + new InetSocketAddress(packet.getIp(), packet.getPort()) + "] <-> ServerInfoHandler | DISCONNECTED");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "[" + channel.getRemoteAddress().getAddress().getHostAddress() + "] <-> PacketHandler";
    }
}
