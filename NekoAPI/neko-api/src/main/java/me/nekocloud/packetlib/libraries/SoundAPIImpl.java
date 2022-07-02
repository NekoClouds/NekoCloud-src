package me.nekocloud.packetlib.libraries;

import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.SoundType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundAPIImpl implements SoundAPI {

    @Override
    public void play(Player player, Sound sound) {
        play(player, sound, 1.0f, 1.0f);
    }

    @Override
    public void play(Player player, Sound sound, float volume, float pitch) {
        if (player == null) {
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public void play(Location location, SoundType soundType) {
        play(location, soundType, soundType.getVolume(), soundType.getPitch());
    }

    @Override
    public void play(Location location, SoundType soundType, float volume, float pitch) {
        World world = location.getWorld();
        world.playSound(location, getSound(soundType), volume, pitch);
    }

    @Override
    public void play(Location location, Sound sound) {
        World world = location.getWorld();
        world.playSound(location, sound, 1.0f, 1.0f);
    }

    @Override
    public void play(Location location, Sound sound, float volume, float pitch) {
        World world = location.getWorld();
        world.playSound(location, sound, volume, pitch);
    }

    @Override
    public void play(Player player, SoundType soundType) {
        play(player, soundType, soundType.getVolume(), soundType.getPitch());
    }

    @Override
    public void play(Player player, SoundType soundType, float volume, float pitch) {
        Sound sound = getSound(soundType);
        if (sound == null) {
            return;
        }

        play(player, sound, volume, pitch);
    }

    @Override
    public Sound getSound(SoundType type) {
        if (type.getSoundName() == null) {
            return null;
        }

        return Sound.valueOf(type.getSoundName());
    }
}
