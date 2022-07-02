package me.nekocloud.survival.commons.object;

import lombok.Getter;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.config.CommonsSurvivalSql;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.events.UserTeleportToWarpEvent;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.nekoapi.utils.core.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;

public class CraftWarp implements Warp {
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private static final UserManager USER_MANAGER = CommonsSurvivalAPI.getUserManager();
    private static final WarpManager WARP_MANAGER = CommonsSurvivalAPI.getWarpManager();

    @Getter
    private final String name;
    @Getter
    private final int ownerID;
    @Getter
    private final Date date;

    @Getter
    private boolean isPrivate;
    private ItemStack icon;

    private IBaseGamer gamer;

    private String locationString;
    private Location location;

    public CraftWarp(String name, int owner, Location location, boolean isPrivate) {
        this.name = name;
        this.ownerID = owner;
        this.location = location;
        this.date = new Date(System.currentTimeMillis());
        this.isPrivate = isPrivate;
    }

    public CraftWarp(String name, int owner, String locationString, long date, boolean isPrivate) {
        this.name = name;
        this.ownerID = owner;
        this.locationString = locationString;
        this.date = new Date(date);
        this.isPrivate = isPrivate;
    }

    @Override
    public String getNameOwner() {
        return getOwner().getDisplayName();
    }

    @Override
    public IBaseGamer getOwner() {
        if (gamer == null) {
            gamer = GAMER_MANAGER.getOrCreate(ownerID);
        }

        return gamer;
    }

    @Override
    public Collection<Player> getNearbyPlayers(int size) {
        return PlayerUtil.getNearbyPlayers(location, size);
    }

    @Override
    public ItemStack getIcon() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(ownerID);
        if (gamer != null) {
            icon = gamer.getHead();
        }

        if (icon == null) {
            IBaseGamer offlineGamer = getOwner();
            if (offlineGamer == null) {
                icon = ItemUtil.getBuilder(Material.SKULL_ITEM)
                        .setDurability((short) 3)
                        .build();
            } else {
                icon = Head.getHeadByValue(offlineGamer.getSkin().getValue());
            }
        }

        return icon.clone();
    }

    @Override
    public Location getLocation() {
        if (location == null) {
            location = LocationUtil.stringToLocation(locationString, true);

            if (location.getWorld() == null) {
                location = CommonsSurvivalAPI.getSpawn();
            }
        }

        return location.clone();
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public void setPrivate(boolean flag) {
        if (this.isPrivate == flag) {
            return;
        }

        this.isPrivate = flag;
        CommonsSurvivalSql.setWarpPrivate(this);
    }

    @Override
    public void teleport(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (isPrivate && !gamer.isFriend(ownerID) && !gamer.isStaff()) {
            gamer.sendMessageLocale("WARP_CLOSE", getName());
            return;
        }

        User user = USER_MANAGER.getUser(player);
        if (user == null) {
            return;
        }

        UserTeleportToWarpEvent event = new UserTeleportToWarpEvent(user, this);
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Language lang = gamer.getLanguage();
        if (user.teleport(getLocation())) {
            gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("WARP_TO", getName()));
        }
    }

    @Override
    public Warp save() {
        CommonsSurvivalAPI.getWarpManager().addWarp(this);
        return this;
    }

    @Override
    public void remove() {
        WARP_MANAGER.removeWarp(this);
    }

    @Override
    public String toString() {
        return "Warp{name = " + name + ", " +
                "owner = " + ownerID + ", " +
                "private = " + isPrivate + ", " +
                "location = " + location + "}";
    }
}
