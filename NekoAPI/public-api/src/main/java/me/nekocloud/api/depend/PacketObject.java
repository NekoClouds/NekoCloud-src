package me.nekocloud.api.depend;

import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;

public interface PacketObject {
    /**
     * Проверить видно ли этот объект игроку
     * @param player - игрок которого нужно проверить на видимость
     * @return - вернет true если объект видно этому игроку.
     */
    boolean isVisibleTo(Player player);
    Collection<String> getVisiblePlayers(); //todo collection Player

    /**
     * Показать объект игроку
     * @param player - кому показываем
     */
    void showTo(Player player);
    void showTo(BukkitGamer gamer);
    void removeTo(Player player);
    void removeTo(BukkitGamer gamer);

    /**
     * сделать setPublic(false)
     * и удалить всех из getVisiblePlayers
     */
    void hideAll();

    /**
     * получить овнера
     * (если не нулл, то оно удалится при выходе овнера)
     */
    @Nullable
    Player getOwner();

    /**
     * назначить овнера
     * @param owner - овнер
     */
    void setOwner(Player owner);

    /**
     * узнать виден ли этот объект всемм игрокам или нет
     * @return - вернет true если объект видно всем.
     */
    boolean isPublic();
    void setPublic(boolean vision);

    /**
     * удалить объект
     */
    void remove();
}
