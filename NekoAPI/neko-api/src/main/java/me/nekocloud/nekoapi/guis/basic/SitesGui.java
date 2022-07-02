package me.nekocloud.nekoapi.guis.basic;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SitesGui extends Gui {

    public SitesGui(GuiDefaultContainer listener, Language lang) {
        super(listener, lang, "§0➲ §0§n" + lang.getMessage("TEST") + "§r");

        dInventory.createInventoryAction(new InventoryAction() {
            @Override
            public void onOpen(Player player) {
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

        dInventory.setItem(31, new DItem(ItemUtil.getBuilder(Material.COMPASS)
                .setName("ПРИВЕТ")
                .setLore("test")
                .build()));

    }
}
