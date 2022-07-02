package me.nekocloud.lobby.cosmetics.api.player;

import com.google.common.collect.Multimap;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import org.bukkit.entity.Player;

public interface CosmeticPlayer {

    Player getPlayer();

    Multimap<EffectType, ParticleEffect> getParticles();

    ParticleEffect getSelectedParticle(EffectType effectType);

    void setSelectedParticle(EffectType effectType, ParticleEffect effect);

    void addParticle(EffectType effectType, ParticleEffect effect);
}

