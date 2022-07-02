package me.nekocloud.core.connection.server;

import lombok.Getter;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.api.connection.server.IServerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Log4j2
@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class ServerManager implements IServerManager {

    Map<String, Bukkit> bukkitServers = new HashMap<>();
    Map<String, Bungee> bungeeServers = new HashMap<>();

    @Override
    @Synchronized("bukkitServers")
    public void addBukkit(final @NotNull Bukkit bukkitServer) {
        bukkitServers.put(bukkitServer.getName().toLowerCase(), bukkitServer);
    }

    @Override
    @Synchronized("bungeeServers")
    public void addBungee(final @NotNull Bungee bungeeServer) {
        bungeeServers.put(bungeeServer.getName().toLowerCase(), bungeeServer);
    }

    @Override
    public Bukkit getBukkit(final @NotNull String serverName) {
        return bukkitServers.get(serverName.toLowerCase());
    }

    @Override
    public Bungee getBungee(final @NotNull String serverName) {
        return bungeeServers.get(serverName.toLowerCase());
    }

    @Override
    @Synchronized("bukkitServers")
    public void removeBukkit(final @NotNull String serverName) {
        bukkitServers.remove(serverName.toLowerCase());
    }

    @Override
    @Synchronized("bungeeServers")
    public void removeBungee(final @NotNull String serverName) {
        bungeeServers.remove(serverName.toLowerCase());
    }

    @Override
    public int getOnlineByServerPrefix(final @NotNull String serverPrefix) {
        val serverCollection = getServersByPrefix(serverPrefix);

        int serversOnline = 0;
        for (val coreServer : serverCollection) {
            serversOnline += coreServer.getOnline();
        }

        return serversOnline;
    }

    @Override
    public int getOnlineByProxyPrefix(final @NotNull String serverPrefix) {
        val serverCollection = getProxiesByPrefix(serverPrefix);

        int serversOnline = 0;
        for (val coreServer : serverCollection) {
            serversOnline += coreServer.getOnline();
        }

        return serversOnline;
    }

    @Override
    public Collection<Bukkit> getServersByPrefix(final @NotNull String serverPrefix) {
        return bukkitServers.values()
                .stream()
                .filter(bukkit ->
                        bukkit.getName().toLowerCase().startsWith(serverPrefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Bungee> getProxiesByPrefix(final @NotNull String serverPrefix) {
        return bungeeServers.values()
                .stream()
                .filter(bungee ->
                        bungee.getName().toLowerCase().startsWith(serverPrefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
