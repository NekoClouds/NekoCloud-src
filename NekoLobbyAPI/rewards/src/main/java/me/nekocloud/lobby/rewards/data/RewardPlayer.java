package me.nekocloud.lobby.rewards.data;

import com.google.common.collect.Maps;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.rewards.Rewards;
import me.nekocloud.lobby.rewards.bonuses.Bonus;
import me.nekocloud.lobby.rewards.bonuses.RewardType;
import me.nekocloud.lobby.rewards.utils.RewardsHologramReplacer;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.Map;

public class RewardPlayer {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private static final HologramAPI HOLOGRAM_API = NekoCloud.getHologramAPI();
    private static final ParticleAPI PARTICLE_API = NekoCloud.getParticleAPI();
    private static final Map<String, RewardPlayer> PLAYER_MAP = Maps.newConcurrentMap();

    public static void removePlayer(String name) {
        PLAYER_MAP.remove(name);
    }

    public static RewardPlayer getPlayer(String name) {
        return PLAYER_MAP.get(name);
    }

    private final String name;

    private Map<RewardType, Long> rewardData;
    private Hologram availableHologram;

    public RewardPlayer(String name, Rewards rewards) {
        this.name = name;
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return;

        this.rewardData = rewards.getRewardManager().loadRewards(gamer.getPlayerID()); //todo: заполнение

        PLAYER_MAP.put(name, this);
    }

    public long getActivationTime(RewardType type) {
        return rewardData.getOrDefault(type, -1L);
    }

    public void activateReward(RewardType type, Player player, Rewards rewards) {
        long rewardTime = getActivationTime(type) + type.getTimeDelay();
        boolean available = rewardTime < System.currentTimeMillis();

        if (!available)
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return;

        rewardData.put(type, System.currentTimeMillis());
        saveData(type, rewards);

        showHologram(player);

        Language lang = gamer.getLanguage();

        player.sendMessage("");
        player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage(" " + lang.getMessage("REWARD_YOU_GOT_REWARDS"));
//                type.countRewards(),
//                StringUtil.getCorrectWord(type.countRewards(), "BONUSES_1", lang)));

        for (Bonus reward : type.getRewards()) {
            reward.giveTo(gamer, type, true);
        }

        player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");

        PARTICLE_API.launchInstantFirework(player.getLocation(), Color.AQUA, Color.BLACK, Color.RED, Color.YELLOW);
    }

    public void createHologram(Player player, Rewards rewards) {
        removeHologram();

        availableHologram = HOLOGRAM_API.createHologram(rewards.getHumanNPC().getLocation().add(0, 2.1, 0));
        availableHologram.addAnimationLine(2, new RewardsHologramReplacer(player));
    }

    public void showHologram(Player player) {
        int amount = getAvailableCount();
        if (amount > 0)
            availableHologram.showTo(player);
        else
            availableHologram.removeTo(player);
    }

    public void removeHologram() {
        if (availableHologram != null)
            availableHologram.remove();
    }

    public void saveData(RewardType type, Rewards rewards) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return;

        rewards.getRewardManager().saveData(gamer.getPlayerID(), type);
    }

    public int getAvailableCount() {
        int count = 0;
        for (RewardType type : RewardType.values()) {
            long time = getActivationTime(type);

            if (time + type.getTimeDelay() < System.currentTimeMillis())
                count++;
        }

        return count;
    }

}
