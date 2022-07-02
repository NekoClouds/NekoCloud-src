package me.nekocloud.limbo.listeners;

import me.nekocloud.limbo.NekoLimbo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AuthPlayerListener implements Listener {

    private final Map<Player, BukkitRunnable> playerTaskMap = new HashMap<>();
    private final NekoLimbo nekoLimbo;

    public AuthPlayerListener(NekoLimbo nekoLimbo) {
        this.nekoLimbo = nekoLimbo;
        Bukkit.getPluginManager().registerEvents(this, nekoLimbo);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
//        int playerId = NetworkModule.getInstance().getPlayerID(player.getName());
//        AuthModule.getInstance().getAuthPlayerMap().remove(playerId);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
//        val bukkitRunnable = playerTaskMap.remove(event.getPlayer());
//        if (bukkitRunnable != null) {
//            bukkitRunnable.cancel();
//        }
    }

//    @EventHandler
//    public void onAuthComplete(PlayerAuthCompleteEvent event) {
//        if (!ServerMode.isCurrentTyped(ServerMode.AUTH)) {
//            return;
//        }
//
//        val player = event.getBukkitPlayer();
//        if (player != null) {
////            BukkitRunnable runnable = playerTaskMap.remove(player);
//
////            if (runnable != null) {
////                runnable.cancel();
////            }
//
//            player.setExp(0.0f);
//            player.setLevel(0);
//        }
//    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
    }

}
