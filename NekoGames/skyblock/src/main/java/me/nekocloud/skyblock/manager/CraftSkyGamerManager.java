package me.nekocloud.skyblock.manager;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.skyblock.api.entity.SkyGamer;
import me.nekocloud.skyblock.api.manager.SkyGamerManager;
import me.nekocloud.skyblock.craftisland.CraftSkyGamer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftSkyGamerManager implements SkyGamerManager {

    private final Map<String, SkyGamer> skyGamers = new ConcurrentHashMap<>();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    @Override
    public SkyGamer getSkyGamer(int playerID) {
        SkyGamer skyGamer = null;
        BukkitGamer gamer = gamerManager.getGamer(playerID);
        if (gamer != null)
            skyGamer = getSkyGamer(gamer.getName());

        return skyGamer;
    }

    @Override
    public SkyGamer getSkyGamer(String name) {
        return skyGamers.get(name.toLowerCase());
    }

    @Override
    public SkyGamer getSkyGamer(Player player) {
        return getSkyGamer(player.getName());
    }

    @Override
    public boolean contains(String name) {
        return skyGamers.containsKey(name.toLowerCase());
    }

    @Override
    public void addSkyGamer(SkyGamer skyGamer) {
        String name = skyGamer.getName().toLowerCase();
        if (skyGamers.containsKey(name))
            return;

        skyGamers.put(name, skyGamer);
    }

    @Override
    public void removeSkyGamer(SkyGamer skyGamer) {
        if (skyGamer == null)
            return;
        String name = skyGamer.getName();
        removeSkyGamer(name);
    }

    @Override
    public void removeSkyGamer(String name) {
        SkyGamer skyGamer = skyGamers.remove(name.toLowerCase());
        if (skyGamer == null)
            return;

        CraftSkyGamer craftSkyGamer = (CraftSkyGamer) skyGamer;
        craftSkyGamer.save();
    }

    @Override
    public Map<String, SkyGamer> getSkyGamers() {
        return skyGamers;
    }
}
