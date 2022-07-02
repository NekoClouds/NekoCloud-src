package me.nekocloud.lobby.profile.gui.guis;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.api.usableitem.ClickAction;
import me.nekocloud.api.usableitem.ClickType;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.api.profile.ProfileGui;
import me.nekocloud.lobby.profile.hider.HiderItem;
import me.nekocloud.nekoapi.guis.CustomItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SettingsPage extends ProfileGui {

    private static final ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();

    public SettingsPage(Player player) {
        super(player, "PROFILE_MAIN_ITEM_SETTINGS_NAME");
    }

    @Override
    protected void setItems() {
        setBackItem();

        setItem(inventory, 0,19, gamer, ItemUtil.getBuilder(Material.INK_SACK)
                .setDurability((short) 1)
                .build(), SettingsType.BLOOD, Group.HEGENT);
        setItem(inventory, 0,20, gamer, new ItemStack(Material.FEATHER), SettingsType.FLY, Group.AKIO,
                (clicker, clickType, block) -> {
            if (!clicker.getWorld().getName().equals("lobby")) {
                return;
            }

            clicker.setAllowFlight(gamer.getSetting(SettingsType.FLY));
            clicker.setFlying(gamer.getSetting(SettingsType.FLY));
        });
        setItem(inventory,0, 21, gamer, new ItemStack(Material.WATCH), SettingsType.HIDER, (clicker, clickType, block) ->
                HiderItem.setSettings(clicker, gamer.getSetting(SettingsType.HIDER)));
        setItem(inventory, 0,22, gamer, new ItemStack(Material.PAPER), SettingsType.CHAT);
        setItem(inventory, 0,23, gamer, new ItemStack(Material.JUKEBOX), SettingsType.MUSIC, Group.TRIVAL);
        setItem(inventory, 0,24, gamer, new ItemStack(Material.SIGN), SettingsType.BOARD, (clicker, clickType, block) -> {
            val boardLobby = LobbyAPI.getBoardLobby();
            if (boardLobby == null) {
                return;
            }

            boolean enable = gamer.getSetting(SettingsType.BOARD);
            if (enable) {
                boardLobby.showBoard(gamer, gamer.getLanguage());
                return;
            }

            val board = SCORE_BOARD_API.getBoard(clicker);
            if (board == null) {
                return;
            }

            board.remove();
        });

        setItem(inventory, 0, 25, gamer, new ItemStack(Material.BOOK), SettingsType.PRIVATE_MESSAGE);

        setItem(inventory, 1, 19, gamer, gamer.getHead(), SettingsType.FRIENDS_REQUEST);
        setItem(inventory, 1, 20, gamer, ItemUtil.getBuilder(Material.SKULL_ITEM)
                .setDurability((short) 3)
                .build(), SettingsType.PARTY_REQUEST);
        setItem(inventory, 1, 21, gamer, new ItemStack(Material.ENDER_PORTAL_FRAME), SettingsType.GUILD_REQUEST);
        setItem(inventory, 1, 22, gamer, new ItemStack(Material.EXP_BOTTLE), SettingsType.DONATE_CHAT, Group.HEGENT);
        setItem(inventory, 1, 23, gamer, ItemUtil.getBuilder(Head.LUCKY)
                .setDurability((short) 3)
                .build(), SettingsType.GUI_SOUNDS);

        INVENTORY_API.pageButton(lang, InventoryUtil.getPagesCount(SettingsType.values().length, 14),
                inventory, 48, 50);
    }

    private static void setItem(MultiInventory inventory,
                                int page,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType,
                                Group minGroup) {
        setItem(inventory, page, slot, gamer, itemStack, settingsType, minGroup, null);
    }

    private static void setItem(MultiInventory inventory,
                                int page,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType) {
        setItem(inventory, page, slot, gamer, itemStack, settingsType, Group.DEFAULT);
    }

    private static void setItem(MultiInventory inventory,
                                int page,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType,
                                ClickAction clickAction) {
        setItem(inventory, page, slot, gamer, itemStack, settingsType, Group.DEFAULT, clickAction);
    }

    private static void setItem(MultiInventory inventory,
                                int page,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType,
                                Group minGroup,
                                ClickAction clickAction) {
        val lang = gamer.getLanguage();
        boolean enable = gamer.getGroup().getLevel() >= minGroup.getLevel();

        List<String> lore = new ArrayList<>(lang.getList("LOBBY_SETTINGS_" + settingsType.name() + "_LORE"));
        if (!enable)
            lore.addAll(lang.getList("LOBBY_SETTINGS_UNAVAILABLE", minGroup.getNameEn()));

        inventory.setItem(page, slot, new DItem(ItemUtil.getBuilder(itemStack)
                .removeFlags()
                .setName((enable ? "§e" : "§c")
                        + lang.getMessage("LOBBY_SETTINGS_" + settingsType.name() + "_NAME"))
                .setLore(lore)
                .build()));

        inventory.setItem(page, slot + 9, new DItem(getEnableOrDisableItem(gamer, settingsType),
                (clicker, clickType, i) -> {
            if (Cooldown.hasOrAddCooldown(gamer, "click", 5)) {
                return;
            }

            if (!enable) {
                SOUND_API.play(clicker, SoundType.NO);
                return;
            }

            gamer.setSetting(settingsType, !gamer.getSetting(settingsType));
            SOUND_API.play(clicker, SoundType.POP);

            if (clickAction != null) {
                clickAction.onClick(clicker, (clickType.isRightClick() ? ClickType.RIGHT : ClickType.LEFT), null);
            }

            if (inventory.getInventories().isEmpty()) {
                return;
            }

            val dItem = inventory.getInventories().get(0).getItems().get(slot + 9);
            if (dItem == null) {
                return;
            }

            dItem.setItem(getEnableOrDisableItem(gamer, settingsType));
            inventory.setItem(page, slot + 9, dItem);
        }));


    }

    private static ItemStack getEnableOrDisableItem(BukkitGamer gamer, SettingsType settingsType) {
        val lang = gamer.getLanguage();
        return (gamer.getSetting(settingsType) ? CustomItems.getEnable(lang) : CustomItems.getDisable(lang));
    }
}
