package me.nekocloud.lobby.customitems;

import lombok.AllArgsConstructor;
import me.nekocloud.api.usableitem.ClickType;
import me.nekocloud.api.util.Head;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum TypeCustomItem {

    SERVER_MENU(new CustomItem(Head.GADGETS_ITEM,
            "ITEMS_LOBBY_SELECTOR_NAME",
            "ITEMS_LOBBY_SELECTOR_LORE",
            (player, clickType, block) -> {
                if (clickType == ClickType.RIGHT) {
                    player.chat("/gamemenu");
                }
            }))
    ;

    private final CustomItem customItem;

    public void givePlayer(Player player, int slot, Language lang) {
        customItem.givePlayer(player, slot, lang);
    }
}
