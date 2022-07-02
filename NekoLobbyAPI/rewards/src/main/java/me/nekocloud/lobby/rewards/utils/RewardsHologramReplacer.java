package me.nekocloud.lobby.rewards.utils;

import com.google.common.collect.Iterators;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.rewards.data.RewardPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

public final class RewardsHologramReplacer implements Supplier<String> {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private final BukkitGamer gamer;
    private final Iterator<String> colors;

    public RewardsHologramReplacer(Player player) {
        gamer = GAMER_MANAGER.getGamer(player);
        this.colors = Iterators.cycle(Arrays.asList("§d", "§6", "§e", "§a", "§b"));
    }

    @Override
    public String get() {
        if (gamer == null) {
            return "";
        }

        RewardPlayer player = RewardPlayer.getPlayer(gamer.getName());
        if (player == null) {
            return "";
        }

        Language lang = gamer.getLanguage();
        int amount = player.getAvailableCount();

        if (amount < 1) {
            return "";
        }

        return colors.next() + CommonWords.AVAILABLE_1.convert(amount, lang)
                + " " + StringUtil.getNumberFormat(amount)
                + " " + CommonWords.REWARDS_1.convert(amount, lang);

    }
}