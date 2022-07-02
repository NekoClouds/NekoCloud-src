package me.nekocloud.survival.commons.commands.home;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.base.util.TimeUtil;
import org.bukkit.entity.Player;

public class BedHomeCommand extends CommonsCommand {

    public BedHomeCommand(ConfigData configData) {
        super(configData, true, "home", "homes");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Language lang = gamerEntity.getLanguage();
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (Cooldown.hasCooldown(gamer, "bedHome") && !gamer.isDeveloper()) {
            long time = Cooldown.getSecondCooldown(gamer, "bedHome") * 1000L + System.currentTimeMillis();
            String finalString = TimeUtil.leftTime(lang, time, true);
            gamer.sendMessageLocale("COOLDOWN_2", finalString);
            return;
        }

        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(user.getBedLocation())) {
                sendMessageLocale(gamerEntity,  "HOME_TO", "BED");
                Cooldown.addCooldown(gamer, "bedHome", 20 * 60 * 20);
            }
        });
    }
}
