package me.nekocloud.box.type;

import io.netty.util.internal.ConcurrentSet;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.box.api.ItemBox;
import org.bukkit.Material;

import java.util.Set;

public class GroupBox extends ItemBox {

    public static final Set<String> CHANGED_GROUPS = new ConcurrentSet<>();

    private final Group group;

    public GroupBox(Group group, int color, Rarity rarity) {
        super(ItemUtil.getBuilder(Material.STAINED_GLASS)
                .setDurability((short) color)
                .build(), rarity);
        this.group = group;
    }

    @Override
    public String getName(Language lang) {
        return group.getNameEn() + " §f(" + lang.getMessage("GROUP") + "§f)" ;
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        if (gamer.getGroup().getId() >= group.getId()) {
            gamer.changeMoney(PurchaseType.VIRTS, getMoney(group));
            return;
        }

        CHANGED_GROUPS.add(gamer.getName().toLowerCase());
        gamer.setGroup(group);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        if (CHANGED_GROUPS.remove(gamer.getName().toLowerCase())) {
            return;
        }

        Language lang = gamer.getLanguage();
        int money = getMoney(group);
        String moneyString = CommonWords.VIRTS_1.convert(money, lang);
        gamer.sendMessage(lang.getMessage("GROUP_ALREADY_HAVE",
                StringUtil.getNumberFormat(money), moneyString));
    }

    public static int getMoney(Group group) {
        int money = 10;
        switch (group) {
            default:
                return money;
            case AKIO:
            case TRIVAL:
                return money + 10;
            case NEKO:
            case AXSIDE:
                return money + 30;
        }
    }
}
