//package me.nekocloud.chat.bukkit;
//
//import gnu.trove.list.TIntList;
//import gnu.trove.procedure.TIntProcedure;
//import lombok.Setter;
//import lombok.val;
//import me.nekocloud.api.CoreAPI;
//import me.nekocloud.api.NekoCloud;
//import me.nekocloud.api.gui.AbstractGui;
//import me.nekocloud.api.inventory.DItem;
//import me.nekocloud.api.inventory.type.MultiInventory;
//import me.nekocloud.api.util.Head;
//import me.nekocloud.api.util.ItemUtil;
//import org.bukkit.entity.Player;
//
//public class IgnoreGui extends AbstractGui<MultiInventory> {
//
//    private static final CoreAPI CORE_API = NekoCloud.getCoreAPI();
//
//    @Setter
//    private TIntList ignoreList;
//
//    public IgnoreGui(Player player) {
//        super(player);
//
//        createInventory();
//    }
//
//    @Override
//    protected void createInventory() {
//        inventory = INVENTORY_API.createMultiInventory(player,
//                gamer.getLanguage().getMessage("IGNORE_GUI_NAME"), 5);
//    }
//
//    @Override
//    protected void setItems() {
//        val lang = gamer.getLanguage();
//
//        ignoreList.forEach(new TIntProcedure() {
//            int page = 0;
//            int slot = 10;
//
//            @Override
//            public boolean execute(int playerId) {
//                val ignoredGamer = GAMER_MANAGER.getOrCreate(playerId);
//                if (ignoredGamer == null)
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
//                inventory.setItem(page, slot, new DItem(ItemUtil.getBuilder(Head.getHeadBySkin(ignoredGamer.getSkin()))
//                        .setName(lang.getMessage("IGNORE_ITEM_NAME", ignoredGamer.getDisplayName()))
//                        .setLore(lang.getList("IGNORE_ITEM_LORE"))
//                        .build(),
//                        ((player, clickType, i) -> {
//                            player.closeInventory();
//                           // CORE_API.executeCommand(gamer, "ignore remove " + ignoredGamer.getName());
//                        })));
//
//                slot++;
//                return true;
//            }
//        });
//
//        INVENTORY_API.pageButton(lang, 1, inventory, 38, 42);
//    }
//}
