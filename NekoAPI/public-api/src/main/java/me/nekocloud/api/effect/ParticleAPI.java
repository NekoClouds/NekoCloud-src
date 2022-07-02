package me.nekocloud.api.effect;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public interface ParticleAPI {

    /**
     * сделать, чтобы игрок видел опр игроков с глоувингом
     * (цвет глоувинга будет зависить от префикса)
     * @param player - кто будет видеть
     * @param players - кого будет видеть
     */
    PlayerGlowing getOrCreateGlowing(Player owner, List<Player> players);
    PlayerGlowing getGlowing(Player owner);

    PlayerGlowing getByMember(Player member);

    /**
     * для кого отключить glow
     * @param player - для кого
     */
    void resetGlowing(Player player);

    List<PlayerGlowing> getGlowings();

    /**
     * запустить фейерверк
     * @param player - в ком
     */
    void shootRandomFirework(Player player);
    void shootRandomFirework(Location location);

    /**
     * поментальный файерворк
     * @param fe - метаданные
     * @param player - в ком запустить
     */
    void launchInstantFirework(FireworkEffect fe, Player player);
    void launchInstantFirework(FireworkEffect fe, Location location);
    void launchInstantFirework(Location location, Color... colors);

    /**
     * отправить эффект
     * @param effect - эффект
     * @param players - игроки(кому слать)
     * @param center - локация центра
     * @param offsetX - как далеко могут уходить по Х(для некоторых параметров - это цвета и тд)
     * @param offsetY - как далеко могут уходить по Y(для некоторых параметров - это цвета и тд)
     * @param offsetZ - как далеко могут уходить по Z(для некоторых параметров - это цвета и тд)
     * @param speed - скорость
     * @param amount - кол-во частиц
     * @param range - диапазон видимости (чтобы не всем игрокам отслыть)
     */

    /**
     * отправить эффекты всем в заданном диапазоне range
     */
    void sendEffect(ParticleEffect effect, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, double range);
    void sendEffect(ParticleEffect effect, Location center, float speed, int amount);
    void sendEffect(ParticleEffect effect, Location center); //одиночная частица

    /**
     * отправить одну частицу опр игрокам
     */
    void sendEffect(ParticleEffect effect, List<Player> players, Location center);
    void sendEffect(ParticleEffect effect, List<Player> players, Location center, float speed, int amount);

    /**
     * отправить эффекты определенным игрокам
     */
    void sendEffect(ParticleEffect effect, List<Player> players, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount);
    default void sendEffect(ParticleEffect effect, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount,  Player... players) {
        sendEffect(effect, Arrays.asList(players), center, offsetX, offsetY, offsetZ, speed, amount);
    }

    /**
     * отправить одну частицу, которая видна всем игрокам в радиусе range и летит в опр направлении
     */
    void sendEffect(ParticleEffect effect, Location center, Vector direction, float speed, double range);
    void sendEffect(ParticleEffect effect, Material material, byte data, boolean block, Location center, Vector direction, float speed, double range);


    /**
     * отправить одну частицу, которая видна определенным игрокам и летит в опр направлении
     */
    void sendEffect(ParticleEffect effect, List<Player> players, Location center, Vector direction, float speed);
    default void sendEffect(ParticleEffect effect, Location center, Vector direction, float speed,  Player... players) {
        sendEffect(effect, Arrays.asList(players), center, direction, speed);
    }
    void sendEffect(ParticleEffect effect, List<Player> players, Material material, byte data, boolean block, Location center, Vector direction, float speed);
    default void sendEffect(ParticleEffect effect, Material material, byte data, boolean block, Location center, Vector direction, float speed, Player... players) {
        sendEffect(effect, Arrays.asList(players), material, data, block, center, direction, speed);
    }


    /**
     * отправить окрашенную частицу, что будет доступна для всех игроков
     */
    void sendEffect(ParticleEffect effect, Color color, Location center, double range);
    void sendEffect(ParticleEffect effect, int red, int green, int blue, Location center, double range);
    void sendEffect(ParticleEffect effect, int note, Location center, double range); //цветные ноты
    void sendEffect(ParticleEffect effect, Material material, byte data, boolean block, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, double range); //частицы материала

    /**
     * отправить окрашенную частицу, что будет доступна опр игрокам
     */
    void sendEffect(ParticleEffect effect, List<Player> players, Color color, Location center);
    default void sendEffect(ParticleEffect effect, Color color, Location center, Player... players) {
        sendEffect(effect, Arrays.asList(players), color, center);
    }

    void sendEffect(ParticleEffect effect, List<Player> players, int red, int green, int blue, Location center);
    default void sendEffect(ParticleEffect effect, int red, int green, int blue, Location center, Player... players) {
        sendEffect(effect, Arrays.asList(players), red, green, blue, center);
    }

    void sendEffect(ParticleEffect effect, List<Player> players, int note, Location center); //цветные ноты
    default void sendEffect(ParticleEffect effect, int note, Location center, Player... players) {
        sendEffect(effect, Arrays.asList(players), note, center);
    }

    void sendEffect(ParticleEffect effect, List<Player> players, Material material, byte data, boolean block, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount); //частицы материала
    default void sendEffect(ParticleEffect effect, Material material, byte data, boolean block, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, Player... players) {
        sendEffect(effect, Arrays.asList(players), material, data, block, center, offsetX, offsetY, offsetZ, speed, amount);
    }

}
