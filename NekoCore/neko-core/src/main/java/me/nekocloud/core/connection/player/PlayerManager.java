package me.nekocloud.core.connection.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.base.util.map.MultikeyHashMap;
import me.nekocloud.base.util.map.MultikeyMap;
import me.nekocloud.base.util.query.AsyncUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.player.IPlayerManager;
import me.nekocloud.core.api.event.player.OfflineDataCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class PlayerManager implements IPlayerManager {

    Multimap<String, Supplier<String>> offlineMessageMap = HashMultimap.create();

    Cache<String, CorePlayer> offlineCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .weakValues()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    MultikeyMap<CorePlayer> corePlayerMap = new MultikeyHashMap<CorePlayer>()
            .register(String.class, corePlayer -> corePlayer.getName().toLowerCase())
            .register(Integer.class, CorePlayer::getPlayerID);

    Object2IntMap<String> cachedIds = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

    static Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Override
    @Synchronized("corePlayerMap")
    public void playerConnect(final @NotNull CorePlayer corePlayer) {
        corePlayerMap.put(corePlayer);
    }

    @Override
    @Synchronized("corePlayerMap")
    public void playerDisconnect(final @NotNull CorePlayer corePlayer) {
        corePlayerMap.delete(corePlayer);
    }

    @Override
    public CorePlayer getPlayer(final @NotNull String playerName) {
        return corePlayerMap.get(String.class, playerName.toLowerCase());
    }

    @Override
    public CorePlayer getPlayer(final int playerID) {
        return corePlayerMap.get(Integer.class, playerID);
    }


    @SneakyThrows
    @Nullable
    @Override
    public CorePlayer getOfflinePlayer(final @NotNull String playerName) {
        if (!NAME_PATTERN.matcher(playerName).matches() || playerName.length() < 3)
            return null;

        synchronized (offlineCache) {
            offlineCache.cleanUp();

            if (isOnline(playerName)) return getPlayer(playerName);

            CorePlayer offlinePlayer = offlineCache.asMap().get(playerName);

            if (offlinePlayer == null) {
                    offlinePlayer = AsyncUtil.supplyAsyncFuture(() -> {
                        CorePlayerImpl corePlayer;

                        try {
                            corePlayer = new CorePlayerImpl(playerName, null, null);
                        } catch (UnknownHostException e) {
                            return null;
                        }

                        val offlineData = corePlayer.getOfflineData();
                        corePlayer.setBukkit(offlineData.getLastServer());

                        NekoCore.getInstance().callEvent(new OfflineDataCreateEvent(corePlayer));
                        return corePlayer;
                    });

                    offlineCache.put(playerName.toLowerCase(), offlinePlayer);
                }

                return offlinePlayer;

        }
    }


    @Override
    public boolean isOnline(int playerID) {
        return corePlayerMap.contains(Integer.class, playerID);
    }

    @Override
    public boolean isOnline(final @NotNull String playerName) {
        return corePlayerMap.contains(String.class, playerName.toLowerCase());
    }

    @Override
    public void sendOfflineMessage(final @NotNull String playerName, final Supplier<String> messageSupplier) {
        synchronized (offlineMessageMap) {
            val onlinePlayer = getPlayer(playerName);

            if (onlinePlayer != null && messageSupplier.get() != null) {
                onlinePlayer.sendMessage(messageSupplier.get());
                return;
            }

            offlineMessageMap.put(playerName.toLowerCase(), messageSupplier);
        }
    }


    @Override
    public Collection<CorePlayer> getOnlinePlayers(final @NotNull PlayerResponseHandler playerResponseHandler) {
        return corePlayerMap.valueCollection().stream().filter(playerResponseHandler::handle)
                .collect(Collectors.toSet());
    }

    public interface PlayerResponseHandler {

        boolean handle(final @NotNull CorePlayer corePlayer);
    }

}
