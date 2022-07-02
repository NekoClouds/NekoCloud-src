package me.nekocloud.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class MessageLevelReward extends LevelReward {

    private final String key;

    @Override
    public void giveReward(BukkitGamer gamer) {
        //nothing
    }

    @Override
    public String getLore(Language language) {
        return language.getMessage(key);
    }
}
