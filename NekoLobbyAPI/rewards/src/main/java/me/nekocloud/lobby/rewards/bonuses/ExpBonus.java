package me.nekocloud.lobby.rewards.bonuses;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.utils.LevelUtils;

public class ExpBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = getRandomCount(type, gamer.getGroup());
        Language lang = gamer.getLanguage();

        gamer.addExp(amount);

        LevelUtils.setExpData(gamer);

        if (chat) {
            gamer.sendMessage("   " + lang.getMessage(
                    "EXP_REWARD_LOCALE", StringUtil.getNumberFormat(amount)));
        }
    }

    private int getRandomCount(RewardType type, Group group) {
        int baseBounds = 50;
        int minBounds = 200;

        if (type == RewardType.SECOND) {
            baseBounds = 200;
            minBounds = 250;
        } else if (type == RewardType.THIRD) {
            baseBounds = 300;
            minBounds = 1500;
        } else if (type == RewardType.FOURTH) {
            baseBounds = 400;
            minBounds = 350;
        } else if (type == RewardType.FIFTH) {
            baseBounds = 500;
            minBounds = 400;
        } else if (type == RewardType.SIXTH) {
            baseBounds = 600;
            minBounds = 450;
        } else if (type == RewardType.SEVENTH) {
            baseBounds = 700;
            minBounds = 500;
        }

        baseBounds += group.getLevel() * 50;

        return RANDOM.nextInt(baseBounds) + minBounds;
    }
}
