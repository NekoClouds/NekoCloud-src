package me.nekocloud.friends.bukkit.manager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.util.pair.Pair;
import me.nekocloud.friends.bukkit.gui.FriendsGui;
import me.nekocloud.friends.bukkit.gui.FriendsRequestsGui;

@UtilityClass
public class FriendsGuiManager {

    private final GuiManager<AbstractGui<?>> GUI_MANAGER = NekoCloud.getGuiManager();

    public void openFriendsGui(BukkitGamer gamer, Int2ObjectMap<Pair<Long, String>> data) {
        if (gamer == null || !gamer.isOnline())
            return;

        val friendsGui =
                GUI_MANAGER.getGui(FriendsGui.class, gamer.getPlayer());

        friendsGui.setData(data);
        friendsGui.open();
    }

    public void openFriendsRequestsGui(BukkitGamer gamer, IntSet requests) {
        if (gamer == null || !gamer.isOnline())
            return;

        val friendsRequestsGui =
                GUI_MANAGER.getGui(FriendsRequestsGui.class, gamer.getPlayer());

        friendsRequestsGui.setRequests(requests);
        friendsRequestsGui.open();
    }
}
