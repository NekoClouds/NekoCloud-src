package me.nekocloud.survival.commons.commands;

import lombok.val;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.RtpGui;

public class RtpCommand extends CommonsCommand {

	public RtpCommand(ConfigData configData) {
		super(configData, true, "rtp", "ртп", "рандтп");
		spigotCommand.setCooldown(3, "rtpCommandCooldown");
	}

	@Override
	public void execute(GamerEntity gamerEntity, String command, String[] args) {
		val gamer = (BukkitGamer) gamerEntity;
		if (gamer == null)
			return;

		GUI_MANAGER.getGui(RtpGui.class, gamer.getPlayer()).open();
	}
}
