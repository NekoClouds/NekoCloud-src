package me.nekocloud.skyblock.achievement.type;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.achievements.achievement.Achievement;
import me.nekocloud.nekoapi.achievements.achievement.AchievementPlayer;
import me.nekocloud.nekoapi.achievements.achievement.AchievementPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IslandAchievement extends Achievement {

    public IslandAchievement(int id, ItemStack itemStack, String name) {
        super(id, itemStack, "ISLAND_ACHIEVEMENT_" + name + "_NAME"
                , "ISLAND_ACHIEVEMENT_" + name + "_LORE");
    }

    public IslandAchievement(int id, ItemStack itemStack, String nameKey, String loreKey) {
        super(id, itemStack, nameKey, loreKey);
    }

    @Override
    public int getPercent(AchievementPlayer achievementPlayer) {
        return 0;
    }

    @Override
    protected void complete(BukkitGamer player) {
        //noting
    }

    protected int getCompleteAmount(AchievementPlayer achievementPlayer, String key) {
        AchievementPlayerData achievementPlayerData = achievementPlayer.getAchievementsData(this);
        if (achievementPlayerData == null)
            return 0;

        return achievementPlayerData.getLocalInfo().getOrDefault(key, 0);
    }

    @Override
    public int getPoints() {
        return 0;
    }
}
