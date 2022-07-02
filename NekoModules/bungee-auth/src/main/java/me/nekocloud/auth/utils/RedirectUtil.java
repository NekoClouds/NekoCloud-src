package me.nekocloud.auth.utils;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;

public class RedirectUtil {

    public static final ImmutableSet<String> HUB_SERVERS = ImmutableSet.of(
            "hub-1", "hub-2", "hub-3"
    );
    public static final ImmutableSet<String> AUTH_SERVERS = ImmutableSet.of(
            "auth-1", "auth-2", "auth-3"
    );

    public static ServerInfo getBestHub() {
        return getBestServer(HUB_SERVERS);
    }
    public static ServerInfo getBestAuth() {
        return getBestServer(AUTH_SERVERS);
    }

	public static ServerInfo getBestServer(final Collection<String> servers) {
        ServerInfo server = null;

        for (String serverName : servers) {
            ServerInfo srv = ProxyServer.getInstance().getServerInfo(serverName);
            if (srv == null)
                continue;

            if (server == null) {
                server = srv;
                continue;
            }

            if (server.getPlayers().size() >= srv.getPlayers().size())
                continue;
            server = srv;
        }

        return server;
    }
}
