package me.nekocloud.skyblock.gui.guis;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.skyblock.gui.GuiUtil;
import me.nekocloud.skyblock.api.SkyBlockGui;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.member.IgnoredPlayer;
import me.nekocloud.skyblock.api.island.member.MemberType;
import me.nekocloud.skyblock.module.IgnoreModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IgnoreGui extends SkyBlockGui {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private final MultiInventory inventory;

    public IgnoreGui(Player player) {
        super(player, "");
        inventory = API.createMultiInventory(player,
                lang.getMessage(GUI_NAME_SKY_BLOCK) + " ▸ "
                        + lang.getMessage("ISLAND_IGNORED_GUI_NAME"), 5);
    }

    @Override
    protected void setItems(Player clicker) {
        Island island = ISLAND_MANAGER.getIsland(clicker);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(clicker);
        if (gamer == null || island == null || inventory == null)
            return;

        inventory.clearInventories();

        GuiUtil.setBack(inventory, lang);

        int slot = 10;
        int page = 0;

        MemberType memberType = island.getMemberType(gamer);

        IgnoreModule ignoreModule = island.getModule(IgnoreModule.class);
        if (ignoreModule == null)
            return;

        if (ignoreModule.getIgnoreList().size() == 0) {
            inventory.setItem(page, 2 * 9 + 4, new DItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName("§c" + lang.getMessage("AUCTION_NO_ITEMS_NAME"))
                    .setLore(lang.getList("ISLAND_IGNORED_NOTHING_LORE"))
                    .build(), (player, clickType, i) -> SOUND_API.play(player, SoundType.NO)));
            return;
        }


        for (IgnoredPlayer ignoredPlayer : ignoreModule.getIgnoreList().valueCollection()) {
            IBaseGamer iBaseGamer = ignoredPlayer.getGamer();
            boolean online = ignoredPlayer.isOnline();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ignoredPlayer.getDate().getTime());
            String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

            ItemStack head = Head.getHeadByValue(iBaseGamer.getSkin().getValue());
            ItemStack item = ItemUtil.getBuilder(head)
                    .setName(iBaseGamer.getDisplayName())
                    .setLore(lang.getList("ISLAND_IGNORED_LORE",
                            date,
                            ignoredPlayer.getBlockedPlayer().getDisplayName(),
                            colorOnline(online)))
                    .build();

            inventory.setItem(page, slot, new DItem(item, (player, clickType, i) -> {
                if (!clickType.isRightClick())
                    return;

                if (memberType == MemberType.MEMBER || memberType == MemberType.NOBODY) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    return;
                }

                ignoreModule.removeIgnore(ignoredPlayer);
                island.broadcastMessageLocale("ISLAND_REMOVE_IGNORE_PLAYER",
                        iBaseGamer.getDisplayName(), gamer.getDisplayName());
            }));

            slot++;

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        API.pageButton(lang, page + 1, inventory, 48, 50);
    }

    @Override
    public void open() {
        inventory.openInventory(player);
    }

    private String colorOnline(boolean online) {
        if (online)
            return "§a" + lang.getMessage("ANSWER_YES");
        return "§c" + lang.getMessage("ANSWER_NO");
    }
}
