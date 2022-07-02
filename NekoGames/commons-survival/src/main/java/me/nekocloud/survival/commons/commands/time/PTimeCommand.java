package me.nekocloud.survival.commons.commands.time;

import lombok.val;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.time.PTimeGui;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class PTimeCommand extends CommonsCommand {

    public PTimeCommand(ConfigData configData) {
        super(configData, true, "ptime", "playertime");
        setMinimalGroup(configData.getInt("ptimeCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        val gamer = (BukkitGamer) gamerEntity;
        val player = gamer.getPlayer();

        if (strings.length == 0) {
            val gui = GUI_MANAGER.getGui(PTimeGui.class, player);
            gui.open();
            return;
        }

        if (strings[0].equalsIgnoreCase("reset")) {
            sendMessageLocale(gamerEntity, "PTIME_RESET");
            player.resetPlayerWeather();
            player.resetPlayerTime();
            return;
        }

        long time;
        try {
            time = Integer.parseInt(strings[0]);
        } catch (Exception e) {
            player.resetPlayerTime();
            player.resetPlayerWeather();
            gamerEntity.sendMessageLocale("PTIME_ERROR");
            return;
        }

        sendMessageLocale(gamerEntity, "PTIME", time);
        player.setPlayerTime(time, false);
    }
}
