package me.nekocloud.api.scoreboard;

import me.nekocloud.base.util.lists.NextPreviousList;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface PlayerTag {
    /**
     * у каждого игрока своя тима (если это лобби)
     * и мы каждому игроку создаем PlayerTag и шлем их всем
     * с помощью метода addPlayerToTeam мы добавляем игрока в тиму и у него
     * будет тот преф или суфф который мы поставим, а с помощью
     * метода sendTo мы шлем их тем игрокам, которые будут видеть
     * что у игроков которые в тиме опр преф и суфф
     */
    void sendToAll();
    void sendTo(Collection<Player> players);
    void sendTo(Player player);

    void addPlayerToTeam(Player player);
    void addPlayersToTeam(Collection<Player> players);
    void addNamesToTeam(Collection<String> names);

    void removePlayerFromTeam(Player player);

    boolean isPrefixAnimated();
    boolean isContainsTeam(Player player);
    boolean isEmpty();

    List<String> getPlayersTeam();
    NextPreviousList<String> getAnimation();

    void setFriendInv(boolean friendInv);

    void setPrefixSuffix(String prefix, String suffix);
    void setPrefix(String prefix);
    void setSuffix(String suffix);

    void setPrefixAnimated(boolean animated);
    void setPrefixAnimation(List<String> animation);

    void disableCollidesForAll();
    void setCollides(Collides collides);

    void remove();
}
