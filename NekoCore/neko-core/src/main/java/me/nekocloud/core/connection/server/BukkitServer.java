package me.nekocloud.core.connection.server;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.sections.SettingsSection;
import me.nekocloud.base.util.map.MultikeyHashMap;
import me.nekocloud.base.util.map.MultikeyMap;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.connection.Connection;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.server.BukkitServerInfoUpdatedEvent;
import me.nekocloud.core.api.utils.redirect.RedirectUtil;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.info.ServerInfo;
import me.nekocloud.core.io.info.ServerInfoType;
import me.nekocloud.core.io.info.fields.ServerField;
import me.nekocloud.core.io.info.filter.ServerFilter;
import me.nekocloud.core.io.info.types.DefaultServerInfo;
import me.nekocloud.core.io.info.types.GameServerInfo;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.bukkit.*;
import me.nekocloud.core.io.packet.bungee.BungeeServerAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode(of = "name", callSuper = false)
public class BukkitServer extends PacketHandler implements Connection, Bukkit {

    NekoCore core = NekoCore.getInstance();

    @NonFinal ChannelWrapper channel;

    String name;
    int port;

    MultikeyMap<CorePlayer> players = new MultikeyHashMap<CorePlayer>()
            .register(String.class, IBaseGamer::getName)
            .register(Integer.class, CorePlayer::getPlayerID);

    ServerInfo serverInfo = new DefaultServerInfo();

    public BukkitServer(final String name, final int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public final void onConnect(final ChannelWrapper channel) {
        log.info(this + " has connected.");

        this.channel = channel;
        this.channel.setHandler(this);

        core.getBungeeServers().forEach(bungee ->
            bungee.sendPacket(new BungeeServerAction(BungeeServerAction.Action.ADD, name,
                    getAddress().getAddress().getHostAddress(), port)));
    }

    @Override
    public final void onDisconnect(final ChannelWrapper channel) {
        core.getBungeeServers().forEach(bungee ->
                bungee.sendPacket(new BungeeServerAction(BungeeServerAction.Action.REMOVE, name,
                        getAddress().getAddress().getHostAddress(), port)));

        core.handleBukkitDisconnection(this);
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
                player -> player.getBukkit() != null
                        && player.getBukkit().getName().equalsIgnoreCase(name)
        );
    }

    @Override
    public final InetSocketAddress getAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public final void sendPacket(DefinedPacket packet) {
        if (channel == null || packet == null)
            return;

        channel.write(packet);
    }

    @Override
    public void sendCommand(String command) {
        this.sendPacket(new BukkitCommandExecute(command));
    }

    @Override
    public void restart() {
        sendPacket(new BukkitServerAction(BukkitServerAction.Action.RESTART));
    }

    @Override
    public void langReload() {
        sendPacket(new BukkitServerAction( BukkitServerAction.Action.LANG_RELOAD));
    }

    @Override
    public void handle(final @NotNull BukkitSetting packet) {
        val player = core.getPlayer(packet.getPlayerID());
        if (player == null)
            return;

        // так, на всякий случай.
        val settingsSection = player.getSection(SettingsSection.class);
        if (packet.isFlag() == settingsSection.getSetting(packet.getSettingsType()))
            return;

        player.setSetting(packet.getSettingsType(), packet.isFlag());
    }

    @Override
    public void handle(final BukkitNetworking networking) {

    }

    @Override
    public void handle(final @NotNull BukkitGroupPacket groupPacket) {
        val corePlayer = core.getPlayer(groupPacket.getPlayerID());
        if (corePlayer == null)
            return;

        final Group group = Group.getGroupByLevel(groupPacket.getGroupLevel());
        corePlayer.setGroup(group, false);
    }

    @Override // Выполнение команды от CoreAPI#executeCommand()
    public void handle(final @NotNull BukkitPlayerDispatchCommand playerDispatchCommand) {
        val corePlayer = players.get(Integer.class, playerDispatchCommand.getPlayerID());
        if (corePlayer == null)
            return;

        if (core.getCommandManager().dispatchCommand(corePlayer, playerDispatchCommand.getCommand()))
            log.info("{} execute the command: /{}",
                    ChatColor.stripColor(corePlayer.getDisplayName()), playerDispatchCommand.getCommand());
    }

    @Override
    public void handle(final BukkitServerInfoFilter.@NotNull Request request) {
        final Pattern pattern = Pattern.compile(request.getRegex());
        final List<Bukkit> bukkitServers = new ArrayList<>();

        for (Bukkit bukkitServer : core.getBukkitServers()) {
            if (!pattern.matcher(bukkitServer.getName()).matches())
                continue;

            if (serverInfo.getType() == ServerInfoType.GAME &&
                    request.getServerType() == ServerInfoType.GAME) {
                GameServerInfo gameServerInfo = (GameServerInfo) serverInfo;
                if (gameServerInfo.getState() != request.getState())
                    continue;
            }

            bukkitServers.add(bukkitServer);
        }

        final List<ServerInfo> serversInfos = new ArrayList<>();
        
        switch (request.getFilter()) {
            case REGEX:
                for (Bukkit bukkit : bukkitServers)
                    serversInfos.add(bukkit.getServerInfo());

                if (request.getLimit() != -1 && serversInfos.size() >= request.getLimit())
                    break;

                break;
            case MAP_NAME:
                for (Bukkit bukkit : bukkitServers) {
                    String mapName = bukkit.getServerInfo().getFieldValue(ServerField.MAP_NAME);
                    if (mapName != null && mapName.equals(request.getMapName()))
                        serversInfos.add(bukkit.getServerInfo());

                    if (request.getLimit() != -1 && serversInfos.size() >= request.getLimit())
                        break;
                }
                break;
            case MAX_ONLINE:
            case MIN_ONLINE:
                Bukkit currentBukkit = null;
                for (Bukkit bukkit : bukkitServers) {
                    if (currentBukkit == null) {
                        currentBukkit = bukkit;
                    } else if ((request.getFilter() == ServerFilter.MIN_ONLINE ?
                            (bukkit.getOnline() < currentBukkit.getOnline()) :
                            (bukkit.getOnline() > currentBukkit.getOnline()))) {
                        currentBukkit = bukkit;
                    }
                }
                
                if (currentBukkit != null)
                    serversInfos.add(currentBukkit.getServerInfo());
                
                break;
        }

        sendPacket(new BukkitServerInfoFilter.Response(
                request.getRequestId(), request.getRegex(), request.getFilter(), serversInfos));
    }

