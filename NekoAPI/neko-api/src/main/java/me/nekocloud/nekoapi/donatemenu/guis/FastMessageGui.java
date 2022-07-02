package me.nekocloud.nekoapi.donatemenu.guis;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.donatemenu.DonateMenuData;
import me.nekocloud.nekoapi.donatemenu.FastMessage;
import org.bukkit.entity.Player;

import java.util.Map;

public final class FastMessageGui extends DonateMenuGui {

    public FastMessageGui(Player player, DonateMenuData donateMenuData, Language language) {
        super(player, donateMenuData, "§0➲ §0§n" + language.getMessage("DONATE_MENU_NAME")
                + "§r §0| " + language.getMessage("DONATE_MENU_FAST_MESSAGE_NAME"));
    }

    @Override
    protected void setItems(BukkitGamer gamer) {
        setBack(donateMenuData.get(MainDonateMenuGui.class));
        setGlassItems();

        val lang = gamer.getLanguage();
        val enable = gamer.isAkio();

        int slot = 11;
        int page = 0;
        for (Map.Entry<String, FastMessage> entry : FastMessage.getMessages(lang).entrySet()) {
            val name = entry.getKey();
            val fm = entry.getValue();

            inventory.setItem(page, slot++, new DItem(ItemUtil
                    .getBuilder(enable ? Head.BOOKS.getHead() : NO_PERMS.clone())
                    .setName((enable ? "§b" : "§c") + name)
                    .setLore(lang.getMessage("FAST_MESSAGE_LORE1", name + " " + fm.getSmile()))
                    .addLore(enable ?
                            lang.getList("FAST_MESSAGE_LORE2") :
                            lang.getList("LOBBY_SETTINGS_UNAVAILABLE", fm.getGroup().getNameEn()))
                    .build(), (clicker, clickType, slot1) -> {
                if (!enable) {
                    SOUND_API.play(clicker, SoundType.NO);
                    return;
                }
                clicker.chat("/fm " + name);
                player.closeInventory();
            }));

            if ((slot - 7) % 9 == 0) {
                slot += 4;
            }

            if (slot >= 43) {
                slot = 11;
                page++;
            }
        }
    }
}
