//package me.nekocloud.chat.bukkit;
//
//import gnu.trove.list.TIntList;
//import lombok.experimental.UtilityClass;
//import lombok.val;
//import me.nekocloud.api.NekoCloud;
//import me.nekocloud.api.gui.AbstractGui;
//import me.nekocloud.api.manager.GuiManager;
//import me.nekocloud.api.player.BukkitGamer;
//
//@UtilityClass
//public class CoreChatGuiManager {
//
//    private final GuiManager<AbstractGui<?>> GUI_MANAGER = NekoCloud.getGuiManager();
//
//    public void openIgnoreGui(BukkitGamer gamer, TIntList ignoreList) {
//        if (gamer == null || !gamer.isOnline())
//            return;
//
//        val ignoreGui = GUI_MANAGER.getGui(IgnoreGui.class, gamer.getPlayer());
//        ignoreGui.setIgnoreList(ignoreList);
//        ignoreGui.open();
//    }
//
//    static {
//        GUI_MANAGER.createGui(IgnoreGui.class);
//    }
//}
