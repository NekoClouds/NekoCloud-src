package me.nekocloud.skyblock.manager;

import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.skyblock.api.entity.IslandEntity;
import me.nekocloud.skyblock.api.manager.EntityManager;
import me.nekocloud.skyblock.craftisland.CraftIslandEntity;
import me.nekocloud.skyblock.utils.FaweUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CraftEntityManager implements EntityManager {

    @Override
    public List<IslandEntity> getEntities(IslandTerritory territory) {
        List<IslandEntity> entities = new ArrayList<>();
        FaweUtils.getEntities(territory).forEach(entity -> {
            CraftIslandEntity craftIslandEntity = new CraftIslandEntity(entity);
            if (craftIslandEntity.init())
                entities.add(craftIslandEntity);
        });
        return entities;
    }

    @Override
    public List<org.bukkit.entity.Player> getPlayers(IslandTerritory territory) {
        return getEntities(territory).stream()
                .filter(IslandEntity::isPlayer)
                .map(entity -> ((Player)entity.getBukkitEntity()))
                .filter(player -> player != null && player.isOnline())
                .collect(Collectors.toList());
    }
}
