package me.nekocloud.lobby.rewards.bonuses;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;

public class MoneyBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = getRandomCount(type, gamer.getGroup());
        Language lang = gamer.getLanguage();

        gamer.changeMoney(PurchaseType.COINS, amount);

        if (chat) {
            gamer.sendMessage("   " + lang.getMessage("MONEY_REWARD_LOCALE",
                    StringUtil.getNumberFormat(amount),
                    CommonWords.COINS_1.convert(amount, lang)));
        }
    }

    private int getRandomCount(RewardType type, Group group) {
        int baseBounds = 230;
        int minBounds = 150;

        if (type == RewardType.THIRD) {
            baseBounds = 300;
            minBounds = 200;
        } else if (type == RewardType.FIFTH) {
            baseBounds = 500;
            minBounds = 200;
        } else if (type == RewardType.SEVENTH) {
            baseBounds = 700;
            minBounds = 250;
        }

        baseBounds += group.getLevel() * 100;

        return RANDOM.nextInt(baseBounds) + minBounds;
    }
}
