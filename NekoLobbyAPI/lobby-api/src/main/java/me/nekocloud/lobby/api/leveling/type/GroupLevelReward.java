package me.nekocloud.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.box.type.GroupBox;
import me.nekocloud.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class GroupLevelReward extends LevelReward {

    private final Group group;

    @Override
    public void giveReward(BukkitGamer gamer) {
        if (gamer.getGroup().getId() >= group.getId()) {
            gamer.changeMoney(PurchaseType.VIRTS, GroupBox.getMoney(group));
            return;
        }

        gamer.setGroup(group);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ " + group.getNameEn() + " §8(§7" + language.getMessage("GROUP") + "§8)";
    }

    @Override
    public int getPriority() {
        return 1000 + group.getId();
    }
}
