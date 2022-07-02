package me.nekocloud.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.base.locale.Language;
import org.bukkit.ChatColor;

@AllArgsConstructor
@Getter
public enum Rarity {
    NONE(-1, 0, 0, ChatColor.DARK_GRAY, null),
    COMMON(0, 1, 0, ChatColor.YELLOW, ParticleEffect.SMOKE_LARGE),
    RARE( 1, 0.27, 0.02, ChatColor.AQUA, ParticleEffect.CLOUD),
    EPIC(2, 0.07, 0.03, ChatColor.DARK_PURPLE, ParticleEffect.SPELL_WITCH),
    LEGENDARY(3, 0.025, 0.025, ChatColor.GOLD, ParticleEffect.FLAME);

    private final int rarity;
    private final double chance;
    private final double changeChance;
    private final ChatColor color;
    private final ParticleEffect effect;

    public String getName(Language language) {
        return color + language.getMessage(this.name());
    }
}
