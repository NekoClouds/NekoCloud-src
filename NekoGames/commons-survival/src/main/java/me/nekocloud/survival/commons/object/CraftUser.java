package me.nekocloud.survival.commons.object;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.Home;
import me.nekocloud.survival.commons.api.Kit;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserChangeGamemodeEvent;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.config.CommonsSurvivalSql;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public class CraftUser implements User {
    static final UserManager MANAGER = CommonsSurvivalAPI.getUserManager();
    static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    final Map<String, Long> callReguests = new ConcurrentHashMap<>();
    final Map<String, Long> tradeReguests = new ConcurrentHashMap<>();
    final String name;

    @Setter
    boolean recipeSee;

    Player player;

    boolean fly;
    boolean god;
    boolean tpToggle;
    boolean afk;
    Location lastLocation;
    Location bedLocation;
    Map<Kit, Timestamp> kits;
    Map<String, Home> homes;

    transient long lastActivity;

    boolean firstJoin;
    boolean saved = false;

    public CraftUser(String name, boolean fly, boolean god, boolean tpToggle, boolean first,
                     Location lastLocation, Map<Kit, Timestamp> kits, Map<String, Home> homes,
                     Location bedLocation) {
        this.name = name;
        this.kits = kits;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        this.firstJoin = gamer != null && first;
        this.fly = fly;
        this.god = god;
        this.afk = false;
        this.tpToggle = tpToggle;
        this.lastLocation = lastLocation;
        this.homes = homes;
        this.bedLocation = bedLocation;
        this.lastActivity = System.currentTimeMillis();
    }

    @Override
    public Player getPlayer() {
        if (player != null) {
            return player;
        }

        return player = Bukkit.getPlayerExact(getName());
    }

    @Override
    public Map<Kit, Timestamp> getKits() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return kits;
        int playerID = gamer.getPlayerID();
        for (Map.Entry<Kit, Timestamp> kitEntry : kits.entrySet()) {
            Kit kit = kitEntry.getKey();
            long time = kitEntry.getValue().getTime();
            if (System.currentTimeMillis() > time) {
                kits.remove(kit);
                CommonsSurvivalSql.removeKitFromSql(playerID, kit);
            }
        }
        return kits;
    }

    @Override
    public void addKit(Kit kit) {
        getKits();
        long time = System.currentTimeMillis() + kit.getCooldown() * 1000;
        kits.put(kit, new Timestamp(time));
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return;

        int playerID = gamer.getPlayerID();
        CommonsSurvivalSql.addKitToSql(playerID, kit.getName(), time);
    }

    @Override
    public boolean isCooldown(Kit kit) {
        return getKits().containsKey(kit);
    }

    @Override
    public int getCooldown(Kit kit) {
        Timestamp time = getKits().get(kit);
        if (time == null)
            return 0;

        return (int) ((time.getTime() - System.currentTimeMillis()) / 1000);
    }

    @Override
    public void addHome(String name, Location loc) {
        if (homes.containsKey(name))
            return;

        Home home = new CraftHome(name, loc);
        homes.put(name, home);

        BukkitGamer gamer = GAMER_MANAGER.getGamer(this.name);
        if (gamer == null) {
            //BukkitUtil.runTaskAsync(() -> {
            //    int playerID = GlobalLoader.containsPlayerID(this.name);
            //    AlternateSql.addHomeToSql(playerID, name, home.getLocation());
            //});
            return;
        }

        int playerID = gamer.getPlayerID();
        CommonsSurvivalSql.addHomeToSql(playerID, name, home.getLocation());

    }

    @Override
    public void removeHome(String name) {
        homes.remove(name);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(this.name);
        if (gamer == null) {
            //BukkitUtil.runTaskAsync(() -> {
            //    int playerID = GlobalLoader.containsPlayerID(this.name);
            //    AlternateSql.removeHomeFromSql(playerID, name);
            //});
            return;
        }
        int playerID = gamer.getPlayerID();
        CommonsSurvivalSql.removeHomeFromSql(playerID, name);

    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    @Override
    public void setGod(boolean god, boolean message) {
        if (this.god != god)
            saved = true;

        this.god = god;
        Player player = getPlayer();
        if (player == null || !player.isOnline())
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Language lang = gamer.getLanguage();
        if (god) {
            if (player.getHealth() != 0) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
            }
            if (message)
                gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix()
                        + lang.getMessage("GOD_ENABLE"));
        } else if (message) {
            gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix()
                    + lang.getMessage("GOD_DISABLE"));
        }
    }

    @Override
    public void setFly(boolean fly, boolean message) {
        if (this.fly != fly)
            saved = true;

        this.fly = fly;

        Player player = getPlayer();
        if (player == null || !player.isOnline())
            return;

        player.setAllowFlight(fly);
        player.setFlying(fly);

        if (!message)
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Language lang = gamer.getLanguage();

        gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage((fly ? "FLY_ENABLE" : "FLY_DISABLE")));
    }

    @Override
    public void setTpToggle(boolean tpToggle) {
        if (this.tpToggle != tpToggle) {
            this.saved = true;
        }

        this.tpToggle = tpToggle;
    }

    @Override
    public void setLastLocation(Location location) {
        saved = true;
        this.lastLocation = location;
    }

    @Override
    public void setGamemode(GameMode gamemode) {
        Player player = getPlayer();
        if (player == null || !player.isOnline())
            return;

        UserChangeGamemodeEvent event = new UserChangeGamemodeEvent(this, gamemode);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        player.setGameMode(event.getGameMode());
    }

    @Override
    public Location getBedLocation() {
        if (bedLocation == null) {
            return CommonsSurvivalAPI.getSpawn();
        }

        if (!bedLocation.getChunk().isLoaded()) {
            bedLocation.getChunk().load();
        }

        Block block = bedLocation.getWorld().getBlockAt(bedLocation);
        if (block.getType() != Material.BED_BLOCK && block.getType() != Material.BED) {
            return CommonsSurvivalAPI.getSpawn();
        }

        return bedLocation.clone().add(0.0, 1.5, 0.0);
    }

    @Override
    public void setBedLocation(Location location) {
        saved = true;
        this.bedLocation = location;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null) {
            return;
        }

        Language lang = gamer.getLanguage();
        gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("USER_SETBED"));
    }

    @Override
    public boolean teleport(Location location) {
        Player player = getPlayer();
        if (player == null) {
            return false;
        }

        Location old = player.getLocation();
        if (player.teleport(location)) {
            setLastLocation(old);

            return true;
        }

        return false;
    }

    @Override
    public boolean checkAfk() {
        return lastActivity + TimeUnit.MINUTES.toMillis(10) < System.currentTimeMillis();
    }

    @Override
    public void updateAfkPosition() {
        lastActivity = System.currentTimeMillis();
    }

    @Override
    public void setAfk(boolean flag) {
        if (flag) {
            this.lastActivity = 0L;
        } else {
            this.lastActivity = System.currentTimeMillis();
        }

    }

    @Override
    public void remove() {
        MANAGER.removeUser(this);
    }

    public void save() {
        if (!saved) {
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        int playerID = gamer == null ? GlobalLoader.containsPlayerID(name) : gamer.getPlayerID();

        CommonsSurvivalSql.saveData(playerID, "fly", String.valueOf(fly ? 1 : 0));
        CommonsSurvivalSql.saveData(playerID, "god", String.valueOf(god ? 1 : 0));
        CommonsSurvivalSql.saveData(playerID, "tpToggle", String.valueOf(tpToggle ? 1 : 0));

        if (lastLocation != null) {
            CommonsSurvivalSql.saveData(playerID, "lastLocation",
                    LocationUtil.locationToString(lastLocation, true));
        }

        if (bedLocation != null) {
            CommonsSurvivalSql.saveData(playerID, "bedLocation",
                    LocationUtil.locationToString(bedLocation, false));
        }

    }

    public boolean addCallRequest(Player player) {
        long time = System.currentTimeMillis();
        String name = player.getName();

        if (callReguests.containsKey(name) && callReguests.get(name) + 120 * 1000 > System.currentTimeMillis())
            return false;

        callReguests.put(name, time);
        return true;
    }

    public boolean addTradeRequest(Player player) {
        long time = System.currentTimeMillis();
        String name = player.getName();

        if (tradeReguests.containsKey(name) && tradeReguests.get(name) + 120 * 1000 > System.currentTimeMillis())
            return false;

        tradeReguests.put(name, time);
        return true;
    }

}
