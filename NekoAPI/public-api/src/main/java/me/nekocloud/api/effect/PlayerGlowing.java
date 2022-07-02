package me.nekocloud.api.effect;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public interface PlayerGlowing {

    @Nullable
    Player getOwner();

    void addEntity(Player player);
    void addEntity(Player... players);
    void addEntity(List<Player> players);

    void removeEntity(Player player);
    void removeEntity(Player... players);
    void removeEntity(List<Player> players);

    List<String> getPlayers();

    void remove();
}
