package me.nekocloud.api.effect;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ParticleEffect {
    EXPLOSION_NORMAL(0, ParticleProperty.DIRECTIONAL),
    EXPLOSION_LARGE(1),
    EXPLOSION_HUGE(2),
    FIREWORKS_SPARK(3, ParticleProperty.DIRECTIONAL),
    WATER_BUBBLE(4, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_WATER),
    WATER_SPLASH(5, ParticleProperty.DIRECTIONAL),
    WATER_WAKE(6, ParticleProperty.DIRECTIONAL),
    SUSPENDED(7, ParticleProperty.REQUIRES_WATER),
    SUSPENDED_DEPTH(8, ParticleProperty.DIRECTIONAL),
    CRIT(9, ParticleProperty.DIRECTIONAL),
    CRIT_MAGIC(10, ParticleProperty.DIRECTIONAL),
    SMOKE_NORMAL(11, ParticleProperty.DIRECTIONAL),
    SMOKE_LARGE(12, ParticleProperty.DIRECTIONAL),
    SPELL(13),
    SPELL_INSTANT(14),
    SPELL_MOB(15, ParticleProperty.COLORABLE),
    SPELL_MOB_AMBIENT(16, ParticleProperty.COLORABLE),
    SPELL_WITCH(17),
    DRIP_WATER(18),
    DRIP_LAVA(19),
    VILLAGER_ANGRY(20),
    VILLAGER_HAPPY(21, ParticleProperty.DIRECTIONAL),
    TOWN_AURA(22, ParticleProperty.DIRECTIONAL),
    NOTE(23, ParticleProperty.COLORABLE),
    PORTAL(24, ParticleProperty.DIRECTIONAL),
    ENCHANTMENT_TABLE(25, ParticleProperty.DIRECTIONAL),
    FLAME(26, ParticleProperty.DIRECTIONAL),
    LAVA(27),
    FOOTSTEP(28),
    CLOUD(29, ParticleProperty.DIRECTIONAL),
    REDSTONE(30, ParticleProperty.COLORABLE),
    SNOWBALL(31),
    SNOW_SHOVEL(32, ParticleProperty.DIRECTIONAL),
    SLIME(33),
    HEART(34),
    BARRIER(35),
    ITEM_CRACK(36, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    BLOCK_CRACK(37, ParticleProperty.REQUIRES_DATA),
    BLOCK_DUST(38, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    WATER_DROP(39),
    ITEM_TAKE(40),
    MOB_APPEARANCE(41),
    DRAGON_BREATH(42),
    END_ROD(43),
    DAMAGE_INDICATOR(44),
    SWEEP_ATTACK(45),
    //для 1.12
    FALLING_DUST(46),
    TOTEM(47),
    SPIT(48);

    private final int id;
    private final List<ParticleProperty> properties;

    ParticleEffect(int id, ParticleProperty... properties) {
        this.id = id;
        this.properties = Arrays.asList(properties);
    }
}
