package me.nekocloud.survival.commons.managers;

import lombok.val;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.survival.commons.config.CommonsSurvivalSql;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.sql.GlobalLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftWarpManager implements WarpManager {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();
    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();
    private final Map<String, Warp> warps = new ConcurrentHashMap<>();

    @Override
    public void addWarp(Warp warp) {
        String name = warp.getName();
        warps.put(name.toLowerCase(), warp);
    }

    public void addToDataBase(Warp warp) {
        addWarp(warp);

        CommonsSurvivalSql.addWarp(warp.getName(), warp.getOwnerID(), warp.getLocation());
    }

    @Override
    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    @Override
    public List<Warp> getWarps(User user) {
        val name = user.getName();
        val gamer = gamerManager.getGamer(name);

        int playerID = gamer == null ? GlobalLoader.containsPlayerID(name) : gamer.getPlayerID();

        return getWarps(playerID);
    }

    @Override
    public List<Warp> getWarps(String name) {
        User user = userManager.getUser(name);
        return getWarps(user);
    }

    @Override
    public List<Warp> getWarps(int playerID) {
        List<Warp> warps = new ArrayList<>();

        this.warps.values().forEach(warp -> {
            if (warp.getOwnerID() == playerID) {
                warps.add(warp);
            }
        });

        return warps;
    }

    @Override
    public Map<String, Warp> getWarps() {
        return new HashMap<>(warps);
    }

    @Override
    public void removeWarp(Warp warp) {
        if (warp == null) {
            return;
        }

        removeWarp(warp.getName());
    }

    @Override
    public void removeWarp(String name) {
        CommonsSurvivalSql.removeWarp(name);
        warps.remove(name.toLowerCase());
    }

    @Override
    public int size() {
        return warps.size();
    }
}