    @Override
    public void handle(final @NotNull BukkitOnlineFetch onlineFetch) {
        val regex = onlineFetch.getRegex();
        int online = -1;
        if (regex.equals("*")) {
            online = core.getOnline();
        } else {
            if (regex.startsWith("@")) {
                Pattern pattern = Pattern.compile(regex.replaceFirst("@", ""),
                        Pattern.CASE_INSENSITIVE);
                if (core.getBukkitServers().stream().unordered()
                        .anyMatch(bukkit -> pattern.matcher(bukkit.getName()).matches()))
                    online = 0;

                for (val bukkitServer : core.getBukkitServers()) {
                    if (pattern.matcher(bukkitServer.getName()).matches()) {
                        online += bukkitServer.getOnline();
                    }
                }
            } else {
                val bukkit = core.getBukkit(regex);
                if (bukkit != null)
                    online = bukkit.getOnline();
            }
        }

        sendPacket(new BukkitOnlineFetch.Response(regex, online));
    }

    @Override
    public void handle(final @NotNull BukkitServerInfo packet) {
        boolean versionChanged = serverInfo.getProtocolVersion() != packet.getProtocolVersion();
        if (versionChanged) {
            log.info("{}: version mismatch ({} != {}), creating new client info.",
                    toString(),
                    serverInfo.getProtocolVersion(),
                    packet.getProtocolVersion()
            );
        }

        serverInfo.setProtocolVersion(packet.getProtocolVersion());

        for (val entry : packet.getFields().entrySet())
            serverInfo.addFieldValue(entry.getKey(), entry.getValue());

        core.callEvent(new BukkitServerInfoUpdatedEvent(
                this,
                serverInfo,
                versionChanged
        ));
        log.info(packet + " " + packet.getFields());
    }

    @Override
    public void handle(final @NotNull BukkitPlayerRedirect packet) {
        val player = core.getPlayer(packet.getPlayerID());
        if (player == null)
            return;

        val pattern = Pattern.compile(packet.getServer());
        Bukkit bukkit = null;
        switch (packet.getFilter()) {
            case REGEX -> bukkit = RedirectUtil.getRandomServer(pattern);
            case MAX_ONLINE, MIN_ONLINE -> {
                Bukkit currentBukkit = null;
                for (Bukkit current : core.getBukkitServers()) {
                    if (currentBukkit == null) {
                        currentBukkit = current;
                    } else if ((packet.getFilter() == ServerFilter.MIN_ONLINE ?
                            (current.getOnline() < currentBukkit.getOnline()) :
                            (current.getOnline() > currentBukkit.getOnline()))) {
                        currentBukkit = current;
                    }
                }

                if (currentBukkit != null)
                    bukkit = currentBukkit;

            }
            case MAP_NAME -> {
                for (val current : core.getBukkitServers()) {
                    val mapName = current.getServerInfo().getFieldValue(ServerField.MAP_NAME);
                    if (mapName != null && mapName.equals(packet.getMapName()) &&
                            pattern.matcher(current.getName()).matches())
                        bukkit = current;
                }
            }
            default -> bukkit = core.getBukkit(packet.getServer());
        }

        if (bukkit != null) {
            player.redirect(bukkit);
            removePlayer(player);
        }
        else if (player.getBukkit() != null)
            player.getBukkit().sendPacket(new BukkitPlayerRedirect.Error(
                    player.getPlayerID(),
                    packet.getServer(),
                    player.getLanguage().getMessage("SERVER_NOT_FOUND_CORE")
            ));
    }

    @Override
    public void handle(final BukkitSetting.@NotNull Lang packet) {
        val player = core.getPlayer(packet.getPlayerID());
        if (player == null)
            return;

        player.getSection(SettingsSection.class).setLang(packet.getLanguage(), false);
    }

    @Override
    public void handle(final BukkitSetting.@NotNull Prefix packet) {
        val player = core.getPlayer(packet.getPlayerID());
        if (player == null)
            return;

        player.setPrefix(packet.getPrefix());
    }

    @Override
    public void handle(final @NotNull BukkitPlaySound playSound) {
        val corePlayer = core.getPlayer(playSound.getPlayerID());
        if (corePlayer != null) corePlayer.playSound(playSound.getSoundType());
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
        return "[" + name + ":" + port + "] <-> BukkitConnection";
    }
}
