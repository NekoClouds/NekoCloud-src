package me.nekocloud.skyblock.manager;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.IslandModule;
import me.nekocloud.skyblock.api.island.IslandType;
import me.nekocloud.skyblock.api.manager.IslandManager;
import me.nekocloud.skyblock.api.manager.TerritoryManager;
import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.skyblock.craftisland.CraftIsland;
import me.nekocloud.skyblock.utils.FaweUtils;
import me.nekocloud.skyblock.utils.IslandLoader;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftIslandManager implements IslandManager {
    private final TerritoryManager territoryManager = SkyBlockAPI.getTerritoryManager();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    private final Map<Integer, Island> playerIsland = new ConcurrentHashMap<>();
    private final Map<Integer, Island> memberIsland = new ConcurrentHashMap<>();
    private final Map<IslandTerritory, Island> territoryIsland = new ConcurrentHashMap<>();

    @Override
    public Island getIsland(String name) {
        BukkitGamer gamer = gamerManager.getGamer(name);
        if (gamer == null) {
            return null;
        }

        return getIsland(gamer.getPlayerID());
    }

    @Override
    public Island getIsland(int playerID) {
        Island island = playerIsland.get(playerID);
        if (island == null)
            island = memberIsland.get(playerID);

        return island;
    }

    @Override
    public Island getIsland(Player player) {
        return getIsland(player.getName());
    }

    @Override
    public Island getIsland(IBaseGamer gamer) {
        if (gamer == null) {
            return null;
        }

        return getIsland(gamer.getPlayerID());
    }

    @Override
    public Island getIsland(Location location) {
        if (!location.getWorld().getName().equals(SkyBlockAPI.getSkyBlockWorldName())) {
            return null;
        }

        return getIsland(SkyBlockAPI.getTerritoryManager().getTerritory(location));
    }

    @Override
    public Island getIsland(IslandTerritory territory) {
        return territoryIsland.get(territory);
    }

    @Override
    public Island getIsland(Entity entity) {
        return getIsland(entity.getLocation());
    }

    @Override
    public Island getIslandById(int islandId) {
        return playerIsland.values()
                .stream()
                .filter(island -> island.getIslandID() == islandId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean hasIsland(Player player) {
        BukkitGamer gamer = gamerManager.getGamer(player);
        if (gamer == null)
            return false;

        return hasIsland(gamer.getPlayerID());
    }

    @Override
    public boolean hasIsland(int playerID) {
        return playerIsland.containsKey(playerID) || memberIsland.containsKey(playerID);
    }

    @Override
    public Island createIsland(Player player, IslandType islandType) {
        BukkitGamer gamer = gamerManager.getGamer(player);
        if (gamer == null)
            return null;

        int playerID = gamer.getPlayerID();
        Island island = new CraftIsland(gamer, islandType);
        IslandTerritory territory = island.getTerritory();
        playerIsland.put(playerID, island);
        territoryIsland.put(territory, island);

        return island;
    }

    public void addMember(Island island, int playerID, boolean mysql) {
        if (memberIsland.containsKey(playerID))
            return;

        memberIsland.put(playerID, island);

        if (mysql)
            IslandLoader.addMember(island, playerID);
    }

    public void removeMember(Island island, int playerID) {
        if (!memberIsland.containsKey(playerID))
            return;

        memberIsland.remove(playerID);

        IslandLoader.removeMember(island, playerID);
    }

    @Override
    public Map<Integer, Island> getPlayerIsland() {
        return playerIsland;
    }

    @Override
    public Map<IslandTerritory, Island> getTerritoryIsland() {
        return territoryIsland;
    }

    @Override
    public Map<Integer, Island> getMemberIsland() {
        return memberIsland;
    }

    @Override
    public void delete(Island island) {
        island.getModules().values().forEach(IslandModule::delete);

        IslandTerritory territory = island.getTerritory();
        territoryIsland.remove(territory);

        IBaseGamer owner = island.getOwner();
        playerIsland.remove(owner.getPlayerID());

        island.getMembers().forEach(islandMember -> memberIsland.remove(islandMember.getPlayerID()));

        territoryManager.remove(territory);

        FaweUtils.resetBlocks(territory);

        IslandLoader.removeIsland(island);
    }
}
