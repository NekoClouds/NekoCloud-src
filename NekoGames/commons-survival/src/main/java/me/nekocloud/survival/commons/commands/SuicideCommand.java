package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class SuicideCommand extends CommonsCommand {

    public SuicideCommand(ConfigData configData) {
        super(configData, true, "suicide");
        setMinimalGroup(configData.getInt("suicideCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.SUICIDE, Short.MAX_VALUE);
        BukkitUtil.callEvent(event);
        player.damage(Short.MAX_VALUE);

        if (player.getHealth() > 0)
            player.setHealth(0);

        sendMessageLocale(gamerEntity, "SUICIDE");
    }
}
