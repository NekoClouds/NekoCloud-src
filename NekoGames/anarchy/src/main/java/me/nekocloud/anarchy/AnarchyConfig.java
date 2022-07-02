package me.nekocloud.anarchy;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import me.nekocloud.market.Market;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AnarchyConfig {

    private final Spigot spigot = NekoCloud.getGamerManager().getSpigot();
    private final Anarchy anarchy;

    private FileConfiguration config;

    public AnarchyConfig(Anarchy anarchy) {
        this.anarchy = anarchy;
        File file = new File(CoreUtil.getConfigDirectory() + "/anarchy.yml");
        if (!file.exists()) {
            spigot.sendMessage("§c[Anarchy] [ERROR] §fКонфиг не найден! Плагин отключается!");
            Bukkit.getPluginManager().disablePlugin(anarchy);
            return;
        }

        ConfigManager configManager = new ConfigManager(file);
        config = configManager.getConfig();
    }


    public TIntObjectMap<Integer> loadScavenger() {
        TIntObjectMap<Integer> data = new TIntObjectHashMap<>();

        config.getStringList("Scavenger").forEach(s -> {
            String[] strings = s.split(";");
            data.put(Integer.parseInt(strings[0]), Integer.valueOf(strings[1]));
        });

        return data;
    }

    public TIntObjectMap<Double> loadMultiMoney() {
        TIntObjectMap<Double> data = new TIntObjectHashMap<>();

        config.getStringList("MobMoneyMulti").forEach(s -> {
            String[] strings = s.split(";");
            double multi = Market.round(Double.parseDouble(strings[1]));
            data.put(Integer.parseInt(strings[0]), multi);
        });

        return data;
    }

    public Map<EntityType, Integer> loadMoneyData() {
        Map<EntityType, Integer> data = new HashMap<>();

        config.getStringList("MobMoney").forEach(s -> {
            String[] strings = s.split(";");
            EntityType entityType = EntityType.valueOf(strings[0]);
            data.put(entityType, Integer.valueOf(strings[1]));
        });

        return data;
    }



}
