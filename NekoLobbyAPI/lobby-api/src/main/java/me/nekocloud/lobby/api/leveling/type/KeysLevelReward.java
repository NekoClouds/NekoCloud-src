package me.nekocloud.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class KeysLevelReward extends LevelReward {

    private final KeyType keyType;
    private final int amount;

    @Override
    public void giveReward(BukkitGamer gamer) {
        gamer.changeKeys(keyType, amount);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ §d" + StringUtil.getNumberFormat(amount)
                + " §7" + keyType.getName(language).substring(2);
    }

    @Override
    public int getPriority() {
        return 100 + keyType.getId();
    }
}
