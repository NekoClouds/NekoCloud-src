package me.nekocloud.survival.commons.gui.time;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TimeGui extends CommonsSurvivalGui<DInventory> {

	public TimeGui(Player player) {
		super(player);
	}

	@Override
	protected void createInventory() {
		val gamer = GAMER_MANAGER.getGamer(player);
		if (gamer == null)
			return;

		lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createInventory(player, lang.getMessage("COMMONS_GUI_NAME")
                + " ▸ " + lang.getMessage("TIME_GUI_NAME"), 5);
	}

	@Override
	public void updateItems() {
		val level = configData.getInt("timeCommand");
		val gamer = GAMER_MANAGER.getGamer(player);
		if (gamer == null)
			return;

		dInventory.setItem(3, 3, new DItem(ItemUtil.getBuilder(Material.WATCH)
				.setName(lang.getMessage("TIME_ITEM_WATCH_NAME"))
				.setLore(lang.getList("TIME_ITEM_WATCH_LORE", lang.getMessage("MORNING"),
						Group.getGroupByLevel(level).getName()))
				.build(), (player, clickType, i) -> {
			if (gamer.getGroup().getLevel() >= level) {
				player.getWorld().setTime(0L);
				player.closeInventory();

				gamer.sendMessageLocale("TIME_CHANGE", player.getWorld().getName(),
						lang.getMessage("MORNING"));
			}
		}));

      	dInventory.setItem(5, 3, new DItem(ItemUtil.getBuilder(Material.WATCH)
				.setName(lang.getMessage("TIME_ITEM_WATCH_NAME"))
				.setLore(lang.getList("TIME_ITEM_WATCH_LORE",
						lang.getMessage("DAY"), Group.getGroupByLevel(level).getName()))
				.build(), (player, clickType, i) -> {
			if (gamer.getGroup().getLevel() >= level) {
				player.getWorld().setTime(6000L);
				player.closeInventory();
				gamer.sendMessageLocale("TIME_CHANGE", player.getWorld().getName(),
						lang.getMessage("DAY"));
			}
		}));

      	dInventory.setItem(7, 3, new DItem(ItemUtil.getBuilder(Material.WATCH).setName(lang.getMessage("TIME_ITEM_WATCH_NAME"))
				.setLore(lang.getList("TIME_ITEM_WATCH_LORE",
						lang.getMessage("NIGHT"), Group.getGroupByLevel(level).getName()))
				.build(), (player, clickType, i) -> {
			if (gamer.getGroup().getLevel() >= level) {
				player.getWorld().setTime(18000L);
				player.closeInventory();
				gamer.sendMessageLocale("TIME_CHANGE",
						player.getWorld().getName(),
						lang.getMessage("NIGHT"));
			}

		}));
	}
}
