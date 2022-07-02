package me.nekocloud.core.connector.bukkit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.base.game.GameState;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bukkit.connection.BukkitHandler;
import me.nekocloud.core.connector.bukkit.event.CoreConnectedEvent;
import me.nekocloud.core.connector.netty.NettyHelper;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.info.ServerInfo;
import me.nekocloud.core.io.info.ServerInfoType;
import me.nekocloud.core.io.info.filter.ServerFilter;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.PacketProtocol;
import me.nekocloud.core.io.packet.bukkit.BukkitOnlineFetch;
import me.nekocloud.core.io.packet.bukkit.BukkitServerInfoFilter;
import me.nekocloud.core.io.packet.handshake.Handshake;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class BukkitConnector extends CoreConnector {

    BukkitConnectorPlugin javaPlugin;
    Logger logger;

    @NonFinal EventLoopGroup workerGroup;
    @NonFinal Bootstrap bootstrap;
    @NonFinal ChannelWrapper channel;
    @NonFinal boolean enabled;
    @NonFinal BukkitTask onlineTask;

    Map<String, Runnable> onlineRunnables = new ConcurrentHashMap<>();
    Map<String, Integer> onlineResults = new ConcurrentHashMap<>();
    Map<UUID, Runnable> filterRunnables = new ConcurrentHashMap<>();
    Map<UUID, List<ServerInfo>> filterResults = new ConcurrentHashMap<>();

    public BukkitConnector(@NotNull BukkitConnectorPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.logger = javaPlugin.getLogger();

        connectionType = Handshake.ConnectionType.BUKKIT;

        address = Bukkit.getIp();
        port = Bukkit.getPort();
        online = -1;
    }

    public void addOnlineTask(@NotNull String regex) {
        onlineRunnables.putIfAbsent(regex, () -> sendPacket(new BukkitOnlineFetch(regex)));
    }

    public void addFilterTask(
            final @NotNull String regex,
            final @NotNull ServerFilter filter,
            final @NotNull String mapName,
            final @NotNull ServerInfoType serverInfoType,
            final @NotNull GameState gameState,
            int limit
    ) {
        UUID uuid = UUID.nameUUIDFromBytes((regex + "," + filter.name() + "," + mapName + ","
                + serverInfoType.name() + "," + gameState.name() + "," + limit).getBytes());
        filterRunnables.putIfAbsent(uuid, () -> sendPacket(new BukkitServerInfoFilter.Request(
                        uuid, regex, filter, limit, mapName, serverInfoType, gameState)));
    }

    public void removeFilterTask(
            final @NotNull String regex,
            final @NotNull ServerFilter filter,
            final @NotNull String mapName,
            final @NotNull ServerInfoType serverInfoType,
            final @NotNull GameState gameState,
            int limit
    ) {
        UUID uuid = UUID.nameUUIDFromBytes((regex + "," + filter.name() + "," + mapName + ","
                + serverInfoType.name() + "," + gameState.name() + "," + limit).getBytes());

        filterRunnables.remove(uuid);
        filterResults.remove(uuid);
    }

    public List<ServerInfo> getServerInfo(
            final @NotNull String regex,
            final @NotNull ServerFilter filter,
            final @NotNull String mapName,
            final @NotNull ServerInfoType serverInfoType,
            final @NotNull GameState gameState,
            int limit
    ) {
        UUID uuid = UUID.nameUUIDFromBytes((regex + "," + filter.name() + "," + mapName + ","
                + serverInfoType.name() + "," + gameState.name() + "," + limit).getBytes());

        return filterResults.getOrDefault(uuid, new ArrayList<>());
    }

    public void setServerInfo(final UUID uuid, final List<ServerInfo> serverInfos) {
        filterResults.put(uuid, serverInfos);
    }

    public void removeOnlineTask(final String regex) {
        onlineRunnables.remove(regex);
        onlineResults.remove(regex);
    }

    public int getOnline(final String regex) {
        return onlineResults.getOrDefault(regex, -1);
    }

    public void setOnline(final @NotNull String regex, int online) {
        if (regex.equals("*")) this.online = online;
        else onlineResults.put(regex, online);
    }

    private BukkitTask startOnlineTask() {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, () -> {
            if (!isActive())
                return;

            onlineRunnables.values().forEach(Runnable::run);
            filterRunnables.values().forEach(Runnable::run);
        }, 2 * 20L, 5 * 20);
    }

    @Override
    public void setChannel(final @NotNull ChannelWrapper channel) {
        this.channel = channel;
    }

    @Override
    public void start() {
        workerGroup = NettyHelper.newEventLoopGroup(3,
                new ThreadFactoryBuilder().setNameFormat("Netty IO Thread #%1$d").build());

        bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NettyHelper.newChannel())
                .handler(new NettyHelper(PacketProtocol.HANDSHAKE.getMapper()))
                .remoteAddress(coreAddress, 1337);

        enabled = true;
        if (connect()) reconnect();

        onlineTask = startOnlineTask();
        addOnlineTask("*");
    }

    @Override
    public boolean connect() {
        getLogger().log(Level.INFO, "Connecting to the Core... [{0}:{1}]",
                new Object[] { coreAddress, 1337 });
        final ChannelFuture future = bootstrap.connect().awaitUninterruptibly();

        if (future.isSuccess()) {
            getLogger().info("Connected!");
            return false;
        }

        getLogger().log(Level.SEVERE, "Could not connect to the Core");
        return true;
    }

    @Override
    public void reconnect() {
        if (!enabled || isConnected())
            return;

        workerGroup.schedule(() -> {
            if (connect()) reconnect();
        }, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void onDisconnect() {
        channel = null;
        reconnect();
    }

    @Override
    public void shutdown() {
        enabled = false;
        if (channel != null)
            channel.close();

        onlineTask.cancel();
        channel = null;
        workerGroup.shutdownGracefully();
    }

    @Override
    public boolean isConnected() {
        return channel != null && isActive();
    }

    @Override
    public void sendPacket(final @NotNull DefinedPacket packet) {
        if (!isConnected())
            return;

        channel.write(packet);
    }

    @Override
    public PacketHandler newPacketHandler(final @NotNull ChannelWrapper channel) {
        return new BukkitHandler(this, channel);
    }

    @Override
    public void coreConnected() {
       Bukkit.getPluginManager().callEvent(new CoreConnectedEvent());
    }
}
