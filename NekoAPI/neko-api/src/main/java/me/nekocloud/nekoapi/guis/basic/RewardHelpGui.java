package me.nekocloud.nekoapi.guis.basic;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import org.bukkit.Material;

public class RewardHelpGui extends Gui {

    public RewardHelpGui(GuiDefaultContainer listener, Language lang) {
        super(listener, lang, "§0➲ §0§n" + lang.getMessage("GUI_REWARDS_NAME") + "§r");
    }

    @Override
    protected void setItems() {
        if (SVersionUtil.is1_12()) {
            setGlassItems();
        } else {
            setGlassItemsNew();
        }

        dInventory.setItem(40, new DItem(CustomItems.getBack2(lang),
                (player, clickType, slot) -> {
            SOUND_API.play(player, SoundType.PICKUP);
            listener.openGui(HelpGui.class, player);
        }));

        dInventory.setItem(9 + 3 - 1, new DItem(ItemUtil.getBuilder(Head.BATMAN.getHead())
                .setName("§aDuels")
                .setLore(lang.getList("GUI_REWARDS_ITEM_1_LORE"))
                .build()));

        dInventory.setItem(9 * 3 + 5 - 1, new DItem(ItemUtil.getBuilder(Material.BOOK)
                .setName(lang.getMessage( "GUI_REWARDS_ITEM_INFO_NAME"))
                .setLore(lang.getList("GUI_REWARDS_ITEM_INFO_LORE"))
                .build()));
    }
}
