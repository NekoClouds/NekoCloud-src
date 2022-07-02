package me.nekocloud.survival.commons.commands;

import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.base.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FeedCommand extends CommonsCommand {

    public FeedCommand(ConfigData configData) {
        super(configData, true, "feed", "еда", "покушать");
        setMinimalGroup(configData.getInt("feedCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup().getLevel() >= Group.ADMIN.getLevel()) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            feed(player);
            send("FEED", gamer, player.getDisplayName());
            return;
        }

        int level = configData.getInt("ignoreCooldownFeed");
        if (Cooldown.hasCooldown(sender.getName(), getCooldownType()) && gamer.getGroup().getLevel() < level) {
            Language lang = gamer.getLanguage();
            int time = Cooldown.getSecondCooldown(sender.getName(), getCooldownType());
            gamerEntity.sendMessageLocale("COOLDOWN", String.valueOf(time),
                    CommonWords.SECONDS_1.convert(time, lang));
        } else {
            if (gamer.getGroup().getLevel() < level)
                Cooldown.addCooldown(sender.getName(), getCooldownType(), 5 * 60 * 20);

            feed(sender);
        }
    }

    private void feed(Player player){
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.setExhaustion(0F);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        send("FEED", gamer, null);
    }
}
