package me.nekocloud.lobby.rewards;

import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.lobby.rewards.managers.RewardListener;
import me.nekocloud.lobby.rewards.managers.RewardManager;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class Rewards extends JavaPlugin {

    private HumanNPC humanNPC;
    private RewardManager rewardManager;

    @Override
    public void onEnable() {
        Location locationNPC = LocationUtil.stringToLocation(loadConfig().getString("NPCLocation"), true);
        humanNPC = NekoCloud.getEntityAPI().createNPC(locationNPC, Skin.SKIN_REWARD);
        humanNPC.setPublic(true);

        rewardManager = new RewardManager();

        //Listeners
        new RewardListener(this);
    }

    private FileConfiguration loadConfig() {
        return YamlConfiguration.loadConfiguration(new File(CoreUtil.getConfigDirectory() + "/rewards.yml"));
    }
}
