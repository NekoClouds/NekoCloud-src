package me.nekocloud.myserver.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MyServerManager {

    public static final MyServerManager INSTANCE = new MyServerManager();

    @Getter
    private final Map<String, PlayerMyServer> playerMyServers
            = new HashMap<>();


    public void createServer(@NotNull CorePlayer player, @NotNull MyServerType myServerType) {
        PlayerMyServer playerMyServer = new PlayerMyServer(player, myServerType);
        boolean isStarted = playerMyServer.start();

        if (isStarted) {
            playerMyServers.put(player.getName().toLowerCase(), playerMyServer);
        }
    }

    public Collection<PlayerMyServer> getActiveServers() {
        return playerMyServers.values();
    }

    public Collection<PlayerMyServer> getActiveServers(@NotNull MyServerCategory category) {
        return playerMyServers.values()
                .stream()
                .filter(playerMyServer -> playerMyServer.getServerType().getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public Collection<PlayerMyServer> getActiveServers(@NotNull MyServerType type) {
        return playerMyServers.values()
                .stream()
                .filter(playerMyServer -> playerMyServer.getServerType().equals(type))
                .collect(Collectors.toList());
    }

    public void removeServer(@NotNull CorePlayer player) {
        playerMyServers.remove(player.getName().toLowerCase());
    }

    public boolean isAvailable(@NotNull Path serverFolder) {
        for (PlayerMyServer playerMyServer : playerMyServers.values())

            if (playerMyServer.getServerFolder() != null && playerMyServer.getServerFolder().equals(serverFolder))
                return true;

        return false;
    }

    public boolean hasServer(@NotNull String serverName) {
        for (PlayerMyServer playerMyServer : playerMyServers.values())

            if (playerMyServer.getServerName().equalsIgnoreCase(serverName))
                return true;

        return false;
    }

    public boolean isLeader(@NotNull String serverName, @NotNull CorePlayer player) {
        return hasServer(serverName) && getPlayerServer(player) != null &&

                getPlayerServer(player).getServerName().equalsIgnoreCase(serverName) &&
                getPlayerServer(player).isLeader(player);
    }

    public boolean isModer(@NotNull String serverName, @NotNull CorePlayer player) {
        return hasServer(serverName) && getPlayerServer(player) != null &&

                getPlayerServer(player).getServerName().equalsIgnoreCase(serverName) &&
                getPlayerServer(player).isModer(player);
    }

    public PlayerMyServer getPlayerServer(@NotNull CorePlayer player) {
        return playerMyServers.get(player.getName().toLowerCase());
    }

}
