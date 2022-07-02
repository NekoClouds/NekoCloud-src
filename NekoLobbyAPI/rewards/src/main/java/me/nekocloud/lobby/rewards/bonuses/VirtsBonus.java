package me.nekocloud.lobby.rewards.bonuses;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;

public class VirtsBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = 1;
        Language lang = gamer.getLanguage();

        gamer.changeMoney(PurchaseType.VIRTS, amount);

        if (chat) {
            gamer.sendMessage("   " + lang.getMessage("MONEY_REWARD_LOCALE",
                    StringUtil.getNumberFormat(amount),
                    CommonWords.VIRTS_1.convert(amount, lang)));
        }
    }
}
