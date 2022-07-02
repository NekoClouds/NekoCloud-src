package me.nekocloud.nekoapi.achievements.achievement;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.achievements.event.GamerAchievementCompleteEvent;
import me.nekocloud.nekoapi.achievements.manager.AchievementManager;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;

public final class AchievementPlayer {

    @Getter
    private final BukkitGamer gamer;

    private final AchievementManager achievementManager;

    private final Int2ObjectMap<Achievement> completed;
    private final Int2ObjectMap<AchievementPlayerData> achievementsData;

    public AchievementPlayer(BukkitGamer gamer,
                             AchievementManager achievementManager) {
        this.gamer = gamer;
        this.achievementManager = achievementManager;
        this.completed = achievementManager.getAchievementSql().getCompleteData(gamer);
        this.achievementsData = achievementManager.getAchievementSql().getAchievementsData(gamer);
    }

    public boolean hasAchievement(int id) {
        return completed.containsKey(id);
    }

    public boolean hasAchievement(@NonNull Achievement achievement) {
        return hasAchievement(achievement.getId());
    }

    public AchievementPlayerData getAchievementsData(Achievement achievement) {
        if (achievement == null || !achievementManager.getAchievements().containsKey(achievement.getId()))
            return null;

        AchievementPlayerData achievementPlayerData = achievementsData.get(achievement.getId());
        if (achievementPlayerData != null) {
            return achievementPlayerData;
        }

        achievementPlayerData = new AchievementPlayerData(achievement);
        achievementsData.put(achievement.getId(), achievementPlayerData);

        return achievementPlayerData;
    }

    public AchievementPlayerData getAchievementsData(int id) {
        return getAchievementsData(achievementManager.getAchievement(id));
    }

    public Int2ObjectMap<Achievement> getCompleted() {
        return Int2ObjectMaps.unmodifiable(completed);
    }

    public void complete(Achievement achievement) {
        if (hasAchievement(achievement) || gamer == null) {
            return;
        }

        GamerAchievementCompleteEvent event = new GamerAchievementCompleteEvent(gamer, achievement);
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        achievementManager.getAchievementSql().addComplete(gamer.getPlayerID(), achievementsData, achievement);

        completed.put(achievement.getId(), achievement);
        achievementsData.remove(achievement.getId());

        Language lang = gamer.getLanguage();
        gamer.sendMessage("");
        gamer.sendMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        gamer.sendMessage("                     " + lang.getMessage("ACHIEVEMENT_COMPLETE"));
        gamer.sendMessage("");
        gamer.sendMessage(StringUtil.stringToCenter("§a" + achievement.getName(lang).toUpperCase()));
        lang.getList(achievement.getLoreKey()).forEach(text -> gamer.sendMessage(StringUtil.stringToCenter(text)));
        gamer.sendMessage("");
        gamer.sendMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        gamer.sendMessage("");
        gamer.playSound(SoundType.CHEST_OPEN);

        achievement.complete(gamer);
    }

    public void save() {
        if (gamer == null) {
            return;
        }

        for (val entry : achievementsData.int2ObjectEntrySet()) {
            entry.getValue().save(gamer.getPlayerID(), achievementManager.getAchievementSql());
        }

    }
}
