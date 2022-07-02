package me.nekocloud.nekoapi.achievements.manager;

import lombok.Getter;
import me.nekocloud.nekoapi.achievements.achievement.Achievement;
import me.nekocloud.nekoapi.achievements.listeners.AchievementPlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class AchievementManager {

    private final AchievementPlayerManager playerManager = new AchievementPlayerManager();

    private final Map<Integer, Achievement> achievements = new ConcurrentHashMap<>();

    private final JavaPlugin javaPlugin;
    private final AchievementSql achievementSql;
    private final String texture = "textures/blocks/log_spruce.png"; //todo сделать сеттинг этой херни

    private boolean loadOnJoin;

    public AchievementManager(JavaPlugin javaPlugin, String dataBase, String host) {
        this.javaPlugin = javaPlugin;
        achievementSql = new AchievementSql(this, dataBase, host);

        new AchievementPlayerListener(playerManager, this, javaPlugin);
    }

    public void setLoadOnJoin(boolean loadOnJoin) {
        this.loadOnJoin = loadOnJoin;
    }

    public void addAchievements(List<Achievement> list) {
        list.forEach(achievement -> achievements.putIfAbsent(achievement.getId(), achievement));
    }

    public void addAchievements(Achievement achievement) {
        achievements.putIfAbsent(achievement.getId(), achievement);
    }

    public int getAllPoints() {
        return (int) achievements.values()
                .stream()
                .map(Achievement::getPoints)
                .count();
    }

    public Map<Integer, Achievement> getAchievements() {
        return new HashMap<>(achievements);
    }

    public  <T extends Achievement> T getAchievement(int id) {
        return (T) achievements.get(id);
    }

}
