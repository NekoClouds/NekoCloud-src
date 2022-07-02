package me.nekocloud.bettersurvival;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.bettersurvival.combat.CombatManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

//todo переписать нормально
public class BetterSurvival extends JavaPlugin {

    private BukkitTask gameTimer;

    @Override
    public void onEnable() {
        if (!NekoCloud.getGamerManager().getSpigot().getName().contains("skyblock")) { //Если сервер не сб
            this.gameTimer = new GameTimer(this).runTaskTimer(this, 20L, 20L);
            new CommandCompass();
        }
        new CombatManager(this);
    }

    @Override
    public void onDisable() {
        if (this.gameTimer != null) {
            this.gameTimer.cancel();
        }
    }

    public static Location getBedLocation(Player player) {
        User user = CommonsSurvivalAPI.getUserManager().getUser(player);
        if (user != null)
            return user.getBedLocation();

        return CommonsSurvivalAPI.getSpawn();
    }
}
