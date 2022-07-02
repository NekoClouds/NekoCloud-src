package me.nekocloud.nekoapi.tops.hologram;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.lines.TextHoloLine;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.tops.HologramAnimation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;

/**
 * Спиздил у белки
 */
public abstract class HoloTop {

	private final Hologram hologram;
	private final List<String> topHeader;
	public List<HoloTopData> topData;

	//todo переписать этот обоссаный кусок говна сразу же как появится время
	protected HoloTop(Location location, List<String> topHeader, int updateMinutes) {
		this.hologram = NekoCloud.getHologramAPI().createHologram(location);
		this.topHeader = topHeader;

		hologram.addTextLine(topHeader);
		for(int index = 1; index < 11; index++) {
			char color = 'e';
			switch (index) {
				case 1 -> color = 'a';
				case 2 -> color = '6';
				case 3 -> color = 'c';
			}

			hologram.addTextLine("§" + color + "#" + index + " §8| §c" + Language.DEFAULT.getMessage("NO_DATA_ALIVE") + " §8|§f   ");
		}

		hologram.addAnimationLine(20, new HologramAnimation(Language.DEFAULT, updateMinutes));
		hologram.setPublic(true);
	}

	public abstract void updateData();

	public void update() {
		updateData();

		int index = 1;
		val maxPlayerName = topData.stream().map(top -> ChatColor.stripColor(top.getPlayerName()))
				.mapToInt(String::length)
				.max()
				.orElse(16) + 1;
		val maxTop = topData.stream().map(top -> ChatColor.stripColor(top.getTopString()))
				.mapToInt(String::length)
				.max()
				.orElse(5);

		for(val topData : topData) {
			char color = 'e';
			switch (index) {
				case 1 -> color = 'a';
				case 2 -> color = '6';
				case 3 -> color = 'c';
			}

			final TextHoloLine holoLine = hologram.getHoloLine(index + topHeader.size() - 1);
			holoLine.setText("§" + color + "#" + topData.getPosition()
					+ " §8| §f" + topData.getPlayerName() + "§f" + StringUtils.repeat(" ", maxPlayerName - ChatColor.stripColor(topData.getPlayerName()).length())
					+ "§8| §" + color + topData.getTopString() + StringUtils.repeat(" ", maxTop - ChatColor.stripColor(topData.getTopString()).length()));

			index++;
		}
	}
}
