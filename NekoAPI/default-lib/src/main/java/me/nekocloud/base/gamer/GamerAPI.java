package me.nekocloud.base.gamer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.base.sql.GlobalLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class GamerAPI {

    private final Map<String, GamerBase> GAMERS = new ConcurrentHashMap<>();

    public Map<String, GamerBase> getGamers() {
        return Collections.unmodifiableMap(GAMERS);
    }

    public void clearGamers() {
        GAMERS.clear();
    }

    public void addGamer(final GamerBase gamerBase) {
        GAMERS.put(gamerBase.getName().toLowerCase(), gamerBase);
    }

    private final LoadingCache<String, OfflineGamer> OFFLINE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull OfflineGamer load(@NotNull String key) {
                    return new OfflineGamer(key);
                }
            });

    public IBaseGamer getByName(final String name) {
        return GAMERS.get(name.toLowerCase());
    }

    public void removeOfflinePlayer(final String name) {
        OFFLINE_CACHE.invalidate(name.toLowerCase());
    }

    public IBaseGamer getOrCreate(final String name) {
        IBaseGamer gamer = getByName(name);

        OFFLINE_CACHE.cleanUp();

        if (gamer == null) {
            try {
                gamer = OFFLINE_CACHE.get(name.toLowerCase());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (gamer != null && gamer.getPlayerID() == -1) {
            return null;
        }

        return gamer;
    }

    public IBaseGamer getById(final int id) {
        if (id == -1) {
            return null;
        }

        IBaseGamer gamer = OFFLINE_CACHE.asMap().values()
                            .stream()
                            .filter(offline -> offline.getPlayerID() == id)
                            .findFirst()
                            .orElse(null);

        if (gamer != null) {
            return gamer;
        }

        return getOrCreate(GlobalLoader.getName(id));
    }

    public void removeGamer(final String name) {
        GAMERS.remove(name.toLowerCase());
    }

    public void removeGamer(final OnlineGamer gamer) {
        if (gamer == null)
            return;

        removeGamer(gamer.getName());
    }

    public GamerBase getOnline(final int playerID) {
        for (val gamerBase : GAMERS.values()) {
            if (gamerBase.getPlayerID() == playerID)
                return gamerBase;
        }

        return null;
    }

    public boolean contains(final String name) {
        return GAMERS.containsKey(name.toLowerCase());
    }
}
