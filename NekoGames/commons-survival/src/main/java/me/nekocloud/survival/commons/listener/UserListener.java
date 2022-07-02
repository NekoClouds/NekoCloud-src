package me.nekocloud.survival.commons.listener;

import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.config.CommonsSurvivalSql;
import me.nekocloud.survival.commons.config.ConfigData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class UserListener implements Listener {

    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();
    private final ConfigData configData;

    public UserListener(CommonsSurvival main) {
        this.configData = CommonsSurvival.getConfigData();
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        userManager.addUser(CommonsSurvivalSql.createUser(e.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        String name = player.getName();
        userManager.removeUser(name);

        Map<String, Boolean> map = PlayerListener.getMapTpErrorBeforeTp();
        if (map == null)
            return;

        map.remove(name);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatPlayer(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        boolean global = message.startsWith("!") && configData.isGlobalLocalChat();
        if (configData.isGlobalLocalChat() && !global) {
            e.getRecipients().clear();
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all.getWorld() != player.getWorld()
                        || all.getLocation().distanceSquared(player.getLocation()) > 100)
                    continue;

                e.getRecipients().add(all);
            }
        }
        String format = e.getFormat();
        if (global) {
            message = message.replaceFirst("!", "");
            format = " §8[§6G§8]" + format;
            e.setMessage(message);
        } else {
            format = " §8[§9L§8]" + format;
        }

        if (!configData.isGlobalLocalChat())
            return;

        e.setFormat(format);
    }
}
