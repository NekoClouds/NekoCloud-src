package me.nekocloud.api.player;

import me.nekocloud.base.gamer.IBaseGamer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

public interface GamerManager {

    @Nullable
    GamerEntity getEntity(final CommandSender sender);

    Spigot getSpigot();

    @Nullable
    BukkitGamer getGamer(final String name);
    @Nullable
    BukkitGamer getGamer(final Player player);
    @Nullable
    BukkitGamer getGamer(final int playerID);

    void removeGamer(final String name);
    void removeGamer(final Player player);
    void removeGamer(final @NotNull BukkitGamer gamer);

    boolean containsGamer(final @NotNull Player player);
    boolean containsGamer(final String name);

    /**
     * Получить всех кэшированных ентити
     */
    Map<String, GamerEntity> getGamerEntities();

    /**
     * Получить всех онлайн гемеров
     * (кэшированных)
     */
    Map<String, BukkitGamer> getGamers();

    /**
     * получить онлайн игрока или оффлайн
     * @param playerID - ник или айди игрока
     * @return - игрок
     * ВНИМАНИЕ! Делать ассинхронно
     */
    @Nullable
    IBaseGamer getOrCreate(final int playerID);
    @Nullable
    IBaseGamer getOrCreate(final String name);
}
