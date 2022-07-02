package me.nekocloud.survival.commons.commands.time;

import lombok.val;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.SoundType;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.time.TimeGui;

public class TimeCommand extends CommonsCommand {

	public TimeCommand(ConfigData configData) {
		super(configData, false, "time", "settime", "уствремя");
		setMinimalGroup(configData.getInt("timeCommand"));
		spigotCommand.setCooldown(30, "timeCommand");
	}

	@Override
	public void execute(GamerEntity gamerEntity, String command, String[] args) {
		val gamer = (BukkitGamer) gamerEntity;
		if (gamer == null)
			return;

		val player = gamer.getPlayer();
		if (args.length == 0) {
			GUI_MANAGER.getGui(TimeGui.class, player).open();
			gamer.playSound(SoundType.NOTE_BELL);
		} else {
			long time;
			try {
				time = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				sendMessageLocale(gamerEntity, "TIME_ERROR");
				return;
			}

			sendMessageLocale(gamerEntity, "TIME_CHANGE", player.getWorld().getName(), time);
			player.getWorld().setTime(time);
		}
	}
}
