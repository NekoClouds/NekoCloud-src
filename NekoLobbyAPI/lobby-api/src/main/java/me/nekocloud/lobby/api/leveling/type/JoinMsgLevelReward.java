package me.nekocloud.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.JoinMessage;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class JoinMsgLevelReward extends LevelReward {

    private final JoinMessage joinMessage;

    @Override
    public void giveReward(BukkitGamer gamer) {
        gamer.addJoinMessage(joinMessage);
    }

    @Override
    public String getLore(Language language) {
        return language.getMessage("REWARD_JOIN_MESSAGE", joinMessage);
    }
}
