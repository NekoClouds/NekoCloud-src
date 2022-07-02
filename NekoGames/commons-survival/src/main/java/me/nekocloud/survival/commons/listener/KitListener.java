package me.nekocloud.survival.commons.listener;

import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.Kit;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.manager.KitManager;
import me.nekocloud.survival.commons.api.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class KitListener extends DListener<CommonsSurvival> {

    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();
    private final KitManager kitManager = CommonsSurvivalAPI.getKitManager();

    public KitListener(CommonsSurvival commonsSurvival) {
        super(commonsSurvival);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        User user = userManager.getUser(player);
        if (user == null)
            return;

        if (!user.isOnline() || !user.isFirstJoin())
            return;

        Inventory inventory = player.getInventory();
        for (Kit kit : kitManager.getKits().values()) {
            if (!kit.isStart())
                continue;

            kit.getItems().forEach(inventory::addItem);
        }
    }
}
