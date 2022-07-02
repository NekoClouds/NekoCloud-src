package me.nekocloud.core.api.connection.player;

import lombok.NonNull;
import lombok.val;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.OnlineGamer;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.connection.player.CorePlayerImpl;
import me.nekocloud.core.io.packet.bungee.BungeePlayerTitle;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import java.util.function.Function;

public interface CorePlayer extends OnlineGamer, CommandSender {

    static CorePlayer getPlayer(@NotNull CorePlayer player) {
        return getPlayer(player.getName());
    }

    static CorePlayer getPlayer(String name) {
        return (CorePlayer) GamerAPI.getByName(name);
    }


    static CorePlayer getPlayer(int id) {
        return (CorePlayer) GamerAPI.getOnline(id);
    }

    static @NotNull CorePlayer getOrCreate(
            final String name, final String inetAddress,
            final Bungee bungeeServer, final int protocolVersion
    ) throws UnknownHostException {
        val player = (CorePlayer) GamerAPI.getByName(name);
        if (player != null)
            return player;

        return new CorePlayerImpl(name, inetAddress, bungeeServer, protocolVersion);
    }

    /**
     * Получить оффлайн игрока из кеша
     * @return обьект игрока
     */
    IOfflineData getOfflineData();

    /**
     * Получить ип игрока
     * @return inetAddress
     */
    InetAddress getIp();

    Bungee getBungee();
    Bukkit getBukkit();

    Version getVersion();

    /**
     * Получить валюту игрока
     * @param type тип валюты которую надо
     * @return кол-во валюты
     */
    int getMoney(final PurchaseType type);

    /**
     * Проиграть игроку звук
     * @param sound тип
     */
    void playSound(final SoundType sound);

    /**
     * Проиграть игроку звук
     * @param sound тип
     * @param volume сила
     */
    void playSound(final SoundType sound, float volume, float pitch);

    /**
     * Отправить тайтл игроку
     * @param action тип
     * @param message сообщение
     * @param fadeIn ...
     * @param stay сколько показывать
     * @param fadeOut ...
     */
    void sendTitle(final BungeePlayerTitle.Action action, final String message,
                   int fadeIn, int stay, int fadeOut);

    /**
     * Отправить тайтл игроку
     * @param action тип
     * @param message скомпонент сообщения
     * @param fadeIn ...
     * @param stay сколько показывать
     * @param fadeOut ...
     */
    void sendTitle(final BungeePlayerTitle.Action action, final BaseComponent[] message,
                   int fadeIn, int stay, int fadeOut);

    /**
     * Отправить сообщение игроку
     * @param message сообщение
     */
    void sendMessage(final @NotNull String message);

    /**
     * перенаправить игрока на сервер
     * @param bukkit серв
     */
    void redirect(@NonNull final Bukkit bukkit);

    /**
     * Установить игроку сервер
     * @param bukkit серв
     */
    void setBukkit(@NonNull final Bukkit bukkit);

    /**
     * перенаправить игрока на сервер
     * @param serverName имя сервера
     */
    void redirect(@NonNull final String serverName);

    /**
     * Отключить игрока от прокси
     * @param reason причина
     */
    void disconnect(final BaseComponent... reason);


    long getLastOnline();
    long getJoinedTime();
}
