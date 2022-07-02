package me.nekocloud.survival.commons.util;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.api.manager.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class GuiThread extends Thread {

    private final GuiManager<CommonsSurvivalGui> manager = CommonsSurvivalAPI.getGuiManager();
    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();

    public GuiThread() {
        super("CommonsSurvivalGuiThread");
        start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(50);

                for (User user : userManager.getUsers().values()) {
                    CraftUser craftUser = (CraftUser) user;
                    clear(craftUser.getCallReguests());
                    clear(craftUser.getTradeReguests());
                }

                for (String name : manager.getPlayerGuis().keySet()) {
                    Map<String, CommonsSurvivalGui> guiList = manager.getPlayerGuis().get(name);
                    if (guiList != null) {
                        guiList.values().forEach(alternateGui -> {
                            if (alternateGui != null) {
                                alternateGui.update();
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clear(Map<String, Long> data) {
        for (Map.Entry<String, Long> request : data.entrySet()) {
            String name = request.getKey();
            Long time = request.getValue();
            if (time + 122 * 1000 < System.currentTimeMillis()) {
                data.remove(name);
            }

            Player player = Bukkit.getPlayerExact(name);
            if (player == null || !player.isOnline()) {
                data.remove(name);
            }
        }
    }
}
