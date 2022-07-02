package me.nekocloud.nekoapi.donatemenu.guis;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.donatemenu.DonateMenuData;
import me.nekocloud.nekoapi.guis.CustomItems;
import org.bukkit.entity.Player;

public final class MainDonateMenuGui extends DonateMenuGui {

    public MainDonateMenuGui(Player player, DonateMenuData donateMenuData, Language language) {
        super(player, donateMenuData, "§0➲ §0§n" + language.getMessage("DONATE_MENU_NAME") + "§r");
    }

    @Override
    protected void setItems(BukkitGamer gamer) {
        setGlassItems();

        val lang = gamer.getLanguage();

        if (NekoCloud.isHub() || NekoCloud.isLobby()) {
            inventory.setItem(49, new DItem(CustomItems.getBack(lang),
                    (player, clickType, slot) -> {
                SOUND_API.play(player, SoundType.PICKUP);
                player.chat("/profile");
            }));
        }

        inventory.setItem(20, new DItem(ItemUtil.getBuilder(Head.BOOKS)
                .setName("§d" + lang.getMessage("DONATE_MENU_FAST_MESSAGE_NAME"))
                .setLore(lang.getList("DONATE_MENU_FAST_MESSAGE_LORE"))
                .removeFlags()
                .build(), (player, clickType, slot) -> {
            val gui = donateMenuData.get(FastMessageGui.class);
            if (gui != null) {
                gui.open();
            }
        }));

        inventory.setItem(22, new DItem(ItemUtil.getBuilder(Head.PREFIX)
                .setName("§d" + lang.getMessage("DONATE_MENU_PREFIX_NAME"))
                .setLore(lang.getList("DONATE_MENU_PREFIX_LORE", gamer.getChatName()))
                .removeFlags()
                .build(), (player, clickType, slot) -> {
            val gui = donateMenuData.get(PrefixGui.class);
            if (gui != null) {
                gui.open();
            }
        }));

        inventory.setItem(24, new DItem(ItemUtil.getBuilder(Head.JOIN_MESSAGE)
                .setName("§d" + lang.getMessage( "DONATE_MENU_JOIN_MESSAGE_NAME"))
                .setLore(lang.getList("DONATE_MENU_JOIN_MESSAGE_LORE"))
                .removeFlags()
                .build(), (player, clickType, slot) -> {
            val gui = donateMenuData.get(JoinMessageGui.class);
            if (gui != null) {
                gui.open();
            }
        }));

        inventory.setItem(30, new DItem(ItemUtil.getBuilder(Head.COMPUTER)
                .setName("§d" + lang.getMessage( "DONATE_MENU_TITUL_NAME"))
                .setLore(lang.getList("DONATE_MENU_TITUL_LORE"))
                .removeFlags()
                .build(), (player, clickType, slot) -> {
            val gui = donateMenuData.get(JoinMessageGui.class);
            if (gui != null) {
                gui.open();
            }
        }));

        inventory.setItem(32, new DItem(ItemUtil.getBuilder(Head.COIN)
                .setName("§d" + lang.getMessage( "DONATE_MENU_GLOW_NAME"))
                .setLore(lang.getList("DONATE_MENU_GLOW_LORE"))
                .removeFlags()
                .build(), (player, clickType, slot) -> {
            val gui = donateMenuData.get(JoinMessageGui.class);
            if (gui != null) {
                gui.open();
            }
        }));

    }
}
