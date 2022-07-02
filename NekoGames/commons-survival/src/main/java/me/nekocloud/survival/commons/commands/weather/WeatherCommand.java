package me.nekocloud.survival.commons.commands.weather;

import lombok.val;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.SoundType;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.weather.WeatherGui;

public class WeatherCommand extends CommonsCommand {
	public WeatherCommand(ConfigData configData) {
		super(configData, false, "weather", "погода");
		setMinimalGroup(configData.getInt("weatherCommand"));
		spigotCommand.setCooldown(30, "weatherCommand");
	}

	@Override
	public void execute(GamerEntity gamerEntity, String command, String[] args) {
		val gamer = (BukkitGamer) gamerEntity;
		if (gamer == null)
			return;

		GUI_MANAGER.getGui(WeatherGui.class, gamer.getPlayer()).open();
		gamer.playSound(SoundType.NOTE_BELL);
	}
}
