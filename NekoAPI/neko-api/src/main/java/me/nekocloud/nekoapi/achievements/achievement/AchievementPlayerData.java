package me.nekocloud.nekoapi.achievements.achievement;

import lombok.Getter;
import me.nekocloud.nekoapi.achievements.manager.AchievementSql;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * тут инфа о том, кто сколько выполнил от этой ачивки
 */
public final class AchievementPlayerData {

    private final Achievement achievement;

    private final Map<String, Integer> cachedInfo = new HashMap<>();
    @Getter
    private final Map<String, Integer> localInfo = new ConcurrentHashMap<>();

    public AchievementPlayerData(Achievement achievement) {
        this.achievement = achievement;
    }

    /**
     * при загрузке с БД
     */
    public void addCachedInfo(String key, int value) {
        cachedInfo.put(key, value);
        localInfo.put(key, value);
    }

    public boolean isEmpty() {
        return cachedInfo.isEmpty();
    }

    /**
     * когда работаем локально
     */
    public void addLocalInfo(String key, int value) {
        localInfo.put(key, value);
    }

    void save(int playerID, AchievementSql achievementSql) {
        localInfo.forEach((key, value1) -> {
            int value = value1;

            Integer cachedValue = cachedInfo.get(key);
            if (cachedValue == null) {
                achievementSql.insertData(playerID, achievement, key, value);
                return;
            }

            if (cachedValue != value)
                achievementSql.updateData(playerID, achievement, key, value);

        });
    }
}
