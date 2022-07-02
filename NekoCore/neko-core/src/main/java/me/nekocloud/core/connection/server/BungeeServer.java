package me.nekocloud.core.connection.server;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.util.map.MultikeyHashMap;
import me.nekocloud.base.util.map.MultikeyMap;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.Connection;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.api.event.player.ConnectedEvent;
import me.nekocloud.core.api.event.player.LoginEvent;
import me.nekocloud.core.api.event.player.SwitchServerEvent;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.info.fields.ServerField;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.bungee.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.stream.Collectors;

@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode(of = "name", callSuper = false)
public class BungeeServer extends PacketHandler implements Connection, Bungee {

    NekoCore core = NekoCore.getInstance();

    @NonFinal ChannelWrapper channel;

    String name;
    int port;

    MultikeyMap<CorePlayer> players = new MultikeyHashMap<CorePlayer>()
            .register(String.class, IBaseGamer::getName)
            .register(Integer.class, CorePlayer::getPlayerID);

    public BungeeServer(final String name, final int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public final void onConnect(final ChannelWrapper channel) {
        log.info(this + " has connected.");

        this.channel = channel;
        this.channel.setHandler(this);

        for (val bukkitServer : core.getBukkitServers()) {
            sendPacket(new BungeeServerAction(
                    BungeeServerAction.Action.ADD,
                    bukkitServer.getName(),
                    bukkitServer.getAddress().getAddress().getHostAddress(),
                    bukkitServer.getPort()));
        }

        ensureOnline();
        ensureCommands();
    }

    @Override
    public final void onDisconnect(final ChannelWrapper channel) {
        for (val player : players.valueCollection()) {
            if (player.getBukkit() != null)
                player.getBukkit().removePlayer(player);

            core.handlePlayerDisconnection(player);
            player.remove();
        }

        core.handleBungeeDisconnection(this);
    }

    @Override
    public final void onExceptionCaught(final ChannelWrapper channel, final Throwable cause) {
        if (cause instanceof SocketException) {
            channel.close();
        } else {
            cause.printStackTrace();
        }
    }

    @Override
    public final Collection<CorePlayer> getOnlinePlayers() {
        return NekoCore.getInstance().getOnlinePlayers(
                player -> player.getBungee() != null
                        && player.getBungee().getName().equalsIgnoreCase(name)
        );
    }

    @Override
    public InetSocketAddress getAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public void sendPacket(DefinedPacket packet) {
        if (this.channel == null || packet == null) {
            return;
        }
        this.channel.write(packet);
    }

    @Override
    public void ensureOnline() {
        this.sendPacket(new BungeeOnlineUpdate(core.getOnline()));
    }

    @Override
    public void ensureCommands() {
        core.getCommandManager().getCommandMap().values().forEach(command ->
                sendPacket(new BungeeCommandRegister(command.getCommand(), command.getAliases(),
                        BungeeCommandRegister.Action.REGISTER)));
    }

    private void updateOnline() {
        core.getBungeeServers().forEach(Bungee::ensureOnline);
    }

    @Override
    public void handle(final @NotNull BungeePlayerLogin packet) {
        if (packet.getPlayerName() == null)
            return;

        val player = core.getPlayer(packet.getPlayerName());
        if (player != null && player.isOnline() && player.getBukkit() != null) {
            sendPacket(new BungeePlayerLogin.Result(name, false,
                    "§cИгрок с таким ником уже играет на сервере"
            ));
            return;
        }

        core.getExecutor().execute(() -> {
            val loginEvent = new LoginEvent(
                    packet.getPlayerName(),
                    packet.getVirtualHost(),
                    this
            );

            core.callEvent(loginEvent);
            if (loginEvent.isCancelled()) {
                sendPacket(new BungeePlayerLogin.Result(packet.getPlayerName(),
                        false, loginEvent.getCancelReasonJson()));
                return;
            }

            sendPacket(new BungeePlayerLogin.Result(
                    packet.getPlayerName(),
                    true, null
            ));
        });
    }

    @Override
    public void handle(final @NotNull BungeePlayerJoin playerJoin) {
        CorePlayer corePlayer = core.getPlayer(playerJoin.getPlayerID());
        if (corePlayer != null) {
            sendPacket(new BungeePlayerKick(playerJoin.getPlayerID(),
                    "§cИгрок с таким ником уже играет на сервере"));
            return;
        }
        try {
            // ладно, помучали, создаем объект
            corePlayer = CorePlayer.getOrCreate(
                    playerJoin.getPlayer(),
                    playerJoin.getIp(),
                    this,
                    playerJoin.getProtocolVersion()
            );

            // Добавляем игрока в сам кор
            core.handlePlayer(corePlayer);
        } catch (UnknownHostException e) {
            sendPacket(new BungeePlayerKick(
                    playerJoin.getPlayerID(),
                    "§cУ вас неверный ip-адрес")
            );
            return;
        }

        // Добавляем игрока на его серв банжи в коре
        addPlayer(corePlayer);

        val bukkit = core.getBukkit(playerJoin.getServer());
        if (bukkit != null) {
            corePlayer.setBukkit(core.getBukkit(playerJoin.getServer()));
            bukkit.getServerInfo().addFieldValue(ServerField.ONLINE, bukkit.getOnline());
        }

        // без комментариев
        final CorePlayer finalCorePlayer = corePlayer;
        core.getExecutor().execute(() -> core.callEvent(new ConnectedEvent(finalCorePlayer)));

        updateOnline();
        log.info("{} connected to the [Proxy] {}", corePlayer.getName(), this.getName());
    }

    @Override
    public void handle(final @NotNull BungeePlayerDisconnect playerDisconnect) {
        val player = core.getPlayer(playerDisconnect.getPlayerID()); // пиздец....
        if (player == null)
            return;

        removePlayer(player);

        val bukkit = player.getBukkit();
        if (bukkit != null) player.getBukkit().removePlayer(player);

        core.handlePlayerDisconnection(player); // Ивент вызывается тут
        updateOnline();

        player.remove();
        log.info("{} disconnected.", player.getName());
    }

    @Override
    public void handle(final @NotNull BungeePlayerDispatchCommand playerDispatchCommand) {
        val corePlayer = core.getPlayer(playerDispatchCommand.getPlayerID());
        if (corePlayer == null)
            return;

        if (core.getCommandManager().dispatchCommand(corePlayer, playerDispatchCommand.getCommand())) {
            log.info("{} execute the command: /{}", corePlayer.getName(), playerDispatchCommand.getCommand());
        }
    }

    @Override
    public void handle(final @NotNull BungeePlayerSwitchServer playerSwitchServer) {
        val player = core.getPlayer(playerSwitchServer.getPlayerID());
        if (player == null)
            return;

        if (player.getBukkit() != null) player.getBukkit().removePlayer(player);

        val bukkit = core.getBukkit(playerSwitchServer.getServer());
        if (bukkit != null) {
            player.setBukkit(bukkit);
            bukkit.getServerInfo().addFieldValue(ServerField.ONLINE, bukkit.getOnline());
            core.callEvent(new SwitchServerEvent(player, bukkit));
        }

        log.info("{} connected to the [Server] {}",player.getName(), playerSwitchServer.getServer());
    }

    @Override
    public final int getOnline() {
        return getPlayers(player -> true).size();
    }

    @Override
    public final CorePlayer getPlayer(final int playerID) {
        return this.players.get(Integer.class, playerID);
    }

    @Override
    @Synchronized("players")
    public final void addPlayer(final CorePlayer corePlayer) {
        this.players.put(corePlayer);
    }

    @Override
    @Synchronized("players")
    public final void removePlayer(final CorePlayer corePlayer) {
        this.players.delete(corePlayer);
    }

    @Override
    public final Collection<CorePlayer> getPlayers(final @NotNull ResponseHandler responseHandler) {
        return players.valueCollection().stream().filter(responseHandler::handle)
                .collect(Collectors.toSet());
    }

    @Contract(pure = true)
    @Override
    public final @NotNull String toString() {
        return "[" + name + ":" + port + "] <-> BungeeConnection";
    }
}
