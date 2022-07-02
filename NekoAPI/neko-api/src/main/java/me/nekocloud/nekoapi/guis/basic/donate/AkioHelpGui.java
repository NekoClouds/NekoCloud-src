package me.nekocloud.nekoapi.guis.basic.donate;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import me.nekocloud.nekoapi.guis.basic.Gui;

public class AkioHelpGui extends Gui {

    public AkioHelpGui(GuiDefaultContainer container, Language lang) {
        super(container, lang, "§0➲ §0§n" + lang.getMessage("GUI_DONATE_GUI_NAME") + "§r" + Group.AKIO.getNameEn());
    }

    @Override
    protected void setItems() {
        if (SVersionUtil.is1_12()) {
            setGlassItems();
        } else {
            setGlassItemsNew();
        }
        setBackItem();
        dInventory.setItem(9 + 5 - 1, new DItem(ItemUtil.getBuilder(Head.MAINLOBBY.getHead())
                .setName(lang.getMessage("GUI_DONATE_AKIO_HUB_NAME"))
                .setLore(lang.getList("GUI_DONATE_AKIO_HUB_LORE"))
                .build()));
        dInventory.setItem(4, 4, new DItem(ItemUtil.getBuilder(Head.ANARCHY.getHead())
                .setName(lang.getMessage("GUI_DONATE_AKIO_ANARCHY_NAME"))
                .setLore(lang.getList("GUI_DONATE_AKIO_ANARCHY_LORE"))
                .build()));
        dInventory.setItem(6, 4, new DItem(ItemUtil.getBuilder(Head.GRIEF.getHead())
                .setName(lang.getMessage("GUI_DONATE_AKIO_GRIEF_NAME"))
                .setLore(lang.getList("GUI_DONATE_AKIO_GRIEF_LORE"))
                .build()));
    }
}
