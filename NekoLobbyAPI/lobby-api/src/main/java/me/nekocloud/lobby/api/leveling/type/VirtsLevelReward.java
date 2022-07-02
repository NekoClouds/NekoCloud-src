package me.nekocloud.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class VirtsLevelReward extends LevelReward {

    private final int virts;

    @Override
    public void giveReward(BukkitGamer gamer) {
        gamer.changeMoney(PurchaseType.COINS, virts);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ §6" + StringUtil.getNumberFormat(virts)
                + " §7" + CommonWords.VIRTS_1.convert(virts, language);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
