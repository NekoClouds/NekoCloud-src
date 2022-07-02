package me.nekocloud.lobby.cosmetics.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.util.Head;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum Effect {

    FIREWORK(10, new ItemStack(Material.FIREWORK), ParticleEffect.FIREWORKS_SPARK),
    WATER(10, new ItemStack(Material.WATER_BUCKET), ParticleEffect.WATER_SPLASH),
    EXPLOSION(5, new ItemStack(Material.TNT), ParticleEffect.EXPLOSION_NORMAL),
    SMOKE(5, new ItemStack(Material.CONCRETE_POWDER), ParticleEffect.SMOKE_NORMAL),
    HEART(15, Head.getHeadByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQzNmMzMjkxZmUwMmQxNDJjNGFmMjhkZjJmNTViYjAzOTdlMTk4NTU0ZTgzNDU5OTBkYmJjZDRjMTQwMzE2YiJ9fX0="),
            ParticleEffect.HEART),
    SLIME(5, new ItemStack(Material.SLIME_BALL), ParticleEffect.SLIME),
    NOTE(15, new ItemStack(Material.NOTE_BLOCK), ParticleEffect.NOTE),
    PORTAL(15, Head.getHeadByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBiZmMyNTc3ZjZlMjZjNmM2ZjczNjVjMmM0MDc2YmNjZWU2NTMxMjQ5ODkzODJjZTkzYmNhNGZjOWUzOWIifX19"),
            ParticleEffect.PORTAL),
    SPELL(15, new ItemStack(Material.DRAGONS_BREATH), ParticleEffect.SPELL),
    CLOUD(10, Head.getHeadByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY2YjEwYmY2ZWUyY2Q3ZTNhYzk2ZDk3NDllYTYxNmFhOWM3MzAzMGJkY2FlZmZhZWQyNDllNTVjODQ5OTRhYyJ9fX0="),
            ParticleEffect.CLOUD),
    SNOW(5, new ItemStack(Material.SNOW_BALL), ParticleEffect.SNOW_SHOVEL),
    LAVA(10, new ItemStack(Material.LAVA_BUCKET), ParticleEffect.LAVA),
    FLAME(15, new ItemStack(Material.BLAZE_POWDER), ParticleEffect.FLAME),
    VILLAGER_HAPPY(15, new ItemStack(Material.EMERALD), ParticleEffect.VILLAGER_HAPPY),
    MAGIC(15, new ItemStack(Material.ENCHANTMENT_TABLE), ParticleEffect.CRIT_MAGIC),
    SPELL_WITCH(15, new ItemStack(Material.END_CRYSTAL), ParticleEffect.SPELL_WITCH);

    int price;
    ItemStack icon;
    ParticleEffect particleEffect;
}

