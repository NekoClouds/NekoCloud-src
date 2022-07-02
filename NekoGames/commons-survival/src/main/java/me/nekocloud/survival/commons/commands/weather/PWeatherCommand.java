package me.nekocloud.survival.commons.commands.weather;

import lombok.val;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.SoundType;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.weather.PWeatherGui;

public class PWeatherCommand extends CommonsCommand {

	public PWeatherCommand(ConfigData configData) {
		super(configData, true, "pweather", "ппогода", "персональнаяпогода");
		setMinimalGroup(configData.getInt("pweatherCommand"));
		spigotCommand.setCooldown(30, "pweatherCommand");
	}

	@Override
	public void execute(GamerEntity gamerEntity, String command, String[] args) {
		val gamer = (BukkitGamer) gamerEntity;
		if (gamer == null)
			return;

		val player = gamer.getPlayer();
		if (args.length != 0 && (args[0].equalsIgnoreCase("reset")
				|| args[0].equalsIgnoreCase("сброс"))) {
			sendMessageLocale(gamerEntity, "PWEATHER_RESET");
			player.resetPlayerWeather();
			return;
		}

		GUI_MANAGER.getGui(PWeatherGui.class, player).open();
		gamer.playSound(SoundType.NOTE_BELL);
	}
}
