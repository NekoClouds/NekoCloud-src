package me.nekocloud.bettersurvival.combat;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.api.ActionBarAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CombatData {

    private static final int LEAVE_DELAY_SECONDS = 15;
    private static final ActionBarAPI ACTION_BAR_API = NekoCloud.getActionBarAPI();

    private BukkitTask task;
    private final BukkitGamer gamer;
    @Setter
    private LivingEntity lastDamager;
    @Getter
    private boolean inPvp;
    private long lastCombatTime;

    public CombatData(BukkitGamer gamer) {
        this.task = null;
        this.lastCombatTime = 0L;
        this.gamer = gamer;
    }

    void handleCombat(boolean damager, Plugin owner) {
        Language lang = gamer.getLanguage();
        this.lastCombatTime = System.currentTimeMillis();
        if (!this.inPvp) {
            this.inPvp = true;

            gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_PVP_START", LEAVE_DELAY_SECONDS));
        }
        if (!damager && this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (this.task == null) {
            this.task = new BukkitRunnable() {
                public void run() {
                    if (CombatData.this.lastCombatTime + LEAVE_DELAY_SECONDS * 1000 <= System.currentTimeMillis()) {
                        CombatData.this.inPvp = false;
                        CombatData.this.task = null;
                        if (CombatData.this.gamer.isOnline()) {
                            CombatData.this.gamer.sendMessage(CommonsSurvival.getConfigData().getPrefix() + lang.getMessage("BETTER_SURVIVAL_PVP_END"));
                            ACTION_BAR_API.sendBar(CombatData.this.gamer.getPlayer(), " ");
                        }
                        this.cancel();
                    } else {
                        int delay = (int) (LEAVE_DELAY_SECONDS + (CombatData.this.lastCombatTime - System.currentTimeMillis()) / 1000L);
                        int per = (int) ((1.0f - delay / LEAVE_DELAY_SECONDS) * 20.0f);
                        StringBuilder bar = new StringBuilder();
                        for (int i = 0; i < 20; ++i) {
                            bar.append("§").append((i < per) ? "a" : "c").append("|");
                        }
                        ACTION_BAR_API.sendBar(CombatData.this.gamer.getPlayer(), lang.getMessage("BETTER_SURVIVAL_PVP_BAR",bar, delay ));
                    }
                }
            }.runTaskTimerAsynchronously(owner, 10L, 10L);
        }
    }
}
