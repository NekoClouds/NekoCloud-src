package me.nekocloud.packetlib.libraries.effect;

import lombok.Getter;
import me.nekocloud.packetlib.libraries.effect.data.*;
import me.nekocloud.packetlib.libraries.effect.glow.CraftPlayerGlowing;
import me.nekocloud.packetlib.libraries.effect.glow.GlowListener;
import me.nekocloud.packetlib.libraries.effect.glow.GlowManager;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.effect.ParticleProperty;
import me.nekocloud.api.effect.PlayerGlowing;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ParticleAPIImpl implements ParticleAPI {

    private final CraftFireworkListener firework;

    @Getter
    private final NekoAPI nekoAPI;
    @Getter
    private final GlowManager glowManager;

    public ParticleAPIImpl(NekoAPI nekoApi) {
        this.nekoAPI = nekoApi;

        this.firework = new CraftFireworkListener(nekoApi);
        this.glowManager = new GlowManager();
        new GlowListener(this);
    }

    @Override
    public PlayerGlowing getOrCreateGlowing(Player player, List<Player> players) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        if (players == null) {
            players = new ArrayList<>();
        }

        String name = player.getName().toLowerCase();
        PlayerGlowing playerGlowing = glowManager.getGlowings().get(name);
        if (playerGlowing != null) {
            playerGlowing.addEntity(players);

            return playerGlowing;
        }

        CraftPlayerGlowing craftPlayerGlowing = new CraftPlayerGlowing(glowManager, player, players);
        for (Player other : players) {
            glowManager.getMemberGlowing().put(other.getName().toLowerCase(), craftPlayerGlowing);
        }
        glowManager.getGlowings().put(name, craftPlayerGlowing);

        return craftPlayerGlowing;
    }

    @Override
    public PlayerGlowing getGlowing(Player player) {
        return getOrCreateGlowing(player, null);
    }

    @Override
    public PlayerGlowing getByMember(Player member) {
        return glowManager.getMemberGlowing().get(member.getName().toLowerCase());
    }

    @Override
    public void resetGlowing(Player player) {
        if (player == null) {
            return;
        }

        CraftPlayerGlowing glowing = glowManager.getGlowings().remove(player.getName().toLowerCase());
        if (glowing == null) {
            return;
        }

        glowing.remove();
    }

    @Override
    public List<PlayerGlowing> getGlowings() {
        return new ArrayList<>(glowManager.getGlowings().values());
    }

    @Override
    public void shootRandomFirework(Player player) {
        shootRandomFirework(player.getLocation());
    }

    @Override
    public void shootRandomFirework(Location location) {
        firework.shootRandomFirework(location);
    }

    @Override
    public void launchInstantFirework(FireworkEffect fe, Player player) {
        launchInstantFirework(fe, player.getLocation());
    }

    @Override
    public void launchInstantFirework(FireworkEffect fe, Location location) {
        firework.launchInstantFirework(fe, location);
    }

    @Override
    public void launchInstantFirework(Location location, Color... colors) {
        firework.launchInstantFirework(FireworkEffect.builder()
                .with(FireworkEffect.Type.BURST)
                .withColor(colors)
                .build(), location);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Location center, float offsetX, float offsetY, float offsetZ,
                           float speed, int amount, double range) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        if (ParticleUtils.hasProperty(effect, (ParticleProperty.REQUIRES_WATER)) && !ParticleUtils.isWater(center))
            throw new IllegalArgumentException("There is no water at the center location");

        CraftParticlePacket packet = new CraftParticlePacket(effect, offsetX, offsetY, offsetZ, speed, amount,
                range > 256, null);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Location center, float speed, int amount) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        if (ParticleUtils.hasProperty(effect, (ParticleProperty.REQUIRES_WATER)) && !ParticleUtils.isWater(center))
            throw new IllegalArgumentException("There is no water at the center location");

        CraftParticlePacket packet = new CraftParticlePacket(effect, 0.0F, 0.0F, 0.0F, speed,
                amount, true, null);
        packet.sendTo(center, 128);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Location center) {
        CraftParticlePacket packet = new CraftParticlePacket(effect, 0.0F, 0.0F, 0.0F,
                0.0F, 1, true, null);
        packet.sendTo(center, 128);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Location center) {
        CraftParticlePacket packet = new CraftParticlePacket(effect, 0.0F, 0.0F, 0.0F,
                0.0F, 1, true, null);
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Location center, float speed, int amount) {
        CraftParticlePacket packet = new CraftParticlePacket(effect, 0.0F, 0.0F, 0.0F, speed,
                amount, true, null);
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Location center, float offsetX, float offsetY,
                           float offsetZ, float speed, int amount) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        if (ParticleUtils.hasProperty(effect, (ParticleProperty.REQUIRES_WATER)) && !ParticleUtils.isWater(center))
            throw new IllegalArgumentException("There is no water at the center location");

        CraftParticlePacket packet = new CraftParticlePacket(effect, offsetX, offsetY, offsetZ, speed, amount,
                ParticleUtils.isLongDistance(center, players), null);
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Location center, Vector direction, float speed, double range) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        if (!ParticleUtils.hasProperty(effect, ParticleProperty.DIRECTIONAL))
            throw new IllegalArgumentException("This particle effect is not directional");

        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_WATER) && !ParticleUtils.isWater(center))
            throw new IllegalArgumentException("There is no water at the center location");

        CraftParticlePacket packet = new CraftParticlePacket(effect, direction, speed,
                range > 256, null);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Material material, byte data, boolean block, Location center,
                           Vector direction, float speed, double range) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        CraftParticleData particleData = new CraftParticleItemData(material, data);
        if (block)
            particleData = new CraftParticleBlockData(material, data);

        if (!ParticleUtils.isDataCorrect(effect, particleData))
            throw new IllegalArgumentException("The particle achievement cosmetics is incorrect");

        CraftParticlePacket packet = new CraftParticlePacket(effect, direction, speed, range > 256,
                particleData);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Location center, Vector direction, float speed) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        if (!ParticleUtils.hasProperty(effect, ParticleProperty.DIRECTIONAL))
            throw new IllegalArgumentException("This particle effect is not directional");

        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_WATER) && !ParticleUtils.isWater(center))
            throw new IllegalArgumentException("There is no water at the center location");

        CraftParticlePacket packet = new CraftParticlePacket(effect, direction, speed, ParticleUtils.isLongDistance(
                center, players), null);
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Material material, byte data, boolean block,
                           Location center, Vector direction, float speed) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        CraftParticleData particleData = new CraftParticleItemData(material, data);
        if (block)
            particleData = new CraftParticleBlockData(material, data);

        if (!ParticleUtils.isDataCorrect(effect, particleData))
            throw new IllegalArgumentException("The particle achievement cosmetics is incorrect");

        CraftParticlePacket packet = new CraftParticlePacket(effect, direction, speed, ParticleUtils.isLongDistance(
                center, players), particleData);
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Color color, Location center, double range) {
        if (!ParticleUtils.hasProperty(effect, ParticleProperty.COLORABLE))
            throw new IllegalArgumentException("This particle effect is not colorable");

        CraftParticleColor particleColor = new CraftParticleOrdinaryColor(color);

        if (!ParticleUtils.isColorCorrect(effect, particleColor))
            throw new IllegalArgumentException("The particle color cosmetics is incorrect");

        CraftParticlePacket packet = new CraftParticlePacket(effect, particleColor, range > 256);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, int red, int green, int blue, Location center, double range) {
        if (!ParticleUtils.hasProperty(effect, ParticleProperty.COLORABLE))
            throw new IllegalArgumentException("This particle effect is not colorable");

        CraftParticleColor particleColor = new CraftParticleOrdinaryColor(red, green, blue);

        if (!ParticleUtils.isColorCorrect(effect, particleColor))
            throw new IllegalArgumentException("The particle color cosmetics is incorrect");

        CraftParticlePacket packet = new CraftParticlePacket(effect, particleColor, range > 256);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, int note, Location center, double range) {
        if (!ParticleUtils.hasProperty(effect, ParticleProperty.COLORABLE))
            throw new IllegalArgumentException("This particle effect is not colorable");

        CraftParticleNoteColor particleColor = new CraftParticleNoteColor(note);
        CraftParticlePacket packet = new CraftParticlePacket(effect, particleColor, range > 256);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, Material material, byte data, boolean block, Location center,
                           float offsetX, float offsetY, float offsetZ, float speed, int amount, double range) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        CraftParticleData particleData = new CraftParticleItemData(material, data);
        if (block)
            particleData = new CraftParticleBlockData(material, data);

        if (!ParticleUtils.isDataCorrect(effect, particleData))
            throw new IllegalArgumentException("The particle achievement cosmetics is incorrect");

        CraftParticlePacket packet = new CraftParticlePacket(effect, offsetX, offsetY, offsetZ, speed, amount,
                range > 256, particleData);
        packet.sendTo(center, range);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Color color, Location center) {
        if (!ParticleUtils.hasProperty(effect, ParticleProperty.COLORABLE))
            throw new IllegalArgumentException("This particle effect is not colorable");

        CraftParticleColor particleColor = new CraftParticleOrdinaryColor(color);

        if (!ParticleUtils.isColorCorrect(effect, particleColor))
            throw new IllegalArgumentException("The particle color cosmetics is incorrect");

        CraftParticlePacket packet = new CraftParticlePacket(effect, particleColor, ParticleUtils.isLongDistance(
                center, players));
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, int red, int green, int blue, Location center) {
        if (!ParticleUtils.hasProperty(effect, ParticleProperty.COLORABLE)) {
            throw new IllegalArgumentException("This particle effect is not colorable");
        }

        CraftParticleColor particleColor = new CraftParticleOrdinaryColor(red, green, blue);

        if (!ParticleUtils.isColorCorrect(effect, particleColor)) {
            throw new IllegalArgumentException("The particle color cosmetics is incorrect");
        }

        CraftParticlePacket packet = new CraftParticlePacket(effect, particleColor, ParticleUtils.isLongDistance(
                center, players));
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, int note, Location center) {
        if (!ParticleUtils.hasProperty(effect, ParticleProperty.COLORABLE))
            throw new IllegalArgumentException("This particle effect is not colorable");

        CraftParticleNoteColor particleColor = new CraftParticleNoteColor(note);
        CraftParticlePacket packet = new CraftParticlePacket(effect, particleColor, ParticleUtils.isLongDistance(
                center, players));
        packet.sendTo(center, players);
    }

    @Override
    public void sendEffect(ParticleEffect effect, List<Player> players, Material material, byte data, boolean block,
                           Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        if (ParticleUtils.hasProperty(effect, ParticleProperty.REQUIRES_DATA))
            throw new IllegalArgumentException("This particle effect requires additional achievement");

        CraftParticleData particleData = new CraftParticleItemData(material, data);
        if (block) {
            particleData = new CraftParticleBlockData(material, data);
        }

        if (!ParticleUtils.isDataCorrect(effect, particleData)) {
            throw new IllegalArgumentException("The particle achievement cosmetics is incorrect");
        }

        CraftParticlePacket packet = new CraftParticlePacket(effect, offsetX, offsetY, offsetZ, speed, amount,
                ParticleUtils.isLongDistance(center, players), particleData);
        packet.sendTo(center, players);

    }


}
