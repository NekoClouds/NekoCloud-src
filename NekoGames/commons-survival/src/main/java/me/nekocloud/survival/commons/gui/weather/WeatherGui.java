package me.nekocloud.survival.commons.gui.weather;

import lombok.val;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import org.bukkit.entity.Player;

public class WeatherGui extends CommonsSurvivalGui<DInventory> {

	public WeatherGui(Player player) {
		super(player);
	}

	@Override
	protected void createInventory() {
		val gamer = GAMER_MANAGER.getGamer(player);
		if (gamer == null)
			return;

		lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createInventory(player, lang.getMessage("COMMONS_GUI_NAME")
                + " ▸ " + lang.getMessage("WEATHER_GUI_NAME"), 5);
	}

	@Override
	public void updateItems() {

	}
}
