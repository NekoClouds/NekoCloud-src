package me.nekocloud.nekoapi.guis.basic;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import me.nekocloud.nekoapi.guis.basic.donate.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DonateGui extends Gui {

    public DonateGui(GuiDefaultContainer listener, Language lang) {
        super(listener, lang, "§0➲ §0§n" + lang.getMessage( "GUI_DONATE_MAIN_NAME") + "§r");
        dInventory.createInventoryAction(new InventoryAction() {
            @Override
            public void onOpen(final Player player) {
                if (NekoCloud.isHub() || NekoCloud.isLobby()) {
                    dInventory.setItem(49, new DItem(CustomItems.getBack2(lang),
                            (player1, clickType, slot) -> {
                                SOUND_API.play(player, SoundType.PICKUP);
                                player.chat("/profile");
                            }
                    ));
                }
            }
        });
    }

    @Override
    protected void setItems() {
        if (SVersionUtil.is1_12()) {
            setGlassItems();
        } else {
            setGlassItemsNew();
        }

        dInventory.setItem(9 + 3 - 1, new DItem(ItemUtil.getBuilder(Head.EGG_ORB_RAINBOW.getHead())
                .setName("§b" + lang.getMessage( "GUI_DONATE_ITEM_1_NAME") + Group.NEKO.getNameEn())
                .setLore(lang.getList("GUI_DONATE_ITEM_1_LORE", 2100))
                .build(), (clicker, clickType, slot) -> listener.openGui(NekoHelpGui.class, clicker)));

        dInventory.setItem(9 + 4 - 1, new DItem(ItemUtil.getBuilder(Head.EGG_ORB_RED.getHead())
                .setName("§b" + lang.getMessage("GUI_DONATE_ITEM_1_NAME") + Group.AXSIDE.getNameEn())
                .setLore(lang.getList("GUI_DONATE_ITEM_1_LORE", 900))
                .build(), (clicker, clickType, slot) -> listener.openGui(AxideHelpGui.class, clicker)));

        dInventory.setItem(9 + 5 - 1, new DItem(ItemUtil.getBuilder(Head.EGG_ORB_AQUA.getHead())
                .setName("§b" + lang.getMessage("GUI_DONATE_ITEM_1_NAME") + Group.TRIVAL.getNameEn())
                .setLore(lang.getList("GUI_DONATE_ITEM_1_LORE", 590))
                .build(), (clicker, clickType, slot) -> listener.openGui(TrivalHelpGui.class, clicker)));

        dInventory.setItem(9 + 6 - 1, new DItem(ItemUtil.getBuilder(Head.EGG_ORB_YELLOW.getHead())
                .setName("§b" + lang.getMessage("GUI_DONATE_ITEM_1_NAME") + Group.AKIO.getNameEn())
                .setLore(lang.getList("GUI_DONATE_ITEM_1_LORE", 300))
                .build(), (clicker, clickType, slot) -> listener.openGui(AkioHelpGui.class, clicker)));

        dInventory.setItem(9 + 7 - 1, new DItem(ItemUtil.getBuilder(Head.EGG_ORB_BLUE.getHead())
                .setName("§b" + lang.getMessage( "GUI_DONATE_ITEM_1_NAME") + Group.HEGENT.getNameEn())
                .setLore(lang.getList("GUI_DONATE_ITEM_1_LORE", 100))
                .build(), (clicker, clickType, slot) -> listener.openGui(HegentHelpGui.class, clicker)));

        dInventory.setItem(9 * 3 + 4 - 1, new DItem(ItemUtil.getBuilder(Material.EXP_BOTTLE)
                .setName(lang.getMessage("GUI_DONATE_ITEM_2_NAME"))
                .setLore(lang.getList( "GUI_DONATE_ITEM_2_LORE"))
                .build()));

        dInventory.setItem(9 * 3 + 6 - 1, new DItem(ItemUtil.getBuilder(Material.BOOK)
                .setName(lang.getMessage( "GUI_DONATE_ITEM_3_NAME"))
                .setLore(lang.getList("GUI_DONATE_ITEM_3_LORE"))
                .build()));
    }
}
