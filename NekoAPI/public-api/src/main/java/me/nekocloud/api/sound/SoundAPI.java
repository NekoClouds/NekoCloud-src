package me.nekocloud.api.sound;

import me.nekocloud.base.SoundType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public interface SoundAPI {
    /**
     * Проиграть звук игроку
     * @param player - кому играть
     * @param soundType - какой звук
     */
    void play(Player player, SoundType soundType);

    void play(Player player, SoundType soundType, float volume, float pitch);

    void play(Player player, Sound sound);

    void play(Player player, Sound sound, float volume, float pitch);

    void play(Location location, SoundType soundType);

    void play(Location location, SoundType soundType, float volume, float pitch);

    void play(Location location, Sound sound);

    void play(Location location, Sound sound, float volume, float pitch);

    Sound getSound(SoundType type);
}
