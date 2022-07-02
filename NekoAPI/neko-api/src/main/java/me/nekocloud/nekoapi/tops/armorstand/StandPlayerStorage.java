package me.nekocloud.nekoapi.tops.armorstand;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class StandPlayerStorage {

    private final Map<String, StandPlayer> players = new ConcurrentHashMap<>();

    void addPlayer(final StandPlayer standPlayer) {
        players.put(standPlayer.getGamer().getName().toLowerCase(), standPlayer);
    }

    void removePlayer(final String name) {
        players.remove(name);
    }

    @Nullable
    public StandPlayer getPlayer(final @NotNull BukkitGamer gamer) {
        return players.get(gamer.getName().toLowerCase());
    }
}
