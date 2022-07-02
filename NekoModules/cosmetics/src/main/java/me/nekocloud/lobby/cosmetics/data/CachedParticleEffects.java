package me.nekocloud.lobby.cosmetics.data;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;
import me.nekocloud.api.effect.ParticleEffect;

import java.util.Arrays;

@UtilityClass
public final class CachedParticleEffects {
    
    private final TIntObjectMap<ParticleEffect> CACHED_EFFECTS = new TIntObjectHashMap<>();

    public ParticleEffect getById(int id) {
        return CACHED_EFFECTS.get(id);
    }

    static {
        Arrays.stream(ParticleEffect.values()).forEach(particle ->
                CACHED_EFFECTS.put(particle.getId(), particle));
    }
}

