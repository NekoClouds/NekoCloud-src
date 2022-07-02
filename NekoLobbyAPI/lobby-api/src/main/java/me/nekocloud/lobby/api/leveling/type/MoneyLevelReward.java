package me.nekocloud.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class MoneyLevelReward extends LevelReward {

    private final int money;

    @Override
    public void giveReward(BukkitGamer gamer) {
        gamer.changeMoney(PurchaseType.COINS, money);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ §6" + StringUtil.getNumberFormat(money)
                + " §7" + CommonWords.COINS_2.convert(money, language);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
