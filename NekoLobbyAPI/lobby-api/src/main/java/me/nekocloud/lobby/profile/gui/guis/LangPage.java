package me.nekocloud.lobby.profile.gui.guis;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.api.profile.ProfileGui;
import org.bukkit.entity.Player;

public class LangPage extends ProfileGui {

    public LangPage(Player player) {
        super(player, "PROFILE_MAIN_ITEM_LANG_NAME");
    }

    @Override
    protected void setItems() {
        setBackItem();
        setGlassItems();

        int slot = 20;
        for (Language language : Language.VALUES) {
            boolean enable = language == lang;
            val head = Head.valueOf(language.getHeadName());
            inventory.setItem(slot++, new DItem(ItemUtil.getBuilder(head)
                    .setName((enable ? "§a" : "§c") + language.getName())
                    .setLore(lang.getMessage("PROFILE_LANG_ITEM_LORE1", language.getName()))
                    .addLore("")
                    .addLore(enable ? lang.getMessage( "PROFILE_LANG_ITEM_LORE3")
                            : lang.getMessage( "PROFILE_LANG_ITEM_LORE2"))
                    .build(), (clicker, clickType, i) -> {
                if (enable) {
                    SOUND_API.play(clicker, SoundType.NO);
                    return;
                }

                SOUND_API.play(clicker, SoundType.DESTROY);
                gamer.setLanguage(language);
                clicker.closeInventory();

                this.update();
            }));
        }
    }
}
