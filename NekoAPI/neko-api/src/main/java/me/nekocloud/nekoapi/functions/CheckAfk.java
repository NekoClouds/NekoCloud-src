package me.nekocloud.nekoapi.functions;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.TitleAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CheckAfk extends DListener<NekoAPI> {

    private final Map<String, BukkitTask> afk = new ConcurrentHashMap<>();
    private final SoundAPI soundAPI = NekoCloud.getSoundAPI();
    private final TitleAPI titleAPI = NekoCloud.getTitlesAPI();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    public CheckAfk(NekoAPI javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!(NekoCloud.isLobby()) && !(NekoCloud.isHub())) {
            Player player = e.getPlayer();
            String name = player.getName();
            BukkitGamer gamer = gamerManager.getGamer(player);
            if (gamer == null)
                return;
            Language lang = gamer.getLanguage();
            if (e.getFrom().getBlockX() != e.getTo().getBlockX()
                    || e.getFrom().getBlockY() != e.getTo().getBlockY()
                    || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                BukkitTask task = afk.remove(name);
                if (task != null)
                    task.cancel();
                return;
            }

            if (afk.containsKey(name))
                return;
//            if (PlayerUtil.isSpectator(player))
//                return;
            BukkitTask active = new BukkitRunnable() {
                int timeAFK = 0;
                int timeTitle = 0;
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        afk.remove(name);
                    }
                    if (timeAFK >= 2700) {
                        soundAPI.play(player, SoundType.AFK_SOUND);
                        if (timeTitle >= 10) {
                            timeTitle = 0;
                            titleAPI.sendTitle(player, "§r", lang.getMessage("AFK"),
                                    0, 3, 1);
                        }
                        timeTitle++;
                    }
                    timeAFK++;
                }
            }.runTaskTimer(NekoAPI.getInstance(), 1L, 1L);

            afk.put(name, active);
        }
    }
}