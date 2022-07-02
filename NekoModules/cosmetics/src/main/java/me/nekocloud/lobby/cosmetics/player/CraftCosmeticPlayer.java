package me.nekocloud.lobby.cosmetics.player;

import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import me.nekocloud.lobby.cosmetics.api.player.CosmeticPlayer;
import me.nekocloud.lobby.cosmetics.sql.CosmeticLoader;
import org.bukkit.entity.Player;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CraftCosmeticPlayer implements CosmeticPlayer {

    Player player;
    BukkitGamer gamer;

    Multimap<EffectType, ParticleEffect> particles;
    Map<EffectType, ParticleEffect> selectedData;

    public CraftCosmeticPlayer(Player player,
                               Multimap<EffectType, ParticleEffect> particles,
                               Map<EffectType, ParticleEffect> selectedData) {
        this.player = player;
        this.gamer = NekoCloud.getGamerManager().getGamer(player);

        this.particles = particles;
        this.selectedData = selectedData;
    }

    @Override
    public ParticleEffect getSelectedParticle(EffectType effectType) {
        return selectedData.get(effectType);
    }

    @Override
    public void setSelectedParticle(EffectType effectType, ParticleEffect particleEffect) {
        val insert = selectedData.get(effectType) == null;
        selectedData.put(effectType, particleEffect);

        CosmeticLoader.setSelectedEffect(gamer.getPlayerID(), effectType, particleEffect, insert);
    }

    @Override
    public void addParticle(EffectType effectType, ParticleEffect particleEffect) {
        particles.put(effectType, particleEffect);

        CosmeticLoader.addEffect(gamer.getPlayerID(), effectType, particleEffect);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Multimap<EffectType, ParticleEffect> getParticles() {
        return particles;
    }
}

