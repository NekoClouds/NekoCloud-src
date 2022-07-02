package me.nekocloud.lobby.profile;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JumpListener extends DListener<Lobby> {

    private final ParticleAPI particleAPI = NekoCloud.getParticleAPI();
    private final Map<String, Boolean> cooldown = new HashMap<>();

    public JumpListener(Lobby lobby) {
        super(lobby);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getName().toLowerCase());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        val from = e.getFrom();
        val to = e.getTo();
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        val player = e.getPlayer();
        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || gamer.getGroup() == Group.DEFAULT || gamer.getSetting(SettingsType.FLY)) {
            return;
        }

        val name = player.getName().toLowerCase();
        if (cooldown.get(name) != null && cooldown.get(name)) {
            player.setAllowFlight(true);
        } else {
            player.setAllowFlight(false);
        }

        if (player.isOnGround()) {
            cooldown.put(name, true);
        }

        if (cooldown.get(name) != null && !cooldown.get(name)) {
            particleAPI.sendEffect(ParticleEffect.FLAME, getPlayers(gamer), player.getLocation());
        }
    }

    private List<Player> getPlayers(BukkitGamer owner) {
        List<Player> players = new ArrayList<>();
        for (BukkitGamer gamer : GAMER_MANAGER.getGamers().values()) {
            Player player = gamer.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }

            if (gamer == owner) {
                players.add(player);
                continue;
            }

            if (owner.isFriend(gamer)) {
                players.add(player);
                continue;
            }

            if (owner.isYouTube() || owner.isStaff() || owner.isTikTok()) {
                players.add(player);
                continue;
            }

            if (!gamer.getSetting(SettingsType.HIDER)) {
                players.add(player);
            }
        }

        return players;
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        val player = e.getPlayer();
        val name = player.getName().toLowerCase();

        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (cooldown.containsKey(name) && !gamer.getSetting(SettingsType.FLY)) {
            e.setCancelled(true);
            cooldown.put(name, false);

            player.setVelocity(player.getLocation().getDirection()
                    .multiply(1.6D)
                    .setY(1.0D));

            player.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        val player = e.getPlayer();
        val name = player.getName().toLowerCase();

        if (!player.isOnGround() && cooldown.get(name) != null && !cooldown.get(name)) {
            player.setVelocity(new Vector(0, -5, 0));
        }
    }
}
