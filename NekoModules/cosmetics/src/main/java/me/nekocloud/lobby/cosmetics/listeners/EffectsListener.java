package me.nekocloud.lobby.cosmetics.listeners;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.lobby.cosmetics.CosmeticPlugin;
import me.nekocloud.lobby.cosmetics.api.CosmeticsAPI;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import me.nekocloud.lobby.cosmetics.api.manager.CosmeticManager;
import me.nekocloud.lobby.cosmetics.api.player.CosmeticPlayer;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EffectsListener extends DListener<CosmeticPlugin> {

    ParticleAPI particleAPI = NekoCloud.getParticleAPI();
    CosmeticManager cosmeticManager = CosmeticsAPI.getCosmeticManager();
    Map<Arrow, ParticleEffect> arrows = new ConcurrentHashMap<>();

    public EffectsListener(CosmeticPlugin javaPlugin) {
        super(javaPlugin);
        new BukkitRunnable(){

            @Override
            public void run() {
                for (Map.Entry<Arrow, ParticleEffect> entry : EffectsListener.this.arrows.entrySet()) {
                    Arrow arrow = entry.getKey();
                    if (arrow == null || arrow.isDead()) {
                        EffectsListener.this.arrows.remove(arrow);
                        continue;
                    }

                    EffectsListener.this.particleAPI.sendEffect(
                            entry.getValue(),
                            arrow.getLocation(),
                            0.0f,
                            0.0f,
                            0.0f,
                            0.1f,
                            5,
                            20.0);
                }
            }
        }.runTaskTimerAsynchronously(javaPlugin, 5L, 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGamerJoin(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();
        if (gamer == null)
            return;

        cosmeticManager.loadPlayer(gamer.getPlayer());
    }

    @EventHandler
    public void onGamerQuit(PlayerQuitEvent e) {
        val player = e.getPlayer();
        cosmeticManager.unloadPlayer(player);
    }

//
//    @EventHandler
//    public void onKill(PlayerKillEvent e) {
//        CosmeticPlayer cosmeticPlayer = this.cosmeticManager.getCosmeticPlayer(e.getPlayer());
//        if (cosmeticPlayer == null) {
//            return;
//        }
//        ParticleEffect particleEffect = cosmeticPlayer.getSelectedParticle(EffectType.KILLS);
//        if (particleEffect == null) {
//            return;
//        }
//        this.particleAPI.sendEffect(particleEffect, e.getPlayer().getLocation().clone().add(0.0, 1.0, 0.0), 0.25f, 0.25f, 0.25f, 0.1f, 15, 12.0);
//    }

    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;


        if (!(e.getDamager() instanceof Player))
            return;


        Player player = (Player)e.getDamager();
        CosmeticPlayer cosmeticPlayer = cosmeticManager.getCosmeticPlayer(player);
        if (cosmeticPlayer == null)
            return;

        ParticleEffect particleEffect = cosmeticPlayer.getSelectedParticle(EffectType.CRITS);
        if (particleEffect == null)
            return;

        if (player.isOnGround()
                || player.isInsideVehicle()
                || player.isSprinting()
                || player.getFallDistance() <= 0.0f
                || player.hasPotionEffect(PotionEffectType.BLINDNESS)
                || player.hasMetadata("swing-time")
                && (player.getMetadata("swing-time").get(0)).asFloat() <= 0.9f) {
            return;
        }

        particleAPI.sendEffect(particleEffect,
                e.getEntity().getLocation().clone().add(0.0, 1.0, 0.0),
                0.25f, 0.25f, 0.25f, 0.1f, 15, 12.0);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onArrowShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        if (!(e.getProjectile() instanceof Arrow))
            return;

        CosmeticPlayer cosmeticPlayer = this.cosmeticManager.getCosmeticPlayer((Player)e.getEntity());
        if (cosmeticPlayer == null)
            return;

        ParticleEffect particleEffect = cosmeticPlayer.getSelectedParticle(EffectType.ARROWS);
        if (particleEffect == null)
            return;

        arrows.put((Arrow)e.getProjectile(), particleEffect);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow))
            return;

        arrows.remove(((Arrow)e.getEntity()));
    }
}

