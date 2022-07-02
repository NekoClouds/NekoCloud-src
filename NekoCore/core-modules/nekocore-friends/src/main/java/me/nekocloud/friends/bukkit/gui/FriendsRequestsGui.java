//package me.nekocloud.friends.bukkit.gui;
//
//import it.unimi.dsi.fastutil.ints.IntConsumer;
//import it.unimi.dsi.fastutil.ints.IntSet;
//import lombok.Setter;
//import lombok.val;
//import me.nekocloud.api.CoreAPI;
//import me.nekocloud.api.NekoCloud;
//import me.nekocloud.api.gui.AbstractGui;
//import me.nekocloud.api.inventory.DItem;
//import me.nekocloud.api.inventory.type.MultiInventory;
//import me.nekocloud.api.util.Head;
//import me.nekocloud.api.util.InventoryUtil;
//import me.nekocloud.api.util.ItemUtil;
//import me.nekocloud.base.gamer.IBaseGamer;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//
//public class FriendsRequestsGui extends AbstractGui<MultiInventory> {
//
//    private static final CoreAPI CORE_API = NekoCloud.getCoreAPI();
//
//    @Setter
//    private IntSet requests;
//
//    public FriendsRequestsGui(Player player) {
//        super(player);
//        this.createInventory();
//    }
//
//    @Override
//    protected void createInventory() {
//        this.inventory = INVENTORY_API.createMultiInventory(this.player,
//                gamer.getLanguage().getMessage("FRIENDS_REQUESTS_GUI_NAME"), 5);
//    }
//
//    @Override
//    protected void setItems() {
//        val lang = gamer.getLanguage();
//        this.requests.forEach((playerId) -> {
//            int page = 0;
//            int slot = 10;
//
//            @Override
//            public boolean execute(int playerId) {
//                IBaseGamer friend = GAMER_MANAGER.getGamer(playerId);
//                if (friend == null)
//                    return false;
//
//                if (slot == 17) {
//                    slot = 19;
//                } else if (slot == 26) {
//                    slot = 28;
//                } else if (slot == 35) {
//                    slot = 10;
//                    page++;
//                }
//
//                inventory.setItem(page, slot, new DItem(ItemUtil.getBuilder(Head.getHeadBySkin(friend.getSkin()))
//                        .setName(lang.getMessage("FRIEND_ITEM_REQUEST_NAME", friend.getDisplayName()))
//                        .setLore(lang.getList("FRIEND_ITEM_REQUEST_LORE"))
//                        .build(),
//                        ((player1, clickType, i) -> {//todo
//                            CORE_API.sendToServer(gamer.getPlayer(), "friends " + (clickType.isLeftClick() ? "accept " : "deny ") + friend.getName());
//                            player1.closeInventory();
//                        })));
//
//                slot++;
////            }
//        });
//
//        inventory.setItem(4, new DItem(ItemUtil.getBuilder(Material.BOOK_AND_QUILL)
//                .setName(lang.getMessage("FRIEND_ITEM_REQUEST_INFO_NAME"))
//                .setLore(lang.getList("FRIEND_ITEM_REQUEST_INFO_LORE", requests.size()))
//                .build()));
//
//        INVENTORY_API.pageButton(lang, InventoryUtil.getPagesCount(9, 10), this.inventory, 38, 42);
//    }
//}
