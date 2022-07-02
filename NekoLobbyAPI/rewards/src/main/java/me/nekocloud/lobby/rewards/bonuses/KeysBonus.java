package me.nekocloud.lobby.rewards.bonuses;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;

public class KeysBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = getRandomCount(gamer.getGroup());
        Language lang = gamer.getLanguage();

        gamer.changeKeys(KeyType.GAME_KEY, amount);

        if (chat) {
            gamer.sendMessage("   " + lang.getMessage(
                    "KEYS_REWARD_LOCALE",
                    StringUtil.getNumberFormat(amount),
                    CommonWords.KEYS_1.convert(amount, lang)));
        }
    }

    private int getRandomCount(Group group) {
        int baseBounds = 3;
        int minBounds = 1;

        //todo разные ключи на разные уровни наград

        baseBounds += group.getLevel();

        return RANDOM.nextInt(baseBounds) + minBounds;
    }

}
