package me.nekocloud.entity;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.GamerBase;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class NekoGamerManager implements GamerManager {

    @Getter
    private final SpigotServer spigot;

    public NekoGamerManager(NekoAPI nekoAPI) {
        spigot = new SpigotServer(nekoAPI, nekoAPI.getUsername());
    }

    @Override
    public GamerEntity getEntity(CommandSender sender) {
        if (sender instanceof Player) {
            return getGamer(sender.getName());
        } else if (sender instanceof ConsoleCommandSender) {
            return spigot;
        } else {
            return null;
        }
    }

    @Override
    public BukkitGamer getGamer(String name) {
        return (BukkitGamer) GamerAPI.getByName(name);
    }

    @Override
    public BukkitGamer getGamer(Player player) {
        if (player == null) {
            return null;
        }
        return getGamer(player.getName());
    }

    @Override
    public BukkitGamer getGamer(int playerID) {
        return (BukkitGamer) GamerAPI.getOnline(playerID);
    }

    @Override
    public void removeGamer(String name) {
        GamerAPI.removeGamer(name.toLowerCase());
    }

    @Override
    public void removeGamer(@NotNull Player player) {
        removeGamer(player.getName());
    }

    @Override
    public void removeGamer(@NotNull BukkitGamer gamer) {
        removeGamer(gamer.getName());
    }

    @Override
    public boolean containsGamer(@NotNull Player player) {
        return containsGamer(player.getName());
    }

    @Override
    public boolean containsGamer(String name) {
        return GamerAPI.contains(name);
    }

    @Override
    public Map<String, GamerEntity> getGamerEntities() {
        Map<String, GamerEntity> gamerEntities = new HashMap<>();
        gamerEntities.put(spigot.getName(), spigot);
        for (GamerBase gamerBase : GamerAPI.getGamers().values()) {
            gamerEntities.put(gamerBase.getName(), (BukkitGamer) gamerBase);
        }

        return gamerEntities;
    }

    @Override
    public Map<String, BukkitGamer> getGamers() {
        Map<String, BukkitGamer> gamers = new HashMap<>();
        for (GamerBase gamerBase : GamerAPI.getGamers().values()) {
            gamers.put(gamerBase.getName().toLowerCase(), (BukkitGamer) gamerBase);
        }
        return gamers;
    }

    @Override
    public IBaseGamer getOrCreate(int playerID) {
        IBaseGamer gamer = GamerAPI.getOnline(playerID);
        if (gamer != null) {
            return gamer;
        }
        return GamerAPI.getById(playerID);
    }

    @Override
    public IBaseGamer getOrCreate(String name) {
         return GamerAPI.getOrCreate(name);
     }
}
