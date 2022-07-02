package me.nekocloud.lobby.cosmetics.api;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum EffectType {

    ARROWS(1),
    CRITS(2),
    KILLS(3);

    private final int id;
    private static final TIntObjectMap<EffectType> EFFECTS;

    public static EffectType getById(int id) {
        return EFFECTS.get(id);
    }

    static {
        EFFECTS = new TIntObjectHashMap<>();
        Arrays.stream(values()).forEach(effectType -> EFFECTS.put(effectType.getId(), effectType));
    }
}

