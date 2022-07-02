package me.nekocloud.core.connector.bungee.listeners;

import lombok.val;
import me.nekocloud.core.connector.bungee.BungeeConnector;
import me.nekocloud.core.connector.bungee.BungeeConnectorPlugin;
import me.nekocloud.core.io.packet.bungee.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.api.event.gamer.AsyncGamerQuitEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.listeners.ProxyListener;

import static net.md_5.bungee.event.EventPriority.LOW;
import static net.md_5.bungee.event.EventPriority.LOWEST;

public class ConnectorListener extends ProxyListener<BungeeConnectorPlugin> {

    private final BungeeConnector connector;

    public ConnectorListener(
            final BungeeConnectorPlugin connectorPlugin,
            final BungeeConnector connector
    ) {
        super(connectorPlugin);

        this.connector = connector;
    }

    @EventHandler(priority = LOW)
    public void onLoginEvent(final @NotNull LoginEvent event) {
        if (event.isCancelled() || !connector.isConnected())
            return;

        event.registerIntent(plugin);

        connector.registerPlayerLogin(event);
        connector.sendPacket(new BungeePlayerLogin(
                event.getConnection().getName(),
                event.getConnection().getVirtualHost()
        ));
    }

    @EventHandler(priority = LOWEST)
    public void onJoin(final @NotNull PostLoginEvent event) {
        val gamer = BungeeGamer.getGamer(event.getPlayer());
        if (gamer == null)
            return;

        connector.sendPacket(new BungeePlayerJoin(
                gamer.getPlayerID(),
                gamer.getName(),
                gamer.getIp().getHostAddress(),
                "nothing",
                event.getPlayer().getPendingConnection().getVersion()
        ));
        gamer.setCoreLogged(true);
    }

    @EventHandler(priority = LOWEST)
    public void onLeave(final @NotNull AsyncGamerQuitEvent event) {
        val gamer = event.getGamer();
        if (gamer == null)
            return;

        connector.sendPacket(new BungeePlayerDisconnect(gamer.getPlayerID()));
    }

    @EventHandler(priority = 64)
    public void onServerConnectedEvent(final @NotNull ServerConnectedEvent event) {
        val player = event.getPlayer();
        val gamer = BungeeGamer.getGamer(player.getName());
        if (gamer == null)
            return;

        connector.sendPacket(new BungeePlayerSwitchServer(
                gamer.getPlayerID(),
                event.getServer().getInfo().getName()
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatEvent(final @NotNull ChatEvent event) {
        if (event.isCancelled()
                || !event.getMessage().startsWith("/")
                || !(event.getSender() instanceof ProxiedPlayer)
                || event.getMessage().length() == 1) {
            return;
        }

        val commandLine = event.getMessage().replaceFirst("/", "");
        val split = commandLine.split(" ");
        val commandName = split[0].toLowerCase();
        if (!connector.getRegisteredCommands().contains(commandName))
            return;

        val gamer = BungeeGamer.getGamer(((ProxiedPlayer) event.getSender()).getName());

        connector.sendPacket(new BungeePlayerDispatchCommand(gamer.getPlayerID(), commandLine));
        event.setCancelled(true);
    }
}
