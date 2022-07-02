package me.nekocloud.survival.commons.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.Kit;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.manager.KitManager;
import me.nekocloud.survival.commons.object.CraftKit;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.utils.bukkit.BlockUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigData {

    final CommonsSurvival commonsSurvival;

    String prefix;
    String dataBase;
    String rtpWorld;

    int rtpSize;

    final Map<String, Integer> voidWorld = new HashMap<>();

    boolean trade;
    boolean spawnTp;
    boolean spawnFlyOff;

    boolean warpSystem;
    boolean globalLocalChat;
    boolean callSystem;
    boolean kitSystem;
    boolean homeSystem;
    boolean bedHomeSystem;
    boolean portalCreate;
    boolean rtpSystem;

    final Map<Group, Integer> homeLimit = new HashMap<>();
    final Map<Group, Integer> warpLimit = new HashMap<>();

    public ConfigData(CommonsSurvival commonsSurvival) {
        this.commonsSurvival = commonsSurvival;

        commonsSurvival.saveDefaultConfig();
    }

    public void load() {
        FileConfiguration config = commonsSurvival.getConfig();

        voidWorld.clear();
        for (String world : config.getConfigurationSection("voidSpawn").getKeys(false)) {
            int level = config.getInt("voidSpawn." + world);
            voidWorld.put(world.toLowerCase(), level);
        }

        dataBase = config.getString("dataBase");
        prefix = config.getString("prefix");

        CommonsSurvivalAPI.setSpawn(LocationUtil.stringToLocation(config.getString("spawn"), true));
        spawnTp = config.getBoolean("spawnTp");
        spawnFlyOff = config.getBoolean("spawnFlyOff");

        globalLocalChat = config.getBoolean("globalLocalChat");

        warpSystem = config.getBoolean("warpSystem");
        homeSystem = config.getBoolean("homeSystem");
        callSystem = config.getBoolean("callSystem");
        kitSystem = config.getBoolean("kitSystem");
        bedHomeSystem = config.getBoolean("bedHomeSystem");
        portalCreate = config.getBoolean("portalCreate");

        rtpSystem = config.getBoolean("rtp.enable");
        rtpWorld = config.getString("rtp.world");
        rtpSize = config.getInt("rtp.size");

        trade = config.getBoolean("trade");
    }

    public void init() {
        if (kitSystem)
            loadKit();

        if (homeSystem)
            loadHomeLimit();

        if (warpSystem) {
            loadWarpLimit();
            CommonsSurvivalSql.loadWarps();
        }
    }

    public boolean isBedHomeSystem() {
        return bedHomeSystem;
    }

    private void loadHomeLimit() {
        homeLimit.clear();
        homeLimit.put(Group.DEFAULT, commonsSurvival.getConfig().getInt("setHomeDefault"));

        for (String string : commonsSurvival.getConfig().getStringList("setHomeLimit")) {
            if (!string.contains(":")) {
                continue;
            }

            String groupName = string.split(":")[0];
            Group group = Group.getGroupByName(groupName);
            if (group == Group.DEFAULT) {
                continue;
            }

            int limit = Integer.parseInt(string.split(":")[1]);
            homeLimit.put(group, limit);
        }
    }

    private void loadKit() {
        KitManager kitManager = CommonsSurvivalAPI.getKitManager();
        kitManager.getKits().clear();
        FileConfiguration config = commonsSurvival.getConfig();

        try {
            for (String kitName : config.getConfigurationSection("Kits").getKeys(false)) {
                String patch = "Kits." + kitName + ".";
                boolean start = false;
                int cooldown = config.getInt(patch + "cooldown");
                Group group = Group.DEFAULT;
                Group defaultGroup = null;

                if (config.contains(patch + "start")) {
                    start = config.getBoolean(patch + "start");
                }

                if (config.contains(patch + "group")) {
                    group = Group.getGroup(config.getInt(patch + "group"));
                }

                if (config.contains(patch + "mainGroup")) {
                    defaultGroup = Group.getGroup(config.getInt(patch + "mainGroup"));
                }

                ItemStack icon = BlockUtil.itemStackFromString(config.getString(patch + "icon"));
                List<ItemStack> itemStack = config.getStringList(patch + "items")
                        .stream()
                        .map(BlockUtil::itemStackFromString)
                        .collect(Collectors.toList());

                Kit kit = new CraftKit(kitName, itemStack, icon, cooldown, group, defaultGroup);
                kit.setStart(start);

                kitManager.addKit(kit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWarpLimit() {
        warpLimit.clear();
        warpLimit.put(Group.DEFAULT, commonsSurvival.getConfig().getInt("setWarpDefault"));

        for (String string : commonsSurvival.getConfig().getStringList("setWarpLimit")) {
            if (!string.contains(":")) {
                continue;
            }

            String groupName = string.split(":")[0];
            Group group = Group.getGroupByName(groupName);
            if (group == Group.DEFAULT) {
                continue;
            }

            int limit = Integer.parseInt(string.split(":")[1]);
            warpLimit.put(group, limit);
        }
    }

    public Integer getHomeLimit(Group group) {
        return homeLimit.get(group);
    }

    public int getInt(String patch) {
        return commonsSurvival.getConfig().getInt(patch);
    }
}
