package pw.novit.nekocloud.bungee.api.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.game.SubType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

@UtilityClass
public class RedirectUtil {

    public static final ImmutableSet<String> AUTH_SERVERS = ImmutableSet.of(
            "auth-1", "auth-2", "auth-3"
    );

	public ServerInfo bestServer(final @NotNull Collection<String> servers) {
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

    public ServerInfo bestServer(final String prefixOrName) {
        ServerInfo bestServer = null;
        for (final ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if (!serverInfo.getName().startsWith(prefixOrName.toLowerCase()))
                continue;

            if (bestServer == null) {
                bestServer = serverInfo;
            } else {
                if (serverInfo.getPlayers().size() >= bestServer.getPlayers().size())
                    continue;


                bestServer = serverInfo;
            }
        }

        return bestServer;
    }

    public ServerInfo randomServer(final Predicate<ServerInfo> predicate) {
        return ProxyServer.getInstance().getServers()
                .values()
                .stream()
                .filter(predicate)
                .collect(randomItem());
    }

    @Contract(" -> new")
    private <T> @NotNull Collector<T, List<T>, T> randomItem() {
        return Collector.of((Supplier<List<T>>) ArrayList::new,
                List::add,
                (ts, ts2) -> (List<T>) Iterables.concat(ts, ts2),
                list -> list.isEmpty() ? null : list.get(ThreadLocalRandom.current().nextInt(list.size())),
                new Collector.Characteristics[0]);
    }

    /**
     * Получить лобби-сервер, если указанный упадет.
     *
     * @param server - префикс или имя сервера.
     */
    public static ServerInfo getFallbackLobby(final @NotNull String server) {
        GameType gameType = GameType.getType(server);
        GameType fallbackServer = GameType.LIMBO;

        if (GameType.isTyped(server, SubType.MISC))
            if (!SubType.ofMode(gameType).isEmpty())
                return bestServer(gameType.getLobbyChannel());

        if (bestServer(GameType.HUB.getName()) != null)
            fallbackServer = GameType.HUB;

        return bestServer(fallbackServer.getName());
    }
}
