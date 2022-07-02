package me.nekocloud.skyblock.api.manager;

import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.skyblock.api.entity.IslandEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface EntityManager {

    List<IslandEntity> getEntities(IslandTerritory territory);

    List<Player> getPlayers(IslandTerritory territory);
}
