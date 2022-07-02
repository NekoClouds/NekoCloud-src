package me.nekocloud.box.type;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.locale.Language;
import me.nekocloud.box.api.ItemBox;
import org.bukkit.inventory.ItemStack;

public class ItemsBox extends ItemBox {

    public ItemsBox(ItemStack icon, Rarity rarity) {
        super(icon, rarity);
    }

    @Override
    public String getName(Language lang) {
        return null;
    }

    @Override
    public void onApply(BukkitGamer gamer) {

    }

    @Override
    public void onMessage(BukkitGamer gamer) {

    }
}
