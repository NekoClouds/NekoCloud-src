package me.nekocloud.friends.bukkit.gui;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Setter;
import lombok.val;
import me.nekocloud.api.CoreAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.gui.DefaultGui;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.base.util.pair.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendsGui extends DefaultGui<MultiInventory> {

    private static final CoreAPI CORE_API = NekoCloud.getCoreAPI();

    @Setter
    private Int2ObjectMap<Pair<Long, String>> data;

    public FriendsGui(Player player) {
        super(player);
        createInventory();
    }

    @Override
    protected void createInventory() {
        inventory = INVENTORY_API.createMultiInventory(player,
                gamer.getLanguage().getMessage("FRIENDS_GUI_NAME"), 5);
    }

    @Override
    protected void updateItems() {
        List<Integer> friends = new ArrayList<>(data.keySet());

        friends.sort(Comparator.comparing(integer ->
                data.get(integer).getLeft() == -1 ? Long.MAX_VALUE : data.get(integer).getLeft()));
        Collections.reverse(friends);

        val lang = gamer.getLanguage();

        int page = 0;
        int slot = 10;
        int friendsOnline = 0;
        for (Integer playerId : friends) {
            IBaseGamer friend = GAMER_MANAGER.getGamer(playerId);
            if (friend == null)
                return;

            if (slot == 17) {
                slot = 19;
            } else if (slot == 26) {
                slot = 28;
            } else if (slot == 35) {
                slot = 10;
                page++;
            }

            Pair<Long, String> data = this.data.get(playerId);
            List<String> lore = data.getLeft() == -1 ?
                    lang.getList("FRIEND_ITEM_ONLINE_LORE",
                            data.getRight(),
                            gamer.getVersion().toClientName()) :
                    lang.getList("FRIEND_ITEM_OFFLINE_LORE",
                            TimeUtil.leftTime(lang, (System.currentTimeMillis() - data.getLeft()) / 1000),
                            data.getRight(),
                            gamer.getVersion().toClientName());

            inventory.setItem(page, slot, new DItem(ItemUtil.getBuilder(Head.getHeadBySkin(friend.getSkin()))
                    .setName(lang.getMessage("FRIEND_ITEM_NAME", friend.getDisplayName()))
                    .setLore(lore)
                    .build(),
                    ((player1, clickType, i) -> {
                        if (data.getLeft() != -1)
                            return;

                        CORE_API.sendToServer(gamer.getPlayer(), friend + friend.getName());
                        player1.closeInventory();
                    })));

            if (data.getLeft() == -1)
                friendsOnline++;

            slot++;
        }

        inventory.setItem(4, new DItem(ItemUtil.getBuilder(Material.BOOK_AND_QUILL)
                .setName(lang.getMessage("FRIEND_ITEM_INFO_NAME"))
                .setLore(lang.getList("FRIEND_ITEM_INFO_LORE", friends.size(),
                        gamer.getFriendsLimit(), friendsOnline))

                .build()));

        INVENTORY_API.pageButton(lang, page, inventory, 38, 42);
    }
}
