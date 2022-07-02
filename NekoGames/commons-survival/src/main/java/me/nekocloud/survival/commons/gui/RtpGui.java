package me.nekocloud.survival.commons.gui;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.nekoapi.utils.core.PlayerUtil;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RtpGui extends CommonsSurvivalGui<DInventory> {

	public RtpGui(Player player) {
		super(player);
	}

	@Override
	protected void createInventory() {
		val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createInventory(player, lang.getMessage("COMMONS_GUI_NAME")
                + " ▸ " + lang.getMessage("RTP_GUI_NAME"), 5);
	}

	@Override
	public void updateItems() {
		val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

		val rtpCooldown = Cooldown.getSecondCooldown(player.getName(), "RTP");
        val rtpPvpCooldown = Cooldown.getSecondCooldown(player.getName(), "RTP_PVP");

		dInventory.setItem(3, 2, new DItem(ItemUtil.getBuilder(Material.ENDER_PEARL)
                .setName(lang.getMessage("RTP_ITEM_NAME"))
                .setLore(rtpCooldown <= 0 ? lang.getList("RTP_ITEM_LORE") :
						lang.getList("RTP_COOLDOWN_ITEM_LORE", TimeUtil.leftTime(lang, rtpCooldown)))
                .build(), (player1, clickType, i) -> {
                    if (rtpCooldown > 0) {
                        gamer.sendMessageLocale("COOLDOWN", TimeUtil.leftTime(lang, rtpCooldown));
                        return;
                    }

                    player1.closeInventory();
                    TeleportingUtil.teleportRandom(player1, Bukkit.getWorld(
							configData.getRtpWorld()),
							configData.getRtpSize());
                    gamer.sendMessageLocale("RTP_TELEPORT",
							player1.getWorld().getName(),
							player1.getLocation().getBlockX(),
							player1.getLocation().getBlockY(),
							player1.getLocation().getBlockZ());
                    Cooldown.addCooldown(player1.getName(), "RTP", 15 * 20);
				}));

		val pvpLore = !(gamer.getGroup().getLevel() < Group.TRIVAL.getLevel()) ?
                lang.getList("RTP_PVP_ITEM_DENY_LORE", Group.TRIVAL.getNameEn()) :
                rtpCooldown <= 0 ? lang.getList("RTP_PVP_ITEM_LORE") :
                lang.getList("RTP_PVP_COOLDOWN_ITEM_LORE", TimeUtil.leftTime(lang, rtpCooldown));

		dInventory.setItem(7, 2, new DItem(ItemUtil.getBuilder(Material.END_CRYSTAL)
                .setName(lang.getMessage("RTP_PVP_ITEM_NAME"))
                .setLore(pvpLore)
                .build(), (player1, clickType, i) -> {
                    if (gamer.getGroup().getLevel() < Group.TRIVAL.getLevel()) {
                        gamer.sendMessageLocale("RTP_PVP_NO_PERMISSIONS", Group.TRIVAL.getNameEn());
                        return;
                    }

                    if (rtpPvpCooldown > 0) {
                        gamer.sendMessageLocale("COOLDOWN", TimeUtil.leftTime(lang, rtpPvpCooldown));
                        return;
                    }

                    player1.closeInventory();
                    if (!TeleportingUtil.teleportPvp(player1, Bukkit.getWorld(
							configData.getRtpWorld()),
							configData.getRtpSize())) {
                        gamer.sendMessageLocale("RTP_PVP_TELEPORT_ERROR");
                        return;
                    }

                    gamer.sendMessageLocale("RTP_PVP_TELEPORT", PlayerUtil.getNearbyPlayers(player1, 100));
                    Cooldown.addCooldown(player1.getName(), "RTP_PVP", 60*20);
                }));
	}
}
