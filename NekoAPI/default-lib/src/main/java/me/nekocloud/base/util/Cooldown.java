package me.nekocloud.base.util;

import lombok.experimental.UtilityClass;
import me.nekocloud.base.gamer.IBaseGamer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class Cooldown {
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, Map<String, Long>> PLAYERS = new ConcurrentHashMap<>();
    
    public void addCooldown(final String playerName, final long ticks) {
        addCooldown(playerName, "global", ticks);
    }

    public void addCooldown(final @NotNull IBaseGamer gamer, final long ticks) {
        addCooldown(gamer.getName(), ticks);
    }

    public boolean hasCooldown(final String playerName) {
        return hasCooldown(playerName, "global");
    }

    public boolean hasCooldown(final @NotNull IBaseGamer gamer) {
        return hasCooldown(gamer.getName(), "global");
    }

    public boolean hasCooldown(final @NotNull IBaseGamer gamer, String type) {
        return hasCooldown(gamer.getName(), type);
    }

    /**
     * Проверить наличие клоудауна 
     * @param playerName ник игрока
     * @param type       тип
     * @return true/false
     */
    public boolean hasCooldown(final String playerName, final String type) {
        if (playerName == null || type == null) {
            return false;
        }

        final String name = playerName.toLowerCase();
        return PLAYERS.containsKey(name) && PLAYERS.get(name).containsKey(type.toLowerCase());
    }

    /**
     * Получить время клоудауна
     * @param playerName ник игрока
     * @param type       тип
     * @return cooldown time in seconds
     */
    public int getSecondCooldown(final String playerName, final String type) {
        if (!hasCooldown(playerName, type)) {
            return 0;
        }
        final String name = playerName.toLowerCase();
        final Map<String, Long> cooldownData = PLAYERS.get(name);
        if (cooldownData == null) {
            return 0;
        }
        Long startTime = cooldownData.get(type.toLowerCase());
        if (startTime == null) {
            return 0;
        }
        int time = (int) ((startTime - System.currentTimeMillis()) / 50 / 20);
        return (time == 0 ? 1 : time);
    }

    /**
     * Получить время клоудауна
     * @param gamer игрок
     * @param type  тип
     * @return cooldown time in seconds
     */
    public int getSecondCooldown(final @NotNull IBaseGamer gamer, final String type) {
        return getSecondCooldown(gamer.getName(), type);
    }

    /**
     * Поставить клоудаун игроку
     * @param playerName ник
     * @param type тип
     * @param ticks время в тиках
     */
    public void addCooldown(final @NotNull String playerName, final String type, final long ticks) {
        final String name = playerName.toLowerCase();
        final long time = System.currentTimeMillis() + ticks * 50;

        Map<String, Long> cooldownData = PLAYERS.get(name);
        if (cooldownData == null) {
            cooldownData = new ConcurrentHashMap<>();
            cooldownData.put(type.toLowerCase(), time);
            PLAYERS.put(name, cooldownData);
            return;
        }

        if (cooldownData.containsKey(type.toLowerCase()))
            return;

        cooldownData.put(type.toLowerCase(), time);
    }

    /**
     * Поставить клоудаун игроку
     * @param gamer игрок
     * @param type тип
     * @param ticks время в тиках
     */
    public void addCooldown(final @NotNull IBaseGamer gamer, final String type, final long ticks) {
        addCooldown(gamer.getName(), type, ticks);
    }

    /**
     * если есть кулдаун, вернет true
     * если его нет, то просто добавит его
     * @return - есть сейчас или нет кулдауна
     */
    public boolean hasOrAddCooldown(final IBaseGamer gamer, final String type, final long tick) {
        if (!hasCooldown(gamer, type)) {
            addCooldown(gamer, type, tick);
            return false;
        }

        return true;
    }

    static {
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> PLAYERS.forEach((name, data) -> {
            data.forEach((type, time) -> {
                if (time < System.currentTimeMillis()) {
                    data.remove(type);
                }
            });
            if (data.isEmpty()) {
                PLAYERS.remove(name);
            }
        }), 0, 50, TimeUnit.MILLISECONDS);
    }
}
