package me.nekocloud.box.type;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.box.api.ItemBox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MoneyBox extends ItemBox {

    private final int money;

    public MoneyBox(int money, Rarity rarity) {
        super(new ItemStack(Material.DOUBLE_PLANT), rarity);
        this.money = money;
    }

    @Override
    public String getName(Language lang) {
        return lang.getMessage("BOX_MONEY", StringUtil.getNumberFormat(money));
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        gamer.changeMoney(PurchaseType.COINS, money);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        gamer.sendActionBar("§e+" + money + " "
                + CommonWords.COINS_1.convert(money, gamer.getLanguage()));
    }
}
